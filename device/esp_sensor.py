import dht
import machine
import ulogging

log = ulogging.getLogger('SENSOR')

class DHT22:
    def __init__(self):
        self.sensor = dht.DHT22(33)
        log.info('Sensor initialized.')
        self.get_measurement()
    
    def get_measurement(self):
        log.info('Getting measurement')
        self.sensor.measure()
        return {
            'temperature': self.sensor.temperature(),
            'humidity': self.sensor.humidity()
        }
    
