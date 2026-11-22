package ds.robot.rpc;

import java.io.*;
import java.net.*;
import java.util.Random;

import com.google.gson.*;

import ds.robot.mom.LeaderElection;
import ds.robot.mom.MqttClientManager;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * RobotClient represents a single robot.
 * - Connects to Controller JSON-RPC server (login + heartbeats)
 * - Listens for RPC calls from Controller
 * - Participates in Bully leader election via MQTT
 */
public class RobotClient {

    private final String robotId;
    private final String host;
    private final int controllerPort;
    private final int rpcPort;
    private final Gson gson = new Gson();

    private MqttClientManager mqtt;

    private volatile String currentCaptainId = null;
    private volatile long lastCaptainHeartbeat = 0;
    private volatile boolean okReceived = false;
    private volatile boolean electionInProgress = false;

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    private volatile boolean running = true;

    public RobotClient(String robotId, String host, int controllerPort, int rpcPort) {
        this.robotId = robotId;
        this.host = host;
        this.controllerPort = controllerPort;
        this.rpcPort = rpcPort;
    }

    public void start() throws IOException, InterruptedException, MqttException {
        socket = new Socket(host, controllerPort);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        startRPCListener();
        login();

        mqtt = new MqttClientManager(
            "robot-" + robotId,
            "tcp://mqtt_broker:1883"
        );
        mqtt.connect();
        mqtt.setMessageHandler(this);
        mqtt.subscribeAll(this);

        startCaptainMonitor();

        Random random = new Random();
        while (running) {
            Thread.sleep(5000 + random.nextInt(5000));
            sendHeartbeat();
        }
    }

    private void startCaptainMonitor() {
        new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(1000);

                    if (currentCaptainId == null) continue;

                    if (!robotId.equals(currentCaptainId) &&
                        System.currentTimeMillis() - lastCaptainHeartbeat > 10000 &&
                        !electionInProgress) {

                        System.out.println("[Robot " + robotId + "] Captain timeout, starting election");
                        electionInProgress = true;
                        resetOk();

                        LeaderElection.electLeaderViaBully(this);
                    }

                } catch (Exception e) {
                    System.out.println("[Robot " + robotId + "] Captain monitor error: " + e.getMessage());
                }
            }
        }, "CaptainMonitor-" + robotId).start();
    }

    // Handles incoming MQTT messages
    private void handleMQTTMessage(String topic, String payload) {
        try {
            switch (topic) {
                case "le/election" -> LeaderElection.onElectionMessage(this, payload);
                case "le/ok"       -> LeaderElection.onOkMessage(this, payload);
                case "le/leader"   -> LeaderElection.onLeaderMessage(this, payload);
                case "le/start" -> {
                    System.out.println("[Robot " + robotId + "] Manual LE start requested");
                    LeaderElection.electLeaderViaBully(this);
                }
            }
        } catch (Exception e) {
            System.out.println("[Robot " + robotId + "] MQTT handler error: " + e.getMessage());
        }
    }


    // RPC requests to controller
    private void login() throws IOException {
        JsonObject req = new JsonObject();
        req.addProperty("id", 1);
        req.addProperty("method", "login");

        JsonObject params = new JsonObject();
        params.addProperty("robot_id", robotId);
        params.addProperty("host", host);
        params.addProperty("port", rpcPort);
        req.add("params", params);

        sendRequest(req);
    }

    private void sendHeartbeat() throws IOException {
        JsonObject req = new JsonObject();
        req.addProperty("id", 2);
        req.addProperty("method", "heartbeat");

        JsonObject params = new JsonObject();
        params.addProperty("robot_id", robotId);
        req.add("params", params);

        sendRequest(req);
    }

    private long sendRequest(JsonObject request) throws IOException {
        long start = System.nanoTime();

        out.write(request.toString() + "\n");
        out.flush();

        String response = in.readLine();
        long rtt = (System.nanoTime() - start) / 1_000_000;

        if (response != null) {
            System.out.println("[Robot " + robotId + "] Controller reply (" + rtt + " ms): " + response);
        }

        return rtt;
    }

    // RPC server for setCaptain calls
    private void startRPCListener() {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(rpcPort)) {
                System.out.println("[Robot " + robotId + "] RPC listener on port " + rpcPort);
                while (running) {
                    Socket s = server.accept();
                    new Thread(() -> handleRPC(s)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "RobotRPCListener-" + robotId).start();
    }

    private void handleRPC(Socket socket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
        ) {
            String line = in.readLine();
            if (line == null) return;

            JsonObject req = JsonParser.parseString(line).getAsJsonObject();
            String method = req.get("method").getAsString();
            int id = req.get("id").getAsInt();

            JsonObject resp = new JsonObject();
            resp.addProperty("id", id);

            if ("setCaptain".equals(method)) {
                String captain = req.getAsJsonObject("params").get("captain").getAsString();
                System.out.println("[Robot " + robotId + "] Captain set to " + captain);

                currentCaptainId = captain;
                lastCaptainHeartbeat = System.currentTimeMillis();
                electionInProgress = false;

                JsonObject result = new JsonObject();
                result.addProperty("ack", true);
                resp.add("result", result);
            }

            out.write(resp.toString() + "\n");
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    // Bully helpers
    public String getRobotId() { return robotId; }
    public MqttClientManager getMqtt() { return mqtt; }

    public String getCurrentCaptainId() { return currentCaptainId; }
    public void setCurrentCaptainId(String id) { this.currentCaptainId = id; }

    public long getLastCaptainHeartbeat() { return lastCaptainHeartbeat; }
    public void setLastCaptainHeartbeat(long t) { this.lastCaptainHeartbeat = t; }

    public void resetOk() { okReceived = false; }
    public void markOkReceived() { okReceived = true; }
    public boolean hasOk() { return okReceived; }

    public void setElectionInProgress(boolean v) { electionInProgress = v; }
    public boolean isElectionInProgress() { return electionInProgress; }

    public static void main(String[] args) throws Exception {
        String robotId = System.getenv().getOrDefault("ROBOT_ID", "Unknown");
        String controllerHost = System.getenv().getOrDefault("CONTROLLER_HOST", "controller-server");
        int controllerPort = Integer.parseInt(System.getenv().getOrDefault("CONTROLLER_PORT", "9524"));
        int robotPort = Integer.parseInt(System.getenv().getOrDefault("ROBOT_PORT", "10001"));

        new RobotClient(robotId, controllerHost, controllerPort, robotPort).start();
    }
}
