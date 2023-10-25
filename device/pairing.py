import network
import ujson
import usocket
import os
from time import sleep

# Set up device as access point
ap = network.WLAN(network.AP_IF)
ap.config(essid='ESP32-MP', password='filipek123', authmode=network.AUTH_WPA_WPA2_PSK)

# Set up as wifi
sta = network.WLAN(network.STA_IF)

# Serve website to input Wi-Fi credentials
s = usocket.socket()
s.bind(('0.0.0.0', 80))
s.listen(1)

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
        setup_AP()
    else:
        print('Network details found. Attempting to connect...')
        # Connect to Wi-Fi network
        connect_to_wifi(config['ssid'], config['password'])

def setup_AP():
    if sta.active():
        sta.active(False)
        sleep(1)

    if not ap.active():
        ap.active(True)
        sleep(1)

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
            print('Trying to connect to wifi with provided details...')
            connect_to_wifi(ssid, password)
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

def connect_to_wifi(ssid, password):
    if ap.active():
        ap.active(False)
        sleep(1)
    if not sta.active():
        sta.active(True)
        sleep(1)
    sta.connect(ssid, password)
    timer = 0
    while not sta.isconnected():
        if timer > 10:
            os.remove('config.json')
            break
        sleep(1)
        timer += 1
    if sta.isconnected():
        print('Connected to the network')
        print('network config:', sta.ifconfig())
    else:
        print("Wifi doesn't exists or bad credentials. Try again...")
        setup_AP()