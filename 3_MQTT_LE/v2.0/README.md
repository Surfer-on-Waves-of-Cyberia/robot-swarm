# HTTP + gRPC Robot Controller (Java)

**Current Version:** v1.2 – RPC Draft Final <br>
**Date:** 19.01.2026 <br>
**Ports used:** <br>
- HTTP REST: 1243 <br>
- gRPC: 9524

---

## Project Overview

Central controller for a simulated robot swarm system in a Distributed Systems course. 

- **v0.5** – pure HTTP/1.0 REST server & client using raw TCP sockets (no external libraries) 
- **v1.0** – improved stability, statistics, Docker support, and preparation for RPC 
- **v1.3** – final draft of JSON RPC server.

---

## Architecture

- **HTTP REST Layer** (`ds.robot.http` package) 
  - `ControllerServer` – listens on port 1243, handles GET/POST requests 
  - `ControllerClient` – test tool that measures RTT and validates responses 
  - `ControllerData` – simple shared state (robotsActive, captain, health)

- **gRPC / RPC Layer** (`ds.robot.rpc` package) 
  - Generated from `proto/robot.proto` using `make generate` 
  - Messages: `LoginRequest`, `LoginResponse`, `HeartbeatRequest`, `HeartbeatAck` 
  - Service: `RobotService` with `Login` and `Heartbeat` RPCs

- **Communication** 
  - External client - Controller: HTTP/1.0 over TCP (port 1243) 
  - Future: Controller - Robots: gRPC (bidirectional, planned port 9524)

---

## Supported HTTP Endpoints (unchanged from v0.5)

- `GET /status` - `{ "robotsActive": 5 }` 
- `GET /captain` - `{ "captain": "King" }` 
- `GET /health` - `{ "health": "OK" }` 
- `POST /le` - `{ "leCreated": true, ... }` (leader election trigger – dummy for now)

Any other path - 404 Not Found <br>
Wrong method - 405 Method Not Allowed

---

## Version History

**v0.1 – Raw TCP** <br>
Basic server & client with one-line messages.

**v0.2 – First HTTP Parsing** <br>
Manual request line, headers, Content-Length body.

**v0.3 – Routing & Data** <br>
Switch-case routing, introduced `ControllerData`.

**v0.4 – Performance Testing** <br>
RTT measurement, statistical validation (mean deviation, confidence level).

**v0.5 – Final HTTP** (08.12.2025) <br>
Removed keep-alive, fixed hangs, Docker support.

**v1.0 – Preparation & Cleanup** (19.12.2025) <br>
Extended stats, clean shutdown, README fixes.

**v1.1 – RPC Infrastructure** (10.01.2026) <br>
- Added gRPC toolchain & dependencies <br>
- Defined & generated Login + Heartbeat RPC interface <br>
- Clean package structure: `ds.robot.http` + `ds.robot.rpc` <br>
- Working build system (`make generate`, `make compile`, `make run-server`, `make run-client`) <br>
- Verified protobuf messages with `TestProto`

---

## How to Build & Run

### Local Development:

```bash
# 1. Generate gRPC Java classes from proto/robot.proto
make generate

# 2. Compile HTTP + generated gRPC code
make compile

# 3. Start HTTP REST server (port 1243)
make run-server

# 4. In a new terminal – start robot simulation
make run-robot-simulation

# 5. In another new terminal – run HTTP test client (for LE)
make run-client
```

### via Docker Compose:

```bash 
docker-compose up --build 
```

### Troubleshoot stuck containers:

```bash
docker ps -a
docker rm <container_name_or_id>
```