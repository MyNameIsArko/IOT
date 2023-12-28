import network
import ujson
import os
import time
import ulogging

# Set up as wifi
sta = network.WLAN(network.STA_IF)
sta.active(True)

log = ulogging.getLogger('PAIRING')

def enter_pairing():
    log.info('Starting connecting to network')
    # Load Wi-Fi credentials from configuration file
    try:
        with open('config.json', 'r') as f:
            config = ujson.load(f)
    except OSError:
        config = {}

    # Check if Wi-Fi credentials are present
    if 'ssid' not in config or 'password' not in config:
        log.warning('Network details not found')
        return False
    else:
        log.info('Network details found. Attempting to connect')
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
        log.info('Connected to the network')
        log.info('Network config:', sta.ifconfig())
        return True
    else:
        log.warning("Wifi doesn't exists or bad credentials. Trying again")
        return False