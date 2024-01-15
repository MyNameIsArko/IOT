import ulogging

ulogging.basicConfig(level=ulogging.INFO)

import machine
import asyncio
import ujson
import os

import esp_request
import esp_crypto
import esp_sensor
import esp_mqtt

log = ulogging.getLogger("BOOT")


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


async def main():
    import esp_wifi
    import esp_bluetooth

    isconnected = False

    log.info("Removing config.json")
    try:
        os.remove("config.json")
    except OSError:
        log.warning("No config.json file found")

    log.info("Starting bluetooth configuration")

    connection = None
    characteristic = None

    while not isconnected:
        if connection is None:
            characteristic = esp_bluetooth.get_characteristic()
            connection = await esp_bluetooth.discover_bluetooth(characteristic)
        data = await esp_bluetooth.read_data(characteristic)
        log.info("Writing data to config.json")
        with open("config.json", "w") as file:
            file.write(data)
        log.info("Trying with new config")
        config = get_config()
        if config is not None:
            isconnected = esp_wifi.connect_to_wifi(config["ssid"], config["password"])

    if connection is not None:
        esp_bluetooth.write_data(characteristic, "CONNECTED")
        esp_bluetooth.disconnect_connection(connection)

    log.info("Registering device")
    api_client = esp_request.APIClient()
    api_client.send_info(config["token"], config["user_id"], config["mac"])

    log.info("Setup encryption")
    encryption = esp_crypto.Encryption(config["aes_key"], config["aes_iv"])

    log.info("Getting sensor")
    sensor = esp_sensor.DHT22()

    log.info("Starting MQTT client")
    mqtt_client = esp_mqtt.Broker(config["mac"], sensor, encryption)
    while True:
        if mqtt_client.connect():
            mqtt_client.start_pushing()
        else:
            await asyncio.sleep(5)


if machine.reset_cause() != machine.SOFT_RESET:
    log.info("Starting parining sequence")
    asyncio.run(main())
