import sys

sys.path.append("")

from micropython import const

import uasyncio as asyncio
import aioble
import ubluetooth
from aioble.core import ble

import random
import struct
import ulogging

log = ulogging.getLogger("BLUETOOTH")

# org.ubluetooth.service.environmental_sensing
_ENV_SENSE_UUID = ubluetooth.UUID(0x181A)
# org.ubluetooth.characteristic.temperature
_ENV_SENSE_TEMP_UUID = ubluetooth.UUID(0x2A6E)
# org.ubluetooth.characteristic.gap.appearance.xml
_ADV_APPEARANCE_GENERIC_THERMOMETER = const(768)

# How frequently to send advertising beacons.
_ADV_INTERVAL_MS = 250_000


# Register GATT server.
def get_characteristic():
    service = aioble.Service(_ENV_SENSE_UUID)
    characteristic = aioble.BufferedCharacteristic(
        service, _ENV_SENSE_TEMP_UUID, notify=True, write=True, read=True, max_len=999, append=True, capture=True
    )
    aioble.register_services(service)
    return characteristic

# Serially wait for connections. Don't advertise while a central is
# connected.
async def discover_bluetooth(characteristic):
    log.info("Advertising ESP32")
    async with await aioble.advertise(
        _ADV_INTERVAL_MS,
        name="ESP32",
        services=[_ENV_SENSE_UUID],
        appearance=_ADV_APPEARANCE_GENERIC_THERMOMETER,
    ) as connection:
        log.info(f"Connection from: {connection.device}")
        characteristic.notify(connection)
        await connection.disconnected()
        return connection
    
async def disconnect_connection(connection):
    log.info(f'Device: {connection.device} disconnected')
    await connection.disconnect()

async def read_data(characteristic):
    log.info("Reading data")
    whole_message = ""
    is_reading = False
    while True:
        # implement reading data from scratch
        conn, data = await characteristic.written()
        # data = ble.gatts_read(characteristic._value_handle)
        if data is None:
            break
        message = str(data, 'utf-8')
        log.info(f'{message=}')
        if message.startswith("START{"):
            whole_message += message[5:]
            is_reading = True
        elif is_reading:
            whole_message += message
        if whole_message.endswith("}END"):
            whole_message = whole_message[:-3]
            break

    log.info("Received whole message")
    log.info(f'{whole_message=}')
    return whole_message


async def write_data(characteristic, message):
    log.info("Sending message")
    data = bytes(message, 'utf-8')
    await characteristic.write(data, send_update=True)