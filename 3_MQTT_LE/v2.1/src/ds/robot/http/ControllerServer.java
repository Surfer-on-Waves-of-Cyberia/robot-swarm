package ds.robot.http;

import java.io.*;
import java.net.*;
import java.util.*;
import com.google.gson.*;

import ds.robot.rpc.RobotInfo;
import ds.robot.mom.LeaderElection;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * ControllerServer has 3 main parts.
 * - HTTP endpoints for monitoring and leader election
 * - JSON-RPC server for robots to log in and send heartbeats
 * - MoM?
 */
public class ControllerServer {

    private static final ControllerData controllerData = new ControllerData();
    private final ServerSocket httpSocket;

    private static MqttClient mqttClient;

    public ControllerServer(int port) throws IOException {
        this.httpSocket = new ServerSocket(port);
    }

    public static void main(String[] args) throws IOException {
        controllerData.setCaptain("R1");
        controllerData.setHealth("OK");

        int httpPort = 1243;
        ControllerServer server = new ControllerServer(httpPort);
        System.out.println("[Controller] HTTP server listening on port " + httpPort);

        final long timeoutMs = 10000;
        final long intervalMs = 5000;
        ControllerData.startHeartbeatMonitor(timeoutMs, intervalMs);

        new RobotRPCServer(9524).start();

        String brokerUrl = "tcp://mqtt_broker:1883"; // adjust if needed
        try {
            mqttClient = new MqttClient(brokerUrl, "controller-server");
            mqttClient.connect();
            System.out.println("[Controller] Connected to MQTT broker at " + brokerUrl);
        } catch (Exception e) {
            System.out.println("[Controller] Failed to connect to MQTT broker: " + e.getMessage());
        }

        while (true) {
            try {
                Socket clientSocket = server.httpSocket.accept();
                handleHTTPConnection(clientSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // HTTP SERVER
    private static void handleHTTPConnection(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))
        ) {
            handleHTTPRequest(in, out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try { clientSocket.close(); } catch (IOException ignored) {}
        }
    }

    private static void handleHTTPRequest(BufferedReader in, BufferedWriter out) throws IOException {
        String requestLine = in.readLine();
        if (requestLine == null || requestLine.isEmpty()) return;

        String[] parts = requestLine.split(" ");
        if (parts.length != 3) {
            sendResponse(out, "400 Bad Request", "Malformed request line");
            return;
        }

        String method = parts[0];
        String path = parts[1];

        // Read headers
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            int idx = line.indexOf(':');
            if (idx != -1) {
                headers.put(line.substring(0, idx).trim().toLowerCase(),
                            line.substring(idx + 1).trim());
            }
        }

        String body = "";
        if ("POST".equals(method) && headers.containsKey("content-length")) {
            int contentLength = Integer.parseInt(headers.get("content-length"));
            char[] buffer = new char[contentLength];
            int read = in.read(buffer, 0, contentLength);
            body = new String(buffer, 0, read);
        }

        switch (path) {
            case "/status" -> {
                if (!"GET".equals(method)) { sendResponse(out, "405 Method Not Allowed", "Only GET allowed"); return; }
                sendResponse(out, "200 OK", "{ \"robotsActive\": " + ControllerData.getAllRobots().size() + " }");
            }
            case "/captain" -> {
                if (!"GET".equals(method)) { sendResponse(out, "405 Method Not Allowed", "Only GET allowed"); return; }
                sendResponse(out, "200 OK", "{ \"captain\": \"" + controllerData.getCaptain() + "\" }");
            }
            case "/health" -> {
                if (!"GET".equals(method)) { sendResponse(out, "405 Method Not Allowed", "Only GET allowed"); return; }
                sendResponse(out, "200 OK", "{ \"health\": \"" + controllerData.getHealth() + "\" }");
            }
            case "/le" -> {
                if (!"POST".equals(method)) { sendResponse(out, "405 Method Not Allowed", "Only POST allowed"); return; }

                String newCaptain = LeaderElection.electRandomLeader(controllerData);
                notifyLeaderViaMqtt(newCaptain);
                sendResponse(out, "200 OK", "{ \"leaderElection\": true, \"newCaptain\": \"" + newCaptain + "\" }");
            }
            default -> sendResponse(out, "404 Not Found", "Path not found");
        }
    }

    private static void sendResponse(BufferedWriter out, String status, String body) throws IOException {
        out.write("HTTP/1.0 " + status + "\r\n");
        out.write("Content-Type: application/json\r\n");
        out.write("Content-Length: " + body.length() + "\r\n\r\n");
        out.write(body);
        out.flush();
    }


    private static void notifyLeaderViaMqtt(String newCaptain) {
        try {
            if (mqttClient == null || !mqttClient.isConnected()) {
                System.out.println("[Controller] MQTT client not connected, cannot notify leader");
                return;
            }

            JsonObject payload = new JsonObject();
            payload.addProperty("newCaptain", newCaptain);

            MqttMessage message = new MqttMessage(payload.toString().getBytes());
            mqttClient.publish("le/leader", message);

            System.out.println("[Controller] Notified all robots via MQTT: " + newCaptain);
        } catch (Exception e) {
            System.out.println("[Controller] Failed to notify robots via MQTT: " + e.getMessage());
        }
    }



    // JSON-RPC SERVER
    static class RobotRPCServer {
        private final int port;

        public RobotRPCServer(int port) { this.port = port; }

        public void start() {
            new Thread(() -> {
                try (ServerSocket server = new ServerSocket(port)) {
                    System.out.println("[RPC] Robot JSON-RPC server started on port " + port);
                    while (true) {
                        Socket client = server.accept();
                        new Thread(() -> handleRPCConnection(client)).start();
                    }
                } catch (IOException e) { e.printStackTrace(); }
            }).start();
        }

        private void handleRPCConnection(Socket client) {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()))
            ) {
                String line;
                while ((line = in.readLine()) != null && !line.isEmpty()) {
                    processRPCRequest(line, out);
                }
            } catch (IOException e) { e.printStackTrace(); }

