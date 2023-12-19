import network
import ujson
import usocket
import os
import time
import picoweb
from picoweb.utils import parse_qs

# Set up as wifi
sta = network.WLAN(network.STA_IF)
sta.active(True)

def enter_pairing():
    print('Starting connecting to network')
    # Load Wi-Fi credentials from configuration file
    try:
        with open('config.json', 'r') as f:
            config = ujson.load(f)
    except OSError:
        config = {}

    # Check if Wi-Fi credentials are present
    if 'ssid' not in config or 'password' not in config:
        print('Network details not found. Starting access point...')
        return False
    else:
        print('Network details found. Attempting to connect...')
        # Connect to Wi-Fi network
        return connect_to_wifi(config['ssid'], config['password'])
    
def connect_to_wifi(ssid, password):

    sta.connect(ssid, password)
    timer = 0
    while not sta.isconnected():
        if timer > 10:
            os.remove('config.json')
            break
        time.sleep(1)
        timer += 1
    if sta.isconnected():
        print('Connected to the network')
        print('network config:', sta.ifconfig())
        return True
    else:
        print("Wifi doesn't exists or bad credentials. Try again...")
        return False