from umqttsimple import MQTTClient
import machine
import time

class Broker:
    def __init__(self, mac, sensor):
        # dwa topici, jeden temperature  i jeden humidity
        # topic to mac/temperature  mac/humidity
        self.client_id = ubinascii.hexlify(machine.unique_id())
        self.mqtt_ip = 'TO GET'

        self.temperature_topic = f'{mac}/temperature'
        self.humidity_topic = f'{mac}/humidity'
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
                    temperature_msg = str.encode(json_measurements['temperature'])
                    humidity_msg = str.encode(json_measurements['humidity'])
                    self.client.publish(self.temperature_topic, temperature_msg)
                    self.client.publish(self.humidity_topic, humidity_msg)
                    last_message = time.time()
            except OSError:
                self.restart_and_reconnect()
        