package ds.robot.http;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ControllerData manages global controller state and all registered robots.
 * - Tracks active robots
 * - Tracks leader (captain)
 * - Tracks controller health
 * - Provides methods for robot registration, heartbeat updates, and timeout checks
 */
public class ControllerData {

    private int robotsActive;
    private String captain;
    private String health;

    public void setCaptain(String val) { captain = val; }
    public String getCaptain() { return captain; }

    public void setHealth(String val) { health = val; }
    public String getHealth() { return health; }

    // RObot registry: robotId -> RobotInfo mapping
    private static final ConcurrentHashMap<String, RobotInfo> robots = new ConcurrentHashMap<>();

    public static void registerRobot(RobotInfo robot) {
        robots.put(robot.getRobotId(), robot);
        System.out.println("[Controller] Registered " + robot);
    }

    public static void updateHeartbeat(String robotId) {
        RobotInfo r = robots.get(robotId);
        if (r != null) {
            r.updateHeartbeat();
            System.out.println("[Controller] Heartbeat updated for " + robotId);
        }
    }

    // mark a robot offline if no heartbeat in timeout
    public static void markOfflineIfTimeout(long timeoutMs) {
        long now = System.currentTimeMillis();
        for (RobotInfo robot : robots.values()) {
            if (now - robot.getLastHeartbeat() > timeoutMs) {
                if (!"OFFLINE".equals(robot.getStatus())) {
                    System.out.println("[Controller] Robot " + robot.getRobotId() + " timed out, marking OFFLINE");
                    robot.markOffline();
                }
            }
        }
    }

    // all robots check
    public static Collection<RobotInfo> getAllRobots() {
        System.out.println(robot.values());
        return robots.values();

    }

    // all active robots check
    public int getRobotsActive() {
        long count = robots.values().stream()
                            .filter(r -> "ONLINE".equals(r.getStatus()))
                            .count();
        return (int) count;
    }

    // single robot check by id
    public static RobotInfo getRobot(String id) {
        return robots.get(id);
    }

    // Heartbeat Monitor
    public static void startHeartbeatMonitor(long timeoutMs, long intervalMs) {
        new Thread(() -> {
            while (true) {
                markOfflineIfTimeout(timeoutMs);
                try {
                    Thread.sleep(intervalMs);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "HeartbeatMonitor").start();
    }
}
