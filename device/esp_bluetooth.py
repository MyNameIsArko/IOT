import sys

sys.path.append("")

from micropython import const

import uasyncio as asyncio
import aioble
import ubluetooth
from aioble.core import ble

import random
import struct

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
    characteristic = aioble.Characteristic(
        service, _ENV_SENSE_TEMP_UUID, notify=True, write=True
    )
    aioble.register_services(service)
    return characteristic

# Serially wait for connections. Don't advertise while a central is
# connected.
async def discover_bluetooth(characteristic):
    while True:
        async with await aioble.advertise(
            _ADV_INTERVAL_MS,
            name="ESP32",
            services=[_ENV_SENSE_UUID],
            appearance=_ADV_APPEARANCE_GENERIC_THERMOMETER,
        ) as connection:
            print("Connection from", connection.device)
            characteristic.notify(connection)
            await connection.disconnected()
            return connection
    
async def disconnect_connection(connection):
    await connection.disconnected()

async def read_data(characteristic):
    is_reading = False
    whole_message = ""
    while True:
        print("WRITTEN")
        await characteristic.written()
        print("READ1")
        # try:
        # data = characteristic.read()

        # implement reading data from scratch
        data = ble.gatts_read(characteristic._value_handle)

        # except Exception as e:
        #     print(e)
        print("READ2")
        # if data is None:
        #     continue
        print(f'{data=}')
        message = struct.unpack("<h", data) [0]
        print(f'{message=}')
        if message == "END":
            break
        elif message == "START":
            is_reading = True
            continue
        if is_reading:
            whole_message += message
        # data = await characteristic.read()

    json_message = ujson.loads(whole_message)
    return json_message


async def write_data(characteristic, message):
    data = struct.pack("<h", message)
    await characteristic.write()