            finally {
                try { client.close(); }
                catch (IOException ignored) {}
            }
        }

        private void processRPCRequest(String jsonLine, BufferedWriter out) throws IOException {
            JsonObject response = new JsonObject();
            try {
                JsonObject request = JsonParser.parseString(jsonLine).getAsJsonObject();
                String method = request.get("method").getAsString();
                int id = request.has("id") ? request.get("id").getAsInt() : 0;
                JsonObject params = request.has("params") ? request.get("params").getAsJsonObject() : new JsonObject();

                response.addProperty("id", id);

                switch (method) {
                    case "login" -> {
                        RobotInfo robot = new RobotInfo(
                            params.get("robot_id").getAsString(),
                            params.get("host").getAsString(),
                            params.get("port").getAsInt()
                        );
                        ControllerData.registerRobot(robot);
                        JsonObject result = new JsonObject();
                        result.addProperty("success", true);
                        response.add("result", result);
                        System.out.println("[Controller] Robot logged in: " + robot.getRobotId());
                    }
                    case "heartbeat" -> {
                        ControllerData.updateHeartbeat(params.get("robot_id").getAsString());
                        JsonObject hbResult = new JsonObject();
                        hbResult.addProperty("accepted", true);
                        response.add("result", hbResult);
                        System.out.println("[Controller] Heartbeat received from robot: " + params.get("robot_id").getAsString());
                    }
                    default -> {
                        JsonObject error = new JsonObject();
                        error.addProperty("error", "Unknown method");
                        response.add("error", error);
                    }
                }
            } catch (Exception e) {
                JsonObject error = new JsonObject();
                error.addProperty("error", "Invalid request: " + e.getMessage());
                response.add("error", error);
            }

            out.write(response.toString() + "\n");
            out.flush();
        }
    }
}
