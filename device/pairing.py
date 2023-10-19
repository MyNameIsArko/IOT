import network
import ujson
import usocket

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
        print('Network details not found. Starting access point.')
        # Set up device as access point
        ap = network.WLAN(network.AP_IF)
        ap.active(True)
        ap.config(essid='ESP32-MP', password='filipek', authmode=network.AUTH_WPA_WPA2_PSK)

        # Serve website to input Wi-Fi credentials
        s = usocket.socket()
        s.bind(('0.0.0.0', 80))
        s.listen(1)

        print('Access point IP address:', ap.ifconfig()[0])

        while True:
            conn, addr = s.accept()
            request = conn.recv(1024)

            # Check if request contains Wi-Fi credentials
            if b'ssid=' in request and b'password=' in request:
                # Save Wi-Fi credentials to configuration file
                ssid = request.split(b'ssid=')[1].split(b'&')[0].decode()
                password = request.split(b'password=')[1].split(b'&')[0].decode()
                with open('config.json', 'w') as f:
                    ujson.dump({'ssid': ssid, 'password': password}, f)

                # Connect to Wi-Fi network
                sta = network.WLAN(network.STA_IF)
                sta.active(True)
                sta.connect(ssid, password)
                while not sta.isconnected():
                    pass
                print('Connected to the network')
                print('network config:', sta.ifconfig())

                # Send response to client
                response = """<!DOCTYPE html>
<html>
<head>
    <title>Wi-Fi Credentials</title>
</head>
<body>
    <h1>Wi-Fi Credentials Saved</h1>
    <p>Your Wi-Fi credentials have been saved. You can now close this page and connect to your Wi-Fi network.</p>
</body>
</html>"""
                conn.send(response)
                conn.close()
                break
            else:
                # Send website to client
                response = """<!DOCTYPE html>
<html>
<head>
    <title>Wi-Fi Credentials</title>
</head>
<body>
    <h1>Wi-Fi Credentials</h1>
    <form method="post">
        <label for="ssid">SSID:</label><br>
        <input type="text" id="ssid" name="ssid"><br>
        <label for="password">Password:</label><br>
        <input type="password" id="password" name="password"><br><br>
        <input type="submit" value="Submit">
    </form>
</body>
</html>"""
                conn.send(response)
                conn.close()
    else:
        print('Network details found. Attempting to connect.')
        # Connect to Wi-Fi network
        sta = network.WLAN(network.STA_IF)
        sta.active(True)
        sta.connect(config['ssid'], config['password'])
        while not sta.isconnected():
            pass
        print('Connected to the network')
        print('network config:', sta.ifconfig())