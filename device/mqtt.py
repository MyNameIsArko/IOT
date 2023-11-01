from umqttsimple import MQTTClient
import ubinascii
import ujson
import machine
import time

class Broker:
    def __init__(self, mqtt_ip, topic_to_push, sensor):
        self.client_id = ubinascii.hexlify(machine.unique_id())
        self.mqtt_ip = mqtt_ip

        self.topic = topic_to_push
        self.sensor = sensor

        try:
            self.client = self.connect()
        except OSError:
            self.restart_and_reconnect()
    
    def connect(self):
        client = MQTTClient(self.client_id, self.mqtt_ip)
        client.connect()
        print(f'Connected to {self.mqtt_ip} MQTT broker.')
        return client

    def restart_and_reconnect(self):
        print('Failed to connect to MQTT broker. Reconnecting...')
        time.sleep(10)
        machine.reset()

    def start_pushing(self):
        last_message = 0
        message_interval = 5

        while True:
            try:
                if (time.time() - last_message) > message_interval:
                    json_measurements = self.sensor.get_measurement()
                    msg = str.encode(ujson.dumps(json_measurements))
                    self.client.publish(self.topic, msg)
                    last_message = time.time()
            except OSError:
                self.restart_and_reconnect()
        