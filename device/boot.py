import machine
import esp_bluetooth
import asyncio
import ulogging

ulogging.basicConfig(level=ulogging.INFO)

log = ulogging.getLogger('BOOT')

async def main():
    import pairing
    # Soft reset doesn't restart WLAN
    isconnected = pairing.enter_pairing()
    connection = None
    characteristic = None
    if not isconnected:
        log.info("Starting bluetooth configuration")
    while not isconnected:
        if not isconnected:
            if connection is None:
                characteristic = esp_bluetooth.get_characteristic()
                connection = await esp_bluetooth.discover_bluetooth(characteristic)
            data = await esp_bluetooth.read_data(characteristic)
            log.info("Writing data to config.json")
            with open("config.json", "w") as file:
                file.write(data)
            log.info("Trying with new credentials")
            isconnected = pairing.enter_pairing()
    
    if connection is not None:
        esp_bluetooth.write_data(characteristic, "CONNECTED")
        esp_bluetooth.disconnect_connection(connection)
        
if machine.reset_cause() != machine.SOFT_RESET:
    log.info("Starting parining sequence")
    asyncio.run(main())