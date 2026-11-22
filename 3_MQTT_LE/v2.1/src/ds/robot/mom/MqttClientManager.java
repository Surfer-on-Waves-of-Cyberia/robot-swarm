package ds.robot.mom;

import org.eclipse.paho.client.mqttv3.*;
import com.google.gson.*;

public class MqttClientManager {

    private MqttClient client;

    public MqttClientManager(String clientId, String brokerUrl) throws MqttException {
        this.client = new MqttClient(brokerUrl, clientId);
    }

    public void connect() throws MqttException {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
        client.connect(options);
        System.out.println("[MQTT] Connected to broker");
    }

    public void subscribe(String topic) throws MqttException {
        client.subscribe(topic);
        System.out.println("[MQTT] Subscribed to " + topic);
    }

    public void setMessageHandler() {
        client.setCallback(new MqttCallback() {

            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("[MQTT] Connection lost: " + cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                String payload = new String(message.getPayload());
                if (topic.equals("le/leader")) {
                    JsonObject json = JsonParser.parseString(payload).getAsJsonObject();
                    String newCaptain = json.get("newCaptain").getAsString();
                    System.out.println("[MQTT] New leader received: " + newCaptain);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // TODO
            }
        });
    }

    public void publish(String topic, String payload) throws MqttException {
        MqttMessage message = new MqttMessage(payload.getBytes());
        client.publish(topic, message);

        System.out.println("[MQTT] Published to " + topic + ": " + payload);
    }
}
