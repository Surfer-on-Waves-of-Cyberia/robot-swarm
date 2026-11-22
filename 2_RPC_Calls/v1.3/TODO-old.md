
# TODO List – Task 2: RPC  
  

**Deadline: 19.01.2026**
  

### Goals of Task 2  

- Keep existing HTTP REST controller (client <-> controller via sockets/HTTP)
- Add RPC-based communication between **controller <-> robots** (gRPC or Thrift)
- Robots register at controller on startup
- Controller can query each robot’s status/health 
- Controller can trigger a leader election 
- No piggybacking (heartbeat must be a separate RPC call) 
 

---  
  

## Version 1.0 – Preparation & Cleanup (by 19.12.2025)  

- [x] Extended statistical validation (mean deviation, confidence level, number on repeatings) 
- [x] Ensure HTTP server from Task 1 can shut down cleanly.
- [x] README.md correction (docker ps -a, docker rm ..)
- [x] Write short note on choice of RPC framework.
- [x] Tag `v1.0`
  
**Further Changes**: sendRequest Optimization, Readme Cleanup, Local Environments for Host.

**RPC framework choice**: gRPC because of popularity, simplicity, documentation (but bigger dependency size).

---  
  

## Version 1.1 – RPC Basics & Robot Login

- [x] Added gRPC support (dependencies: grpc-*, protobuf-java, guava)
- [x] Defined minimal RPC interface (Login + Heartbeat)
- [x] Created working code generation pipeline (`make generate`)
- [x] Achieved full compilation of HTTP + generated RPC code via .proto
- [x] Restructured project into clean packages:
  - `src/http/` — original REST controller, client, data
  - `src/rpc/` — generated gRPC classes + test classes
- [x] Created TestProto for adding a robot to registry
- [x] Makefile corrections:
  - New options for compiling, generating, cleaning;
  - Cleanup and Documentation.
- [x] Tag `v1.1` 
  

---  
  

## Version 1.2 – Controller RPC Server & Robot Registry

- [ ] Implement controller RPC server (new port: 9524):
  - [ ] Start gRPC server in ControllerServer (or separate thread/class)
  - [ ] Add `RobotServiceGrpc.RobotServiceImplBase` implementation
  - [ ] Handle `login()` call - store robot in registry
  - [ ] Handle `heartbeat()` call - update last seen timestamp
- [ ] Create thread-safe robot registry
  - [ ] Create simple `RobotInfo` class (id, host, port, lastHeartbeat, version, status)
  - [ ] Add `ConcurrentHashMap<String, RobotInfo>` to `ControllerData`
  - [ ] Thread-safe add/update/remove methods
- [ ] Implement robot client with automatic login
  - [ ] Create `Robot.java` class
  - [ ] On startup: connect to controller gRPC stub → call `login()`
- [ ] Add simple debugging / listing endpoint
  - [ ] New HTTP GET `/robots` - return JSON array of all registered robots
  - [ ] Optional: `/robots/{id}` for single robot details
- [ ] CLI debugging endpoints with RPC (optional stretch)
- [ ] Integration test: controller + 5 robots = all appear as logged-in
  - [ ] Manual test or simple bash script to start 5 robots
- [ ] README.md correction
- [ ] Tag `v1.2`


---


## Version 1.3 – Heartbeat & Robot Status

- [ ] Robot sends heartbeat every 3–5 seconds (no piggybacking)
  - [ ] Add timer/scheduled task in `Robot.java`
- [ ] Controller marks robot OFFLINE if no heartbeat for >10 seconds
  - [ ] Background thread that checks timestamps
- [ ] Add RPC call: `GetRobotStatus`
  - [ ] Extend .proto: `rpc GetStatus(GetStatusRequest) returns (RobotStatus)`
- [ ] Connect GET endpoints to RPC registry
  - [ ] `/status` → count active robots from registry
  - [ ] `/captain` → still dummy for now
  - [ ] `/health` → controller + fleet health
  - [ ] `/robots` + `/robots/{id}` → new REST endpoints using registry data
- [ ] Measure heartbeat overhead with ~100 robots (optional)
- [ ] Tag `v1.3`


---


## Version 1.4 – Trigger Leader Election

- [ ] Extend RPC interface with:
  - `rpc StartLeaderElection(StartElectionRequest) returns (ElectionAck)`
- [ ] Connect POST endpoint `POST /le` → controller broadcasts to all robots
- [ ] Robot receives call → sets `leActive = true`, logs event
- [ ] Controller records acknowledgements
- [ ] Integration test: trigger LE → all robots update leActive
- [ ] Tag `v1.4`


---


## Version 1.5 – Robustness & Scaling

- [ ] Robot auto-reconnect using exponential backoff
- [ ] Graceful shutdown: robot sends `Logout` or controller detects closed channel
- [ ] Load test with ~100 robots
- [ ] Add more metrics (RPC call counters, connected robots, heartbeat delays)
- [ ] Measurement protocol: heartbeat frequency vs overhead, max robots
- [ ] Tag `v1.5`


---


## Version 1.6 – Final Submission Prep

- [ ] Update README:
  - How to start Controller REST + RPC + robots with docker-compose
  - Explanation of RPC/proto design
  - Link to measurement protocol
  - Add architecture diagram (REST + RPC)
- [ ] Clean logs, replace `System.out` with proper logging (e.g. java.util.logging)
- [ ] Prepare demo script:
  - start system
  - call GET endpoints
  - trigger LE via POST
  - observe logs
- [ ] Tag `v1.6`


---


## Version 2.0 – Further Changes and Improvements

- [ ] Add basic unit test setup (JUnit?)
- [ ] Add automatic logging mechanism