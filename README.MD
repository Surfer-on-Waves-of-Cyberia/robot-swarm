# Robot Swarm — Distributed Systems Evolution

Evolutionary Java project exploring distributed communication and coordination for a robotic swarm simulation, created collaboratively with **Safar Hamad** as part of the **Verteilte Systeme (AI1015, WiSe 2025/26)** course at **Hochschule Fulda** and published with co-author permission.

## Architecture Evolution

- **`1_REST_HTTP` (`v0.1` – `v0.5`):** Early client-server and controller prototypes over HTTP/REST to establish basic state exchange and control flow.
- **`2_RPC_Calls` (`v1.0` – `v1.3`):** Exploration of RPC communication, starting with early service/container setups, gRPC/Protobuf experiments (`v1.1`, `v1.2_fiasco`), and a shift in `v1.3` to a custom JSON-RPC protocol over HTTP with Gson alongside client simulation scripts.
- **`3_MQTT_LE` (`v2.0` – `v2.2`):** Message-oriented middleware integration using MQTT (`MqttClientManager`) and leader election (`LeaderElection`) for distributed coordination.

## Running & Build Notes

Each stage folder is self-contained with its own `Makefile`, `Dockerfile`, and `docker-compose.yml` where applicable; check the `README.md` inside the specific version directory (`v1.3`, `v2.1`, `v2.2`, etc.) for exact build and startup commands. Local build artifacts, `.class` files are excluded from the repository.