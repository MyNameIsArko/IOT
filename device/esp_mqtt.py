from umqttsimple import MQTTClient
import machine
import time
import ubinascii
import ulogging

log = ulogging.getLogger("MQTT")


class Broker:
    def __init__(self, mac, sensor, encryption):
        # dwa topici, jeden temperature  i jeden humidity
        # topic to mac/temperature  mac/humidity
        self.client_id = ubinascii.hexlify(machine.unique_id())
        self.mqtt_ip = "srv3.enteam.pl"
        self.encryption = encryption

        mac = mac.upper()
        self.temperature_topic = f"{mac}/Temperature"
        self.humidity_topic = f"{mac}/Humidity"
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

    def start_pushing(self):
        assert self.client is not None, "MQTT client is not connected"
        log.info("Starting pushing data to MQTT broker")

        last_message = 0
        message_interval = 5

        while True:
            try:
                if (time.time() - last_message) > message_interval:
                    json_measurements = self.sensor.get_measurement()

                    temperature_txt = str(json_measurements["temperature"])
                    temperature_msg = self.encryption.encrypt(temperature_txt)

                    humidity_txt = str(json_measurements["humidity"])
                    humidity_msg = self.encryption.encrypt(humidity_txt)

                    # temperature_encoded = ubinascii.a2b_base64(temperature_msg)
                    # humidity_encoded = ubinascii.a2b_base64(humidity_msg)

                    self.client.publish(self.temperature_topic, temperature_msg)
                    self.client.publish(self.humidity_topic, humidity_msg)
                    log.info("Sent data to MQTT broker")
                    last_message = time.time()
            except OSError:
                log.warning("Failed to push data to MQTT broker. Reconnecting")
