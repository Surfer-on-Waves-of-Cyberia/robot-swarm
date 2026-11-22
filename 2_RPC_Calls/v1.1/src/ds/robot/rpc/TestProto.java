package ds.robot.rpc;

public class TestProto {
    public static void main(String[] args) {
        // Create a LoginRequest message using the generated builder
        LoginRequest request = LoginRequest.newBuilder()
            .setRobotId("robot-001")
            .setHost("192.168.1.100")
            .setGrpcPort(9524)
            .build();

        System.out.println("=== TestProto - LoginRequest created successfully ===");
        System.out.println("Robot ID:     " + request.getRobotId());
        System.out.println("Host:         " + request.getHost());
        System.out.println("gRPC Port:    " + request.getGrpcPort());
        System.out.println("\nFull message (toString):\n" + request);

        // Optional: show that we can serialize to bytes (just for fun)
        byte[] bytes = request.toByteArray();
        System.out.println("Serialized size: " + bytes.length + " bytes");
    }
}
