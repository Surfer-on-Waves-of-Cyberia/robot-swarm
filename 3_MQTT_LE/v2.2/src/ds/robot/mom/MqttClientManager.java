package ds.robot.mom;

import org.eclipse.paho.client.mqttv3.*;
import ds.robot.rpc.RobotClient;

/**
 * MQTT connection and message handling for robots
 */
public class MqttClientManager {

    private final MqttClient client;

    public MqttClientManager(String clientId, String brokerUrl) throws MqttException {
        this.client = new MqttClient(brokerUrl, clientId);
    }

    public void connect() throws MqttException {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
        client.connect(options);
    }

    /**
     * Subscribe to all election topics and measure total subscription time
     */
    public void subscribeAll(RobotClient robot) throws MqttException {
        long start = System.currentTimeMillis();

        subscribe("le/start");
        subscribe("le/election");
        subscribe("le/ok");
        subscribe("le/leader");
        subscribe("le/heartbeat");

        long duration = System.currentTimeMillis() - start;
        System.out.println("[Robot " + robot.getRobotId() + "] MQTT subscriptions completed (RTT: " + duration + " ms)");
    }

    private void subscribe(String topic) throws MqttException {
        client.subscribe(topic, 1);
    }

    public void setMessageHandler(RobotClient robot) {
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.err.println("[MQTT] Connection lost: " + cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                String payload = new String(message.getPayload());
                try {
                    switch (topic) {
                        case "le/election" -> LeaderElection.onElectionMessage(robot, payload);
                        case "le/ok"       -> LeaderElection.onOkMessage(robot, payload);
                        case "le/leader"   -> LeaderElection.onLeaderMessage(robot, payload);
                        case "le/start"    -> scheduleDelayedElection(robot);
                        case "le/heartbeat"-> handleHeartbeat(robot, payload);
                    }
                } catch (Exception e) {
                    System.err.println("[MQTT] Error on topic " + topic);
                    e.printStackTrace();
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {}
        });
    }

    private void scheduleDelayedElection(RobotClient robot) {
        new Thread(() -> {
            try {
                Thread.sleep(3000 + new java.util.Random().nextInt(5000));
                if (!robot.isElectionInProgress()) {
                    LeaderElection.electLeaderViaBully(robot);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "DelayedElection-" + robot.getRobotId()).start();
    }

    private void handleHeartbeat(RobotClient robot, String payload) {
        if (payload.equals(robot.getCurrentCaptainId())) {
            robot.setLastCaptainHeartbeat(System.currentTimeMillis());
        }
    }

    public void publish(String topic, String payload) throws MqttException {
        MqttMessage msg = new MqttMessage(payload.getBytes());
        msg.setQos(1);
        client.publish(topic, msg);
    }
}
