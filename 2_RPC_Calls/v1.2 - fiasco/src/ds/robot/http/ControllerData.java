package ds.robot.http;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Central robot registry.
 *
 * Thread-safe because HTTP thread, gRPC thread
 * and heartbeat checker access this data concurrently.
 */
public class ControllerData {

    private int robotsActive;
    private String captain;
    private String health;

    public void setRobotsActive(int val) { robotsActive = val; }
    public int getRobotsActive() { return robotsActive; }

    public void setCaptain(String val) { captain = val; }
    public String getCaptain() { return captain; }

    public void setHealth(String val) { health = val; }
    public String getHealth() { return health; }

    // robotId -> RobotInfo
    private static final ConcurrentHashMap<String, RobotInfo> robots =
            new ConcurrentHashMap<>();

    public static void registerRobot(RobotInfo robot) {
        robots.put(robot.getRobotId(), robot);
        System.out.println("[REGISTRY] Registered " + robot);
    }

    public static void updateHeartbeat(String robotId) {
        RobotInfo r = robots.get(robotId);
        if (r != null) {
            r.updateHeartbeat();
        }
    }

    // HTTP /robots
    public static Collection<RobotInfo> getAllRobots() {
        return robots.values();
    }

    // HTTP /robots single
    public static RobotInfo getRobot(String id) {
        return robots.get(id);
    }
}
