# IoT Application System

This is an IoT application system that utilizes an ESP-32 device equipped with a temperature and humidity detector, a REST API server, MQTT server and databases. Servers and databases operate within Docker containers and communicate over a network. The system ensures secure connections over HTTPS and MQTTS, encrypting data with symmetrical encryption. Authentication is handled via JWT tokens for users, while the device requires client certificates and credentials to connect to the MQTTS server.

## Features

- Continuous data transmission from the ESP-32 device to the HTTP server via MQTT.
- Secure connections using HTTPS and MQTTS.
- Symmetrical encryption for data transmission.
- JWT token authentication for users.
- Client certificates and credentials required for device authentication.
- Real-time updates of humidity and temperature data, fetched from the REST API server

## Technologies Used

- ESP-32 device (Micropython)
- REST API server (C#)
- MQTT server (Mosquitto)
- User application (Kotlin)
- Symmetrical encryption
- JWT token authentication
- HTTPS and MQTTS
- PostgreSQL
- Docker