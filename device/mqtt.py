from umqttsimple import MQTTClient
import machine
import time
import ubinascii
class Broker:
    def __init__(self, mac, sensor, encryption):
        # dwa topici, jeden temperature  i jeden humidity
        # topic to mac/temperature  mac/humidity
        self.client_id = ubinascii.hexlify(machine.unique_id())
        self.mqtt_ip = 'srv3.enteam.pl'
        self.encryption = encryption

        mac = mac.upper()
        self.temperature_topic = f'{mac}/Temperature'
        self.humidity_topic = f'{mac}/Humidity'
        self.sensor = sensor

        try:
            self.client = self.connect()
        except OSError:
            self.restart_and_reconnect()
    
    def connect(self):
        client = MQTTClient(self.client_id, self.mqtt_ip, port=8883, user="devicePublisher", password="RVbySf#FV8*!xG4&o4j6")
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
                    
                    temperature_txt = str(json_measurements['temperature'])
                    temperature_msg = self.encryption.encrypt(temperature_txt)
             
                    humidity_txt = str(json_measurements['humidity'])
                    humidity_msg = self.encryption.encrypt(humidity_txt)
                    
                    self.client.publish(self.temperature_topic, temperature_msg)
                    self.client.publish(self.humidity_topic, humidity_msg)
                    last_message = time.time()
            except OSError:
                self.restart_and_reconnect()
        