# HTTP TCP + JSON RPC + MQTT Robot Controller (Java)

**Current Version:** v2.2 – MoM & LE draft final

**Date:** 08.02.2026

**Ports used:**

- HTTP REST: 1243
- JRPC: 9524
- MQTT: 1883

---

## Project Overview

Central controller for a simulated robot swarm system in a Distributed Systems course. 

- **v0.5** – pure HTTP/1.0 REST server & client using raw TCP sockets (no external libraries) 
- **v1.0** – improved stability, statistics, Docker support, and preparation for RPC 
- **v1.3** – draft of JSON RPC server.
- **v2.0** - improved JSON RPC (robot in a single container), other changes.
- **v2.2** - final draft of the robot swarm with MoM and LE.

---

## Architecture

- **HTTP REST Layer** (`ds.robot.http` package) 
  - `ControllerServer` – listens on port 1243, handles GET/POST requests 
  - `ControllerClient` – test tool that measures RTT and validates responses 
  - `ControllerData` – simple shared state (robotsActive, captain, health)

- **JSON RPC Layer** (`ds.robot.rpc` package) 
  - bidirectional communication robot-controller.
  - `RobotInfo` - POJO class - single robot representation
  - `RobotClient` - functional single robot client with logic
  
- **MQTT Layer** (`ds.robot.mom` package)
  - `MqttClientManager` - handles connection to MQTT controller and messages
  - `LeaderElection` - contains Bully algorithm Leader Election logic

- **Communication** 
  - External client - Controller: HTTP/1.0 over TCP (port 1243) 
  - Controller - Robots: JSON RPC (bidirectional, port 9524)
  - Robot - Robot: MQTT (port 1883)
  
- **Dependancies**
  - Gson: for JSON parsing
  - MQTT: MoM network messaging protocol
  - Mosquitto: message broker implementing MQTT protocol (as container in Docker Compose)
  - Paho: library in Java to communicate with Mosquitto

---

## Supported HTTP Endpoints

- `GET /status` - `{ "robotsActive": 10 }` 
- `GET /captain` - `{ "captain": "R1" }` 
- `GET /health` - `{ "health": "OK" }` 
- `POST /le` - `{ "leCreated": true, ... }`

Any other path - 404 Not Found

Wrong method - 405 Method Not Allowed

---

## MoM Details

**MQTT + Mosquitto + Paho**
**QoS = 1**

- le/heartbeat  -> captain heartbeat
- le/start      -> manual election start
- le/election   -> incoming election messages
- le/ok         -> reply from higher robots
- le/leader     -> new captain announced

---

## TODOs:

- README.md correction.
- JSON parsing via Gson for HTTP TCP.
- commenting the code sufficiently.

---

## How to Build & Run

### via Docker Compose:

```bash 
docker-compose up --build 
```

### Troubleshoot Docker stuck containers:

```bash
docker ps -a
docker rm <container_name_or_id>
```
