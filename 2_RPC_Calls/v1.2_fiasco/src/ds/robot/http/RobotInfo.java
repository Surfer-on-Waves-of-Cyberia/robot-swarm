package ds.robot.http;

// DATA MODEL FOR EACH ROBOT
public class RobotInfo {
    private final String robotId;     // Unique Robot ID
    private final String host;        // Host Name of the Robot
    private final int grpcPort;       // gRPC port

    private long lastHeartbeat;       // for Timeout
    private String status;            // ONLINE / OFFLINE / ERROR

    public RobotInfo(String robotId, String host, int grpcPort) {
        this.robotId = robotId;
        this.host = host;
        this.grpcPort = grpcPort;
        this.lastHeartbeat = System.currentTimeMillis();
        this.status = "ONLINE";
    }

    // Robot Time Actualising
    public void updateHeartbeat() {
        this.lastHeartbeat = System.currentTimeMillis();
        this.status = "ONLINE";
    }

    // If not heard from too long - mark offline
    public void markOffline() {
        this.status = "OFFLINE";
    }

    public String getRobotId() { return robotId; }
    public String getHost() { return host; }
    public int getGrpcPort() { return grpcPort; }
    public long getLastHeartbeat() { return lastHeartbeat; }
    public String getStatus() { return status; }

    // forr Debugging & Logs
    @Override
    public String toString() {
        return "Robot[" + robotId + "] " + status + " (last heartbeat: " + lastHeartbeat + ")";
    }
}
