package ds.robot.http;

import java.io.*;
import java.net.*;
import java.util.Random;
import com.google.gson.*;

/**
 * RobotClient represents single robot.
 * - Connects to Controller JSON-RPC server
 * - Sends login and periodic heartbeats
 * - Listens for RPC calls from Controller (e.g., setCaptain)
 */
public class RobotClient {

    private final String robotId;
    private final String host;
    private final int controllerPort;
    private final int rpcPort;
    private final Gson gson = new Gson();

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

    public void start() throws IOException, InterruptedException {
        // Connect to controller
        socket = new Socket(host, controllerPort);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        // Start listener thread for controller RPCs
        startRPCListener();

        // Login
        login();

        // Heartbeat loop
        Random rand = new Random();
        while (running) {
            Thread.sleep(5000 + rand.nextInt(5000));
            try { sendHeartbeat(); }
            catch (IOException e) {
                System.out.println("[Robot " + robotId + "] Heartbeat failed: " + e.getMessage());
                stop();
            }
        }
    }

    public void stop() {
        running = false;
        if (socket != null) {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    public String getRobotId() { return robotId; }

    // ========================= RPC REQUESTS =========================
    private void login() throws IOException {
        JsonObject req = new JsonObject();
        req.addProperty("id", 1);
        req.addProperty("method", "login");

        JsonObject params = new JsonObject();
        params.addProperty("robot_id", robotId);
        params.addProperty("host", host);
        params.addProperty("port", rpcPort);
        req.add("params", params);

        long rtt = sendRequest(req);
        System.out.println("[Robot " + robotId + "] login RTT = " + rtt + " ms");
    }

    private void sendHeartbeat() throws IOException {
        JsonObject req = new JsonObject();
        req.addProperty("id", 2);
        req.addProperty("method", "heartbeat");

        JsonObject params = new JsonObject();
        params.addProperty("robot_id", robotId);
        req.add("params", params);

        long rtt = sendRequest(req);
        System.out.println("[Robot " + robotId + "] heartbeat RTT = " + rtt + " ms");
    }

    private long sendRequest(JsonObject request) throws IOException {
    long startNanos = System.nanoTime();

    out.write(request.toString() + "\n");
    out.flush();

    String responseLine = in.readLine();
    long durationMs = (System.nanoTime() - startNanos) / 1_000_000;

    if (responseLine != null) {
        JsonObject resp = JsonParser.parseString(responseLine).getAsJsonObject();
        System.out.println("[Robot " + robotId + "] Received (RTT " + durationMs + " ms): " + resp);
    } else {
        System.out.println("[Robot " + robotId + "] No response (RTT " + durationMs + " ms)");
    }

    return durationMs;
}

    // ========================= RPC LISTENER =========================
    private void startRPCListener() {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(rpcPort)) {
                System.out.println("[Robot " + robotId + "] RPC listener started on port " + rpcPort);
                while (running) {
                    Socket controller = server.accept();
                    new Thread(() -> handleRPC(controller)).start();
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
            System.out.println("WOW");
            if (line == null) return;

            JsonObject request = JsonParser.parseString(line).getAsJsonObject();
            String method = request.get("method").getAsString();
            int id = request.get("id").getAsInt();

            JsonObject response = new JsonObject();
            response.addProperty("id", id);

            if ("setCaptain".equals(method)) {
                String captain = request.getAsJsonObject("params").get("captain").getAsString();
                System.out.println("[Robot " + robotId + "] New captain: " + captain);

                JsonObject result = new JsonObject();
                result.addProperty("ack", true);
                response.add("result", result);
            }

            out.write(response.toString() + "\n");
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String robotId = args.length > 0 ? args[0] : "R1";
        RobotClient robot = new RobotClient(robotId, "controller-server", 9524, 10000);
        robot.start();
    }
}
