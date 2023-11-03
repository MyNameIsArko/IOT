import dht
import machine

class DHT22:
    def __init__(self):
        self.sensor = dht.DHT22(33)
        print(f'Sensor initialized.\n{self.get_measurement()}')
    
    def get_measurement(self):
        self.sensor.measure()
        return {
            'temperature': self.sensor.temperature(),
            'humidity': self.sensor.humidity()
        }
    
