import network
import asyncio
import ulogging

# Set up as wifi
sta = network.WLAN(network.STA_IF)

log = ulogging.getLogger("WIFI")


async def connect_to_wifi(ssid, password):
    log.info("Connecting to the network")
    sta.active(True)
    await asyncio.sleep(1)
    log.info(f"'{ssid}', '{password}'")
    sta.connect(ssid, password)
    timer = 0
    while not sta.isconnected():
        if timer > 10:
            log.warning("Wifi doesn't exists or bad credentials.")
            sta.disconnect()
            sta.active(False)
            await asyncio.sleep(1)
            return False
        await asyncio.sleep(1)
        timer += 1
    if sta.isconnected():
        log.info("Connected to the network")
        log.info(f"Network config: {sta.ifconfig()}")
        return True
    return False
