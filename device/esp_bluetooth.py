import sys

sys.path.append("")

from micropython import const

import uasyncio as asyncio
import aioble
import ubluetooth
import aioble.core as core

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

_BMS_MTU = const(256)

core.config(
    mtu=_BMS_MTU,
)
# core.ble.gattc_exchange_mtu(_BMS_MTU)


# Register GATT server.
def get_characteristic():
    service = aioble.Service(_ENV_SENSE_UUID)
    characteristic = aioble.BufferedCharacteristic(
        service,
        _ENV_SENSE_TEMP_UUID,
        notify=True,
        write=True,
        read=True,
        max_len=20,
        append=True,
        capture=True,
        write_no_response=True,
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
    log.info(f"Device: {connection.device} disconnected")
    await connection.disconnect()


async def read_data(characteristic, decryption):
    log.info("Reading data")
    whole_message = ""
    is_reading = True
    while True:
        # implement reading data from scratch
        conn, data = await characteristic.written()
        # data = ble.gatts_read(characteristic._value_handle)
        if data is None:
            break
        message = data.decode("utf-8")
        log.info(f"{message=}")
        if message.startswith("S{"):
            whole_message += message
            is_reading = True
        elif is_reading:
            whole_message += message
        if whole_message.endswith("}E"):
            break

    log.info("Received whole message")
    log.info(f"{whole_message=}")
    whole_message = (
        whole_message.replace("S{", "").replace("}E", "").replace("\x00", "")
    )
    message_decrypted = decryption.decrypt(whole_message)
    message_after_split = message_decrypted.split(",")
    json_config = {}
    json_config["ssid"] = message_after_split[0]
    json_config["password"] = message_after_split[1]
    json_config["user_id"] = message_after_split[2]
    json_config["mac"] = message_after_split[3]
    json_config["token"] = message_after_split[4]
    json_config["aes_key"] = message_after_split[5]
    json_config["aes_iv"] = message_after_split[6]
    log.info(f"After parsing: {json_config}")
    ubluetooth.BLE().active(False)
    await asyncio.sleep(1)
    return json_config


async def write_data(characteristic, message):
    log.info("Sending message")
    data = bytes(message, "utf-8")
    await characteristic.write(data, send_update=True)


async def turn_bluetooth():
    core.ensure_active()
    await asyncio.sleep(1)
