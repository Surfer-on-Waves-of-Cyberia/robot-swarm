package ds.robot.mom;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ds.robot.rpc.RobotClient;

/**
 * Bully leader election with RTT measurement
 */
public class LeaderElection {

    /**
     * Start Bully election with random delay and timeout timer
     */
    public static void electLeaderViaBully(RobotClient robot) throws Exception {
        if (robot.isElectionInProgress()) {
            System.out.println("[Robot " + robot.getRobotId() + "] Election already in progress, skipping");
            return;
        }

        long electionStart = System.currentTimeMillis();

        Thread.sleep(new java.util.Random().nextInt(3000) + 500);

        robot.resetOk();
        robot.setElectionInProgress(true);
        System.out.println("[Robot " + robot.getRobotId() + "] Starting Bully election");

        JsonObject electionMsg = new JsonObject();
        electionMsg.addProperty("from", robot.getRobotId());
        robot.getMqtt().publish("le/election", electionMsg.toString());

        // Timeout: become leader if no higher OK received
        new Thread(() -> {
            try {
                Thread.sleep(10000);

                boolean inProgress = robot.isElectionInProgress();
                boolean hasOkFlag = robot.hasOk();

                System.out.println("[Robot " + robot.getRobotId() + "] TIMEOUT CHECK - inProgress=" + inProgress +
                                   ", hasOk=" + hasOkFlag);

                if (inProgress && !hasOkFlag) {
                    long duration = System.currentTimeMillis() - electionStart;
                    System.out.println("[Robot " + robot.getRobotId() + "] - NO higher OK - becoming leader (RTT: " + (duration-10000)+ " ms)");
                    becomeLeader(robot);
                } else {
                    long duration = System.currentTimeMillis() - electionStart;
                    System.out.println("[Robot " + robot.getRobotId() + "] - Election resolved early (RTT: " + (duration-10000) + " ms)");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "ElectionTimeout-" + robot.getRobotId()).start();
    }

    /**
     * Handle incoming ELECTION message
     */
    public static void onElectionMessage(RobotClient robot, String payload) throws Exception {
        JsonObject message = JsonParser.parseString(payload).getAsJsonObject();
        String fromId = message.get("from").getAsString();

        if (fromId.equals(robot.getRobotId())) {
            return; // ignore self
        }

        int myNum = Integer.parseInt(robot.getRobotId().substring(1));
        int fromNum = Integer.parseInt(fromId.substring(1));

        if (myNum > fromNum) {
            JsonObject okMsg = new JsonObject();
            okMsg.addProperty("from", robot.getRobotId());
            robot.getMqtt().publish("le/ok", okMsg.toString());

            if (!robot.isElectionInProgress()) {
                robot.setElectionInProgress(true);
                robot.resetOk();
                electLeaderViaBully(robot);
            }
        }
    }

    /**
     * Handle incoming OK message – only higher IDs stop election
     */
    public static void onOkMessage(RobotClient robot, String payload) throws Exception {
        JsonObject message = JsonParser.parseString(payload).getAsJsonObject();
        String fromId = message.get("from").getAsString();

        if (fromId.equals(robot.getRobotId())) {
            return; // ignore self
        }

        int myNum = Integer.parseInt(robot.getRobotId().substring(1));
        int fromNum = Integer.parseInt(fromId.substring(1));

        if (fromNum > myNum) {
            robot.markOkReceived();
            robot.setElectionInProgress(false);
        }
    }

    /**
     * Handle LEADER announcement
     */
    public static void onLeaderMessage(RobotClient robot, String payload) {
        JsonObject message = JsonParser.parseString(payload).getAsJsonObject();
        String leaderId = message.get("leader").getAsString();

        if (leaderId.equals(robot.getCurrentCaptainId())) {
            return; // ignore duplicate
        }

        robot.setCurrentCaptainId(leaderId);
        robot.setLastCaptainHeartbeat(System.currentTimeMillis());
        robot.setElectionInProgress(false);
    }

    /**
     * Declare self as leader and announce
     */
    public static void becomeLeader(RobotClient robot) throws Exception {
        if (!robot.hasOk()) {
            robot.setCurrentCaptainId(robot.getRobotId());
            robot.setElectionInProgress(false);

            JsonObject leaderMsg = new JsonObject();
            leaderMsg.addProperty("leader", robot.getRobotId());
            robot.getMqtt().publish("le/leader", leaderMsg.toString());
        }
    }
}
