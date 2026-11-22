package ds.robot.mom;

import java.util.List;
import java.util.Random;

import ds.robot.http.ControllerData;
import ds.robot.rpc.RobotInfo;

public class LeaderElection {

    private static final Random random = new Random();

    public static String electRandomLeader(ControllerData controllerData) {
        List<RobotInfo> activeRobots = ControllerData.getActiveRobots();

        if (activeRobots.isEmpty()) {
            System.out.println("[LeaderElection] No active robots. Defaulting to 'R3'");
            controllerData.setCaptain("R3");
            return "R3";
        }

        RobotInfo chosen = activeRobots.get(random.nextInt(activeRobots.size()));
        controllerData.setCaptain(chosen.getRobotId());

        System.out.println("[LeaderElection] New leader elected: " + chosen.getRobotId());
        return chosen.getRobotId();
    }

    public static String electLeaderViaBully() {
        return "";
    }
}
