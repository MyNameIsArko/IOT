from umqttsimple import MQTTClient
import machine
import asyncio
import ubinascii
import ulogging

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
        self.sensor = sensor

        self.client = None

    def connect(self):
        log.info("Trying to connect to MQTT broker")
        client = MQTTClient(
            self.client_id,
            self.mqtt_ip,
            port=883,
            user="devicePublisher",
            password="RVbySf#FV8*!xG4&o4j6",
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

    # Listen for disconnect message and if present remove config and restart device
    def listen_for_disconnect(self):
        assert self.client is not None, "MQTT client is not connected"
        log.info("Listening for disconnect message")
        self.client.set_callback(self.disconnect_callback)
        self.client.subscribe(self.disconnect_topic)

    def disconnect_callback(self, topic, msg):
        log.info("Disconnect message received")
        if str(msg, "utf-8") == self.client_id:
            log.info("Removing config and restarting device")
            try:
                os.remove("config.json")
            except OSError:
                pass
            machine.reset()
