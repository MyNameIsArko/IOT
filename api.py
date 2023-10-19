import urequests, os
from pairing import enter_pairing_mode
from machine import I2C
from bmp280 import *


class APIClient:
    key = None
    baseURL = None

    hardware_pin = machine.Pin(33)
    sensor = None

    def __init__(self, baseURL="https://iotapi.k00l.net/bareboard"):
        self.baseURL = baseURL
        # if not ('key' in os.listdir()):
        #     print("NO KEYFILE ON DEVICE!!")
        #     raise Exception("NO KEYFILE FOUND ON DEVICE")
        # with open("key", "r") as keyfile:
        #     self.key = keyfile.read()

        bus = I2C(scl=22, sda=21)
        bmp = BMP280(bus)

        bmp.use_case(BMP280_CASE_WEATHER)
        bmp.oversample(BMP280_OS_HIGH)

        bmp.temp_os = BMP280_TEMP_OS_8
        bmp.press_os = BMP280_PRES_OS_4

        bmp.standby = BMP280_STANDBY_250
        bmp.iir = BMP280_IIR_FILTER_2

        bmp.spi3w = BMP280_SPI3W_ON

        bmp.normal_measure()

        self.bmp = bmp

        print(f"API client initialized, temp: {self.bmp.temperature}, pressure: {self.bmp.pressure}")

    def request(self, method, url, json=None):
        # headers = {"Authorization": f"Bearer {self.key}"}
        return urequests.request(method,
                                 f"{self.baseURL}{url}",
                                 json=json)
                                #  headers=headers)

    def post(self, url, json=None):
        return self.request("POST", url, json=json)

    def get(self, url, json=None):
        return self.request("GET", url, json=json)

    def post_heartbeat(self):
        req = self.post("/heartbeat")
        req.close()

    def get_hardware_measurement(self):
        return {
            "temperature": self.bmp.temperature,
            "pressure": self.sensor.pressure
        }

    def post_measurement(self):
        measurement = self.get_hardware_measurement()
        req = self.post("/measurement", json=measurement)
        res = None
        if req.status_code == 201:
            res = req.json()
            print(f"Measurement posted: {res}")
        elif req.status_code == 200:
            print(f"No active laundry session {measurement}")
        req.close()
        return res

    def register_device(self, ownerId):
        payload = {"ownerId": ownerId}
        req = self.post("/register", json=payload)
        req.close()

    def get_me(self):
        try:
            req = self.get("/me")
            json = req.json()
            return json
        except:
            return None

    def get_owner(self):
        req = self.get("/owner")
        if req.status_code == 404:
            return None
        json = req.json()
        req.close()
        return json


api = APIClient()