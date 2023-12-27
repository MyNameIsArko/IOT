import machine
import esp_bluetooth
import asyncio

async def main():
    import pairing
    # Soft reset doesn't restart WLAN
    isconnected = pairing.enter_pairing()
    connection = None
    characteristic = None
    while not isconnected:
        if not isconnected:
            if connection is None:
                characteristic = esp_bluetooth.get_characteristic()
                connection = await esp_bluetooth.discover_bluetooth(characteristic)
            data = await esp_bluetooth.read_data(characteristic)
            with open("config.json", "w") as file:
                file.write(data)
            isconnected = pairing.enter_pairing()
    
    if connection is not None:
        esp_bluetooth.write_data(characteristic, "connected")
        esp_bluetooth.disconnect_connection(connection)
        
if machine.reset_cause() != machine.SOFT_RESET:
    asyncio.run(main())