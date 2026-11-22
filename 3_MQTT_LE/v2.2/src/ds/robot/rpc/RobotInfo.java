package ds.robot.rpc;

public class RobotInfo {

    private final String robotId;
    private final String host;
    private final int port;

    private long lastHeartbeat;
    private String status;

    public RobotInfo(String robotId, String host, int port) {
        this.robotId = robotId;
        this.host = host;
        this.port = port;
        this.lastHeartbeat = System.currentTimeMillis();
        this.status = "STARTED";
    }

    public void updateHeartbeat() {
        this.lastHeartbeat = System.currentTimeMillis();
    }

    public void markOffline() {
        this.status = "OFFLINE";
    }

    public void markOnline() {
        this.status = "ONLINE";
    }

    public String getRobotId() { return robotId; }
    public String getHost() { return host; }
    public int getPort() { return port; }
    public long getLastHeartbeat() { return lastHeartbeat; }
    public String getStatus() { return status; }
    public boolean isOnline() { return "ONLINE".equals(status); }

    @Override
    public String toString() {
        return "Robot[" + robotId + "] " + status + " (last heartbeat: " + lastHeartbeat + ")";
    }
}
