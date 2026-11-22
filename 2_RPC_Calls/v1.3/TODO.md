# TODO – Controller v1.2 – JSON-RPC

## 0️⃣ Vorbereitung
- [x] Makefile prüfen: `generate` und `compile` für reine Java-Klassen (Protobuf/gRPC kann wegfallen)  
- [x] Gson-Bibliothek in `lib/` hinzufügen (`gson-2.10.1.jar` z.B.)

## 1️⃣ Datenmodell
- [x] `RobotInfo.java` (POJO)  
- [x] Felder: `robotId`, `host`, `port`, `lastHeartbeat`, `status`  
- [x] Methoden: `updateHeartbeat()`, `markOffline()`, `toString()`

## 2️⃣ Thread-sichere Registry
- [x] `ControllerData.java`  
- [x] `ConcurrentHashMap<String, RobotInfo>`  
- [x] Methoden:
  - [x] `registerRobot(RobotInfo robot)`  
  - [x] `updateHeartbeat(String robotId, long timestamp)`  
  - [x] `getAllRobots()`  

## 3️⃣ HTTP-Server & JSON-RPC
- [ ] `ControllerServer.java`  
  - [ ] ServerSocket auf Port 1243 starten  
  - [ ] Endpoints:
    - [ ] `POST /robot/login` → registriert Roboter  
    - [ ] `POST /robot/heartbeat` → aktualisiert Heartbeat  
    - [ ] `GET /robots` → listet alle Roboter als JSON  
  - [ ] JSON-Parsing & Serialisierung mit Gson  
  - [ ] Status automatisch auf `ONLINE/OFFLINE` setzen

## 4️⃣ Robot-Client für Test/Demo
- [ ] `RobotClient.java`  
  - [ ] Methoden:
    - [ ] `login()` → POST JSON `/robot/login`  
    - [ ] `heartbeat()` → POST JSON `/robot/heartbeat`  
  - [ ] Einfacher Timer für Heartbeats (alle 5 Sekunden)  
  - [ ] CLI-Argumente für `robotId`, `host`, `port`  

## 5️⃣ Test & Debug
- [ ] Controller starten: `java ds.robot.http.ControllerServer`  
- [ ] Mehrere Clients starten: `java ds.robot.client.RobotClient`  
- [ ] Browser / curl prüfen: `http://localhost:1243/robots` → JSON-Liste der Roboter  
- [ ] Status automatisch auf OFFLINE setzen, wenn Heartbeat > 10s fehlt  

## 6️⃣ Präsentation
- [ ] Anmeldung Roboter zeigen  
- [ ] Heartbeat zeigen  
- [ ] Live-Liste der Roboter im Browser  

## 7️⃣ Abschluss
- [ ] Alle Klassen kompilieren (`javac`)  
- [ ] Commit: `"v1.2: JSON-RPC Controller + Robot Registry + HTTP /robots"`  
- [ ] Tag: `git tag v1.2 && git push --tags`  
