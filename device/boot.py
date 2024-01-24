import ulogging

ulogging.basicConfig(level=ulogging.INFO)

import machine
import asyncio
import ujson
import gc
import ubinascii
import micropython
import esp_request

gc.collect()

# import esp_request
import esp_crypto
import esp_sensor
import esp_mqtt
import os

log = ulogging.getLogger("BOOT")
mqtt_client = None


def get_config():
    log.info("Reading config.json")
    try:
        with open("config.json", "r") as file:
            config = ujson.load(file)

        if (
            "ssid" not in config
            or "password" not in config
            or "user_id" not in config
            or "mac" not in config
            or "token" not in config
            or "aes_key" not in config
            or "aes_iv" not in config
        ):
            log.warning("Bad config.json file")
            return None
        return config
    except OSError:
        log.warning("No config.json file found")
        return None


def reset_button_handler(pin):
    log.info("Boot button pressed, resetting device")
    if mqtt_client is not None:
        mqtt_client.send_disconnect()
    try:
        os.remove("config.json")
    except OSError:
        pass
    machine.reset()


async def main():
    global mqtt_client

    import esp_wifi

    log.info("Listening for boot button")
    micropython.alloc_emergency_exception_buf(100)
    boot_button = machine.Pin(0, machine.Pin.IN)
    boot_button.irq(handler=reset_button_handler, trigger=machine.Pin.IRQ_FALLING)

    isconnected = False

    config = get_config()
    if config is not None:
        isconnected = await esp_wifi.connect_to_wifi(config["ssid"], config["password"])

    if not isconnected:
        log.info("Starting bluetooth configuration")

        connection = None
        characteristic = None

        log.info("Setup decryption")
        decryption = esp_crypto.Decryption()

        while not isconnected:
            import esp_bluetooth

            await esp_bluetooth.turn_bluetooth()
            # if connection is None:
            characteristic = esp_bluetooth.get_characteristic()
            connection = await esp_bluetooth.discover_bluetooth(characteristic)
            data = await esp_bluetooth.read_data(characteristic, decryption)
            esp_bluetooth.disconnect_connection(connection)
            connection = None
            log.info("Writing data to config.json")
            with open("config.json", "w") as file:
                ujson.dump(data, file)
            log.info("Trying with new config")
            config = get_config()
            if config is not None:
                isconnected = await esp_wifi.connect_to_wifi(
                    config["ssid"], config["password"]
                )

        # esp_bluetooth.write_data(characteristic, "CONNECTED")
        # esp_bluetooth.disconnect_connection(connection)

    log.info("Registering device")
    api_client = esp_request.APIClient()
    api_client.send_info(config["token"], config["user_id"], config["mac"])

    log.info("Setup encryption")
    encryption = esp_crypto.Encryption(config["aes_key"], config["aes_iv"])

    log.info("Getting sensor")
    sensor = esp_sensor.DHT22()

    log.info("Starting MQTT client")
    mqtt_client = esp_mqtt.ESP32MQTTClient(
        config["mac"], sensor, encryption, config["user_id"]
    )
    while True:
        if mqtt_client.connect():
            asyncio.create_task(mqtt_client.listen_for_disconnect())
            await mqtt_client.start_pushing()
        else:
            await asyncio.sleep(5)


if machine.reset_cause() != machine.SOFT_RESET:
    log.info("Starting pairing sequence")
    asyncio.run(main())
