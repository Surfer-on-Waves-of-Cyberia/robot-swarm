package ds.robot.http;

// Data model representing a single robot.
public class RobotInfo {

    private final String robotId;     // Unique Robot ID
    private final String host;        // Robot host/IP
    private final int port;           // RPC port

    private long lastHeartbeat;       // Timestamp of last heartbeat (ms)
    private String status;            // ONLINE / OFFLINE / ERROR

    public RobotInfo(String robotId, String host, int port) {
        this.robotId = robotId;
        this.host = host;
        this.port = port;
        this.lastHeartbeat = System.currentTimeMillis();
        this.status = "ONLINE";
    }

    public void updateHeartbeat() {
        this.lastHeartbeat = System.currentTimeMillis();
        this.status = "ONLINE";
    }

    public void markOffline() {
        this.status = "OFFLINE";
    }

    // ========================= GETTERS =========================
    public String getRobotId() { return robotId; }
    public String getHost() { return host; }
    public int getPort() { return port; }
    public long getLastHeartbeat() { return lastHeartbeat; }
    public String getStatus() { return status; }

    // ========================= DEBUG / LOG =========================
    @Override
    public String toString() {
        return "Robot[" + robotId + "] " + status + " (last heartbeat: " + lastHeartbeat + ")";
    }
}
