## Design notes (compact)

### Middleware

* **MQTT + Mosquitto**
* Broker runs as Docker service
* Java robots use **Eclipse Paho**
* Works on **Arch Linux + Windows**

---

## Leader Election Algorithm

* **Bully algorithm**
* Each robot has a **numeric rank derived from ID**

  * Example: `R10 > R9 > R1`
* Higher ID **always wins**

---

## MQTT Topics (your table – unchanged)

| Topic                  | Publisher    | Subscriber | Purpose                   |
| ---------------------- | ------------ | ---------- | ------------------------- |
| `controller/le/start`  | Controller   | Robots     | Force leader election     |
| `le/election`          | Robots       | Robots     | Election start            |
| `le/ok`                | Robots       | Robots     | “I am alive and stronger” |
| `le/leader`            | Winner robot | Robots     | Announce captain          |
| `controller/le/result` | Winner robot | Controller | Notify controller         |

**Why multiple topics?**
Because MQTT is pub/sub only:

* Topics = **intent**
* Messages = **data**
* Cleaner debugging, simpler subscriptions

---

## Message meaning

### `controller/le/start`

```json
{ "reason": "manual" }
```

* Controller forces LE (demo / testing)

---

### `le/election`

```json
{ "from": "R4" }
```

* “I start an election”
* Robots with **higher ID** must respond

---

### `le/ok`

```json
{ "from": "R9" }
```

* “I’m stronger than you, stop”
* Sender continues the election itself

---

### `le/leader`

```json
{ "leader": "R10" }
```

* Final result
* All robots update local captain

---

### `controller/le/result`

```json
{ "leader": "R10" }
```

* Winner informs Controller (via MQTT or RPC)

---

## How the Bully algorithm actually works

### Case 1 – manual trigger (Controller)

1. Controller publishes `controller/le/start`
2. All robots receive it
3. Each robot:

   * sends `le/election`
   * waits for `le/ok`

### Case 2 – automatic trigger (robot-side)

Triggered when:

* captain heartbeat missing
* RPC to captain fails
* no `le/leader` seen for X seconds

Robot behavior:

1. Publish `le/election`
2. Start **timeout timer**

---

## Who wins?

* If a robot receives **any `le/ok`** → it **loses**
* If **no `le/ok` arrives before timeout** → it **wins**
* Winner:

  * publishes `le/leader`
  * notifies controller

Even if *everyone starts*, **only highest ID survives**
That’s why Bully is deterministic.

---

## Captain monitoring (simple & robust)

Each robot:

* stores `currentCaptain`
* stores `lastCaptainSeen`

Updated when:

* `le/leader` received
* heartbeat from captain received (optional MQTT heartbeat)

If:

```text
now - lastCaptainSeen > CAPTAIN_TIMEOUT
```
- trigger election automatically

