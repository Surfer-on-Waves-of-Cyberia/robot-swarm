
# Controller – HTTP Controller (Java)
Version: v0.5 (Final)  
Port: 1243  
Date: 05. December 2025  

---

### Project Overview
A clean implementation of a tiny HTTP-like server and client in pure Java (no external libraries).  
The server acts as a central controller in a robot swarm system and exposes simple status endpoints.  
The client is a testing tool that measures round-trip time (RTT) and performs statistical validation.

---

### Architecture
- ControllerServer – listens on port 1243, handles connections with clients, processes one request per TCP connection.
- ControllerClient – connects, sends requests, prints responses, measures RTT.
- ControllerData – simple data structure for the current system state (robotsActive, captain, health)

All communication is over raw TCP sockets with manually parsed HTTP/1.0-style messages.

---

### Supported Endpoints

**GET /status**  
- Returns number of active robots  
Example response: `{ "robotsActive": 5 }`

**GET /captain**  
- Returns current leader  
Example response: `{ "captain": "King" }`

**GET /health**  
- Returns system health  
Example response: `{ "health": "OK" }`

**POST /le**  
- Triggers leader election
Example response: `{ "leCreated": true, "body": "Start Leader Election!" }`

Any other path (including null) -- 404 Not Found  
Wrong HTTP method -- 405 Method Not Allowed

---

###  Real measured performance (v0.5 benchmark):
  - 100000 requests
  - Total RTT = 3558,83
  - Average RTT = 0.0356 ms
  - Minimum RTT = 0.0228 ms
  - Maximum RTT = 0.8393 ms

---

### Version History

**v0.1 – Raw TCP**  
Basic TCP server & client, one-line messages, no HTTP yet.

**v0.2 – First HTTP Parsing**  
Full manual parsing of request line, headers, Content-Length body, HTTP/1.0 vs 1.1 logic.

**v0.3 – Routing & Data**  
Switch-case routing, introduced ControllerData, new methods in client: sendRequest() and readResponse().

**v0.4 – Performance Testing**  
RTT as dedicated method, statistical validation, full testing of invalid cases.

**v0.5 – Final & Stable**  
Removed HTTP/1.1 and keep-alive completely (for simplicity & reliability), fixed all connection hangs, added Connection: close header, extensive comments and cleanup.

---

### Important Design Decisions
- Line endings: always \r\n (official HTTP standard)
- Body parsing: via Content-Length header (exactly like real HTTP)
- Required request headers: only Host and (for POST) Content-Length
- Connection handling: One request per connection model, always close after response (no keep-alive)

---

### Notes about biggest Errors
Many Rounds of Refactoring in Development. Too much features that had come as ideas were very difficult to implement without breaking anything .
- Endless loop because of keepAlive logic.

---

### How to Run
```bash
Compile
javac ControllerServer.java ControllerClient.java

Terminal 1 – start server
java ControllerServer

Terminal 2 – run client
java ControllerClient```
