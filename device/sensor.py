import dht

class DHT22:
    def __init__(self, hardware_pin):
        self.sensor = dht.DHT22(hardware_pin)
        self.sensor.measure()
        print(f'Sensor initialized.\n{self.get_measurement()}')
    
    def get_measurement(self):
        self.sensor.measure()
        return {
            'temperature': self.sensor.temperature(),
            'humidity': self.sensor.humidity()
        }
    
