package ds.robot.http;

import java.io.IOException;
import java.util.*;

/**
 * RobotSimulation
 * - Starts multiple RobotClient instances
 * - Launches the ControllerData heartbeat monitor
 * - Simulates fallout (robot failure) by stopping some robots
 */
public class RobotSimulation {

    public static void main(String[] args) throws InterruptedException, IOException {
        final int numRobots = 10;
        final int controllerPort = 9524;
        final int baseRobotPort = 10000;

        // Start Heartbeat Monitor
        final long timeoutMs = 6000;
        final long intervalMs = 2000;
        ControllerData.startHeartbeatMonitor(timeoutMs, intervalMs);

        // Start robot clients
        List<RobotClient> robots = new ArrayList<>(); // for fallout sim

        for (int i = 1; i <= numRobots; i++) {
            final String robotId = "R" + i;
            final int robotPort = baseRobotPort + i;
            RobotClient robot = new RobotClient(robotId, "controller-server", controllerPort, robotPort);
            robots.add(robot);

            new Thread(() -> {
                try {
                    robot.start();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }, "RobotThread-" + robotId).start();
        }

        System.out.println("[Simulation] Started " + numRobots + " robots. Heartbeats running...");

        // Simulated Fallout
        Thread.sleep(8000); // wait before fallout
        robots.stream()
            .limit(2) // simulate 2 robots failing
            .forEach(r -> {
                System.out.println("[Simulation] Robot " + r.getRobotId() + " stops sending heartbeats!");
                r.stop();
            });
    }
}
