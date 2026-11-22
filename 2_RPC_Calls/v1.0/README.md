# HTTP + gRPC Robot Controller (Java)
**Current Version:** v1.1 (RPC preparation)
**Ports:**
- HTTP REST: 1243
- gRPC (planned): 9524
**Date:** January 2026

---

### Project Overview
A clean implementation of a tiny HTTP-like server and client in pure Java (no external libraries).
The server acts as a central controller in a robot swarm system and exposes simple status endpoints.
The client is a testing tool that measures round-trip time (RTT) and performs statistical validation.
Central controller for a robot swarm system. <br>
- **v0.5** – pure HTTP/1.0 REST server & client (before first checkout) <br>
- **v1.0** - pure HTTP server & client with improvements (after first checkout) <br>
- **v1.1** – added gRPC communication channel for robot registration & heartbeats <br>


### Version History

**v0.1 – Raw TCP** <br>
Basic TCP server & client, one-line messages.

**v0.2 – First HTTP Parsing** <br>
Manual parsing of request line, headers, Content-Length body.

**v0.3 – Routing & Data** <br>
Switch-case routing, ControllerData class.

**v0.4 – Performance Testing** <br>
RTT measurement, statistical validation.

**v0.5 – Final HTTP** <br>
Removed keep-alive, fixed hangs, Docker support.

**v1.0 – Preparation & Cleanup** <br>
Extended stats, clean shutdown, README fixes, choice of RPC framework documented.


**v1.1 – RPC Basics** <br>

- Added gRPC toolchain & dependencies 
- Defined & generated Login + Heartbeat RPC interface
- Project restructured (in package): `src/http/` (REST) vs `src/rpc/` (gRPC generated)
- Working build & run system (`make generate`, `make compile`)


### Architecture (v1.1)

- **HTTP part** (`src/http/`)
  - ControllerServer – REST endpoints (/status, /captain, /health, /le)
  - ControllerClient – testing tool with RTT measurement
  - ControllerData – shared state (robotsActive, captain, health)

- **gRPC / RPC part** (`src/rpc/`)
  - Generated from `proto/robot.proto`
  - Contains messages: LoginRequest, LoginResponse, HeartbeatRequest, HeartbeatAck
  - Contains service: RobotService (Login + Heartbeat RPCs)

- **Communication**
  - Client - Controller: HTTP/1.0 over TCP (port 1243)
  - Controller - Robots: gRPC (planned port 50051, bidirectional)


### Supported Endpoints (HTTP – unchanged from v0.5)

GET /status → `{ "robotsActive": 5 }` 
GET /captain → `{ "captain": "King" }`
GET /health → `{ "health": "OK" }`
POST /le → `{ "leCreated": true, ... }`


### Version History

**v0.5 – Final HTTP** (08.12.2025)
- Raw TCP + manual HTTP/1.0 parsing
- RTT benchmarking & statistical validation
- Docker support

**v1.1 – RPC Basics** (January 2026)
- Added gRPC toolchain & dependencies
- Defined & generated Login + Heartbeat RPC interface
- Project restructuring: `src/http/` vs `src/rpc/`
- Full working build system (`make generate`, `make compile`)


### How to Build & Run (v1.1)

#### Option 1: *Run Locally*

```bash
# 1. Generate gRPC Java classes from .proto
make generate

# 2. Compile HTTP + generated gRPC code
make compile

# 3. Run the HTTP REST server (port 1243)
make run-server

# 4. In a new terminal – run HTTP test client
make run-client
```

#### Option 2: *Run in Container via Docker-Compose*

```bash
# Build the Docker images and Start Server & Client Containers
docker-compose up --build
```

#### *Docker Troubleshooting*:

```bash
docker ps -a

docker rm [container-id]
```