import network
import time
import ulogging

# Set up as wifi
sta = network.WLAN(network.STA_IF)
sta.active(True)

log = ulogging.getLogger("WIFI")


def connect_to_wifi(ssid, password):
    log.info("Connecting to the network")
    sta.connect(ssid, password)
    timer = 0
    while not sta.isconnected():
        if timer > 10:
            log.warning("Wifi doesn't exists or bad credentials.")
            sta.disconnect()
            return False
        time.sleep(1)
        timer += 1
    if sta.isconnected():
        log.info("Connected to the network")
        log.info(f"Network config: {sta.ifconfig()}")
        return True
    return False


def check_if_connected():
    return sta.isconnected()
