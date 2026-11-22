package ds.robot.rpc;

import ds.robot.http.ControllerData;
import ds.robot.http.RobotInfo;
import io.grpc.stub.StreamObserver;

/*
 * gRPC service implementation.
 * This class is called automatically by the gRPC server
 * when a robot sends an RPC request.
 */
public class RobotServiceImpl extends RobotServiceGrpc.RobotServiceImplBase {

    @Override
    public void login(LoginRequest req,
                      StreamObserver<LoginResponse> response) {

        // Build RobotInfo from RPC request
        RobotInfo robot = new RobotInfo(
                req.getRobotId(),
                req.getHost(),
                req.getGrpcPort()
        );

        // Store robot in registry
        ControllerData.registerRobot(robot);

        // Send success response
        response.onNext(
                LoginResponse.newBuilder()
                        .setAccepted(true)
                        .build()
        );
        response.onCompleted();
    }

    @Override
    public void heartbeat(HeartbeatRequest req,
                          StreamObserver<HeartbeatAck> response) {

        ControllerData.updateHeartbeat(req.getRobotId());

        response.onNext(
                HeartbeatAck.newBuilder()
                        .setAccepted(true)
                        .build()
        );
        response.onCompleted();
    }
}
