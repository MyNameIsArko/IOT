import ulogging

ulogging.basicConfig(level=ulogging.INFO)

import machine
import asyncio
import ujson
import gc
import ubinascii
gc.collect()

import esp_request
import esp_crypto
import esp_sensor
import esp_mqtt

log = ulogging.getLogger('BOOT')

def get_config():
    log.info("Reading config.json")
    try:
        with open("config.json", "r") as file:
            config = ujson.load(file)
        
        if 'ssid' not in config or 'password' not in config or 'user_id' not in config or 'mac' not in config or 'token' not in config or 'aes_key' not in config or 'aes_iv' not in config:
            log.warning("Bad config.json file")
            return None
        return config
    except OSError:
        log.warning("No config.json file found")
        return None

async def reset_if_not_exists(api_client, mac, esp_wifi):
    while True:
        await asyncio.sleep(60)
        if not esp_wifi.check_if_connected():
            log.warning("Wifi not connected")
            continue
        log.info("Checking if device is registered")
        if not api_client.check_if_exists(mac):
            log.info("Resetting device")
            machine.reset()

async def check_boot_button_pressed():
    log.info("Listening for boot button")
    boot_button = machine.Pin(0, machine.Pin.IN)
    while True:
        if boot_button.value() == 0:
            log.info("Boot button pressed, resetting device")
            try:
                os.remove("config.json")
            except OSError:
                pass
            machine.reset()

async def main():
    import esp_wifi
    import esp_bluetooth

    asyncio.create_task(check_boot_button_pressed())

    isconnected = False
    encryption = None

    config = get_config()
    if config is not None:
        log.info("Setup encryption")
        encryption = esp_crypto.Encryption(config['aes_key'], config['aes_iv'])
        ssid = encryption.decrypt(ubinascii.a2b_base64(config['ssid'])).decode('utf-8')
        password = encryption.decrypt(ubinascii.a2b_base64(config['password'])).decode('utf-8')
        isconnected = await esp_wifi.connect_to_wifi(ssid, password)


    if not isconnected:
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
                log.info("Setup encryption")
                encryption = esp_crypto.Encryption(config['aes_key'], config['aes_iv'])
                ssid = encryption.decrypt(ubinascii.a2b_base64(config['ssid'])).decode('utf-8')
                password = encryption.decrypt(ubinascii.a2b_base64(config['password'])).decode('utf-8')
                isconnected = await esp_wifi.connect_to_wifi(ssid, password)
    
        esp_bluetooth.write_data(characteristic, "CONNECTED")
        esp_bluetooth.disconnect_connection(connection)
    
    log.info("Registering device")
    api_client = esp_request.APIClient()
    api_client.send_info(config['token'], config['user_id'], config['mac'])

    log.info("Starting 'check if exist' task")
    asyncio.create_task(reset_if_not_exists(api_client, config["mac"], esp_wifi))

    if encryption is None:
        log.info("Setup encryption")
        encryption = esp_crypto.Encryption(config['aes_key'], config['aes_iv'])

    log.info("Getting sensor")
    sensor = esp_sensor.DHT22()

    log.info("Starting MQTT client")
    mqtt_client = esp_mqtt.ESP32MQTTClient(config['mac'], sensor, encryption, config['user_id'])
    while True:
        if mqtt_client.connect():
            mqtt_client.listen_for_disconnect()
            mqtt_client.start_pushing()
        else:
            await asyncio.sleep(5)

        
if machine.reset_cause() != machine.SOFT_RESET:
    log.info("Starting pairing sequence")
    asyncio.run(main())
