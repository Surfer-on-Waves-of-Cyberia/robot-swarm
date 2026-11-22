package ds.robot.http;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import ds.robot.rpc.RobotInfo;

/**
 * ControllerData manages global controller state and all registered robots.
 * - Tracks active robots
 * - Tracks leader (captain)
 * - Tracks controller health
 * - Provides methods for robot registration, heartbeat updates, and timeout checks
 */
public class ControllerData {

    private int robotsActive;
    private static String captain;
    private static String health;

    public void setCaptain(String val) { captain = val; }
    public String getCaptain() { return captain; }

    public void setHealth(String val) { health = val; }
    public String getHealth() { return health; }

    private static final ConcurrentHashMap<String, RobotInfo> robots = new ConcurrentHashMap<>();


    public static void registerRobot(RobotInfo robot) {
        robots.put(robot.getRobotId(), robot);
        robot.markOnline();
        System.out.println("[Controller] Registered " + robot);
    }

    public static void updateHeartbeat(String robotId) {
        RobotInfo robot = robots.get(robotId);
        if (robot != null) {
            robot.updateHeartbeat();
            robot.markOnline();
            System.out.println("[Controller] Heartbeat updated for " + robotId + " - marking the robot ONLINE");
        }
    }


    public static Collection<RobotInfo> getAllRobots() {
        System.out.println(robots.values());
        return robots.values();
    }

    public int getRobotsActive() {
        long count = robots.values().stream()
                            .filter(r -> "ONLINE".equals(r.getStatus()))
                            .count();
        return (int) count;
    }

    public static RobotInfo getRobot(String id) {
        return robots.get(id);
    }


    public static void startHeartbeatMonitor(long timeoutMs, long intervalMs) {
        new Thread(() -> {
            while (true) {
                timeoutCheck(timeoutMs);
                try {
                    Thread.sleep(intervalMs);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "HeartbeatMonitor").start();
    }

    public static void timeoutCheck(long timeoutMs) {
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
}
