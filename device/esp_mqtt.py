from umqttsimple import MQTTClient
import machine
import asyncio
import ubinascii
import ulogging
import os

log = ulogging.getLogger("MQTT")


class ESP32MQTTClient:
    def __init__(self, mac, sensor, encryption, user_id):
        # dwa topici, jeden temperature  i jeden humidity
        # topic to mac/temperature  mac/humidity
        self.client_id = ubinascii.hexlify(machine.unique_id())
        self.mqtt_ip = "srv9.enteam.pl"
        self.encryption = encryption
        self.user_id = user_id

        mac = mac.upper()
        self.temperature_topic = f"{mac}/Temperature"
        self.humidity_topic = f"{mac}/Humidity"
        self.disconnect_topic = f"{mac}/Disconnect"
        self.disconnect_user_topic = f"{mac}/DisconnectUser"
        self.sensor = sensor

        with open("ca.crt", "r") as file:
            self.cert_data = file.read()

        with open("client.key", "r") as file:
            self.client_key = file.read()

        with open("client.crt", "r") as file:
            self.client_cert = file.read()

        self.client = None

    def connect(self):
        log.info("Trying to connect to MQTT broker")
        client = MQTTClient(
            self.client_id,
            self.mqtt_ip,
            port=883,
            user="devicePublisher",
            password="RVbySf#FV8*!xG4&o4j6",
            # ssl=True,
            # ssl_params={
            #     "cert": self.cert_data,
            #     "key": self.client_key,
            #     "ca_certs": self.client_cert,
            # },
        )
        try:
            client.connect()
            log.info(f"Connected to {self.mqtt_ip} MQTT broker.")
            self.client = client
            return True
        except OSError:
            log.warning("Failed to connect to MQTT broker. Reconnecting")
            return False

    async def start_pushing(self):
        assert self.client is not None, "MQTT client is not connected"
        log.info("Starting pushing data to MQTT broker")

        while True:
            try:
                json_measurements = self.sensor.get_measurement()

                temperature_txt = str(json_measurements["temperature"]).encode("utf-8")
                temperature_msg = self.encryption.encrypt(temperature_txt)

                humidity_txt = str(json_measurements["humidity"]).encode("utf-8")
                humidity_msg = self.encryption.encrypt(humidity_txt)

                self.client.publish(self.temperature_topic, temperature_msg)
                self.client.publish(self.humidity_topic, humidity_msg)
                log.info("Sent data to MQTT broker")
                await asyncio.sleep(5)
            except OSError:
                log.warning("Failed to push data to MQTT broker. Reconnecting")
                self.client = None
                break

    # Listen for disconnect message and if present remove config and restart device
    async def listen_for_disconnect(self):
        assert self.client is not None, "MQTT client is not connected"
        log.info("Listening for disconnect message")
        self.client.set_callback(self.disconnect_callback)
        self.client.subscribe(self.disconnect_topic)
        while True:
            try:
                self.client.check_msg()
                await asyncio.sleep(1)
            except OSError:
                log.warning("Failed to listen for disconnect message. Reconnecting")
                self.client = None
                break

    def disconnect_callback(self, topic, msg):
        log.info("Disconnect message received")
        log.info("Removing config and restarting device")
        try:
            os.remove("config.json")
        except OSError:
            pass
        machine.reset()

    def send_disconnect(self):
        assert self.client is not None, "MQTT client is not connected"
        log.info("Sending disconnect message")
        self.client.publish(self.disconnect_user_topic, self.user_id)
        self.client.disconnect()
