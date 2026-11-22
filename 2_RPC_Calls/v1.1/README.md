# HTTP + gRPC Robot Controller (Java)

**Current Version:** v1.1 – RPC Infrastructure  
**Ports used:**  
- HTTP REST: 1243  
- gRPC (planned): 9524  

**Date:** January 2026  
**Location:** Fulda, Germany

## Project Overview

Central controller for a simulated robot swarm system in a Distributed Systems course.  

- **v0.5** – pure HTTP/1.0 REST server & client using raw TCP sockets (no external libraries)  
- **v1.0** – improved stability, statistics, Docker support, and preparation for RPC  
- **v1.1** – added gRPC communication layer (build system, code generation, packages, protobuf messages)

The HTTP part remains zero-dependency. The gRPC part uses external jars (grpc-*, protobuf-java, guava) for protocol buffers, transport, and futures — this is the standard way to use gRPC in Java.

## Architecture (v1.1)

- **HTTP REST Layer** (`ds.robot.http` package)  
  - `ControllerServer` – listens on port 1243, handles GET/POST requests  
  - `ControllerClient` – test tool that measures RTT and validates responses  
  - `ControllerData` – simple shared state (robotsActive, captain, health)

- **gRPC / RPC Layer** (`ds.robot.rpc` package)  
  - Generated from `proto/robot.proto` using `make generate`  
  - Messages: `LoginRequest`, `LoginResponse`, `HeartbeatRequest`, `HeartbeatAck`  
  - Service: `RobotService` with `Login` and `Heartbeat` RPCs

- **Communication**  
  - External client ↔ Controller: HTTP/1.0 over TCP (port 1243)  
  - Future: Controller ↔ Robots: gRPC (bidirectional, planned port 9524)

## Supported HTTP Endpoints (unchanged from v0.5)

- `GET /status` → `{ "robotsActive": 5 }`  
- `GET /captain` → `{ "captain": "King" }`  
- `GET /health` → `{ "health": "OK" }`  
- `POST /le` → `{ "leCreated": true, ... }` (leader election trigger – dummy for now)

Any other path → 404 Not Found  
Wrong method → 405 Method Not Allowed

## Version History

**v0.1 – Raw TCP**  
Basic server & client with one-line messages.

**v0.2 – First HTTP Parsing**  
Manual request line, headers, Content-Length body.

**v0.3 – Routing & Data**  
Switch-case routing, introduced `ControllerData`.

**v0.4 – Performance Testing**  
RTT measurement, statistical validation (mean deviation, confidence level).

**v0.5 – Final HTTP** (08.12.2025)  
Removed keep-alive, fixed hangs, Docker support.

**v1.0 – Preparation & Cleanup** (19.12.2025)  
Extended stats, clean shutdown, README fixes.

**v1.1 – RPC Infrastructure** (January 2026)  
- Added gRPC toolchain & dependencies  
- Defined & generated Login + Heartbeat RPC interface  
- Clean package structure: `ds.robot.http` + `ds.robot.rpc`  
- Working build system (`make generate`, `make compile`, `make run-server`, `make run-client`)  
- Verified protobuf messages with `TestProto`

**RPC framework choice**  
We chose **gRPC** for its popularity, strong typing via `.proto`, code generation, HTTP/2 efficiency, and excellent documentation.  
Trade-off: requires several external jars. Pure Java alternatives (custom binary over TCP) were considered but rejected due to high implementation effort and error risk.

A clean implementation of a tiny HTTP-like server and client in pure Java (no external libraries).
The server acts as a central controller in a robot swarm system and exposes simple status endpoints.
The client is a testing tool that measures round-trip time (RTT) and performs statistical validation.


### How to Build & Run

#### Option 1: *Run Locally*

```bash
# 1. Generate gRPC Java classes from proto/robot.proto
make generate

# 2. Compile HTTP + generated gRPC code
make compile

# 3. Start HTTP REST server (port 1243)
make run-server

# 4. In a new terminal – run HTTP test client
make run-client
```

#### Option 2: *Run in Container via Docker-Compose*

```bash
# Build Docker images and run them
docker-compose up --build
```

#### *Docker Troubleshooting*:

```bash
docker ps -a

docker rm [container-id]
```