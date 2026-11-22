package ds.robot.rpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * The actual RPC service
 * </pre>
 */
@io.grpc.stub.annotations.GrpcGenerated
public final class RobotServiceGrpc {

  private RobotServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "RobotService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<ds.robot.rpc.LoginRequest,
      ds.robot.rpc.LoginResponse> getLoginMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Login",
      requestType = ds.robot.rpc.LoginRequest.class,
      responseType = ds.robot.rpc.LoginResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ds.robot.rpc.LoginRequest,
      ds.robot.rpc.LoginResponse> getLoginMethod() {
    io.grpc.MethodDescriptor<ds.robot.rpc.LoginRequest, ds.robot.rpc.LoginResponse> getLoginMethod;
    if ((getLoginMethod = RobotServiceGrpc.getLoginMethod) == null) {
      synchronized (RobotServiceGrpc.class) {
        if ((getLoginMethod = RobotServiceGrpc.getLoginMethod) == null) {
          RobotServiceGrpc.getLoginMethod = getLoginMethod =
              io.grpc.MethodDescriptor.<ds.robot.rpc.LoginRequest, ds.robot.rpc.LoginResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Login"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ds.robot.rpc.LoginRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ds.robot.rpc.LoginResponse.getDefaultInstance()))
              .setSchemaDescriptor(new RobotServiceMethodDescriptorSupplier("Login"))
              .build();
        }
      }
    }
    return getLoginMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ds.robot.rpc.HeartbeatRequest,
      ds.robot.rpc.HeartbeatAck> getHeartbeatMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Heartbeat",
      requestType = ds.robot.rpc.HeartbeatRequest.class,
      responseType = ds.robot.rpc.HeartbeatAck.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ds.robot.rpc.HeartbeatRequest,
      ds.robot.rpc.HeartbeatAck> getHeartbeatMethod() {
    io.grpc.MethodDescriptor<ds.robot.rpc.HeartbeatRequest, ds.robot.rpc.HeartbeatAck> getHeartbeatMethod;
    if ((getHeartbeatMethod = RobotServiceGrpc.getHeartbeatMethod) == null) {
      synchronized (RobotServiceGrpc.class) {
        if ((getHeartbeatMethod = RobotServiceGrpc.getHeartbeatMethod) == null) {
          RobotServiceGrpc.getHeartbeatMethod = getHeartbeatMethod =
              io.grpc.MethodDescriptor.<ds.robot.rpc.HeartbeatRequest, ds.robot.rpc.HeartbeatAck>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Heartbeat"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ds.robot.rpc.HeartbeatRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ds.robot.rpc.HeartbeatAck.getDefaultInstance()))
              .setSchemaDescriptor(new RobotServiceMethodDescriptorSupplier("Heartbeat"))
              .build();
        }
      }
    }
    return getHeartbeatMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static RobotServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RobotServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RobotServiceStub>() {
        @java.lang.Override
        public RobotServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RobotServiceStub(channel, callOptions);
        }
      };
    return RobotServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static RobotServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RobotServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RobotServiceBlockingV2Stub>() {
        @java.lang.Override
        public RobotServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RobotServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return RobotServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static RobotServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RobotServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RobotServiceBlockingStub>() {
        @java.lang.Override
        public RobotServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RobotServiceBlockingStub(channel, callOptions);
        }
      };
    return RobotServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static RobotServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RobotServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RobotServiceFutureStub>() {
        @java.lang.Override
        public RobotServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RobotServiceFutureStub(channel, callOptions);
        }
      };
    return RobotServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * The actual RPC service
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * Robot → Controller (registration)
     * </pre>
     */
    default void login(ds.robot.rpc.LoginRequest request,
        io.grpc.stub.StreamObserver<ds.robot.rpc.LoginResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getLoginMethod(), responseObserver);
    }

    /**
     * <pre>
     * Periodic keep-alive
     * </pre>
     */
    default void heartbeat(ds.robot.rpc.HeartbeatRequest request,
        io.grpc.stub.StreamObserver<ds.robot.rpc.HeartbeatAck> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getHeartbeatMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service RobotService.
   * <pre>
   * The actual RPC service
   * </pre>
   */
  public static abstract class RobotServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return RobotServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service RobotService.
   * <pre>
   * The actual RPC service
   * </pre>
   */
  public static final class RobotServiceStub
      extends io.grpc.stub.AbstractAsyncStub<RobotServiceStub> {
    private RobotServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RobotServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RobotServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Robot → Controller (registration)
     * </pre>
     */
    public void login(ds.robot.rpc.LoginRequest request,
        io.grpc.stub.StreamObserver<ds.robot.rpc.LoginResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getLoginMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Periodic keep-alive
     * </pre>
     */
    public void heartbeat(ds.robot.rpc.HeartbeatRequest request,
        io.grpc.stub.StreamObserver<ds.robot.rpc.HeartbeatAck> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getHeartbeatMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service RobotService.
   * <pre>
   * The actual RPC service
   * </pre>
   */
  public static final class RobotServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<RobotServiceBlockingV2Stub> {
    private RobotServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RobotServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RobotServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     * <pre>
     * Robot → Controller (registration)
     * </pre>
     */
    public ds.robot.rpc.LoginResponse login(ds.robot.rpc.LoginRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getLoginMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Periodic keep-alive
     * </pre>
     */
    public ds.robot.rpc.HeartbeatAck heartbeat(ds.robot.rpc.HeartbeatRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getHeartbeatMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service RobotService.
   * <pre>
   * The actual RPC service
   * </pre>
   */
  public static final class RobotServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<RobotServiceBlockingStub> {
    private RobotServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RobotServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RobotServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Robot → Controller (registration)
     * </pre>
     */
    public ds.robot.rpc.LoginResponse login(ds.robot.rpc.LoginRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getLoginMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Periodic keep-alive
     * </pre>
     */
    public ds.robot.rpc.HeartbeatAck heartbeat(ds.robot.rpc.HeartbeatRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getHeartbeatMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service RobotService.
   * <pre>
   * The actual RPC service
   * </pre>
   */
  public static final class RobotServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<RobotServiceFutureStub> {
    private RobotServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RobotServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RobotServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Robot → Controller (registration)
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ds.robot.rpc.LoginResponse> login(
        ds.robot.rpc.LoginRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getLoginMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Periodic keep-alive
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ds.robot.rpc.HeartbeatAck> heartbeat(
        ds.robot.rpc.HeartbeatRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getHeartbeatMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_LOGIN = 0;
  private static final int METHODID_HEARTBEAT = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_LOGIN:
          serviceImpl.login((ds.robot.rpc.LoginRequest) request,
              (io.grpc.stub.StreamObserver<ds.robot.rpc.LoginResponse>) responseObserver);
          break;
        case METHODID_HEARTBEAT:
          serviceImpl.heartbeat((ds.robot.rpc.HeartbeatRequest) request,
              (io.grpc.stub.StreamObserver<ds.robot.rpc.HeartbeatAck>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getLoginMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              ds.robot.rpc.LoginRequest,
              ds.robot.rpc.LoginResponse>(
                service, METHODID_LOGIN)))
        .addMethod(
          getHeartbeatMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              ds.robot.rpc.HeartbeatRequest,
              ds.robot.rpc.HeartbeatAck>(
                service, METHODID_HEARTBEAT)))
        .build();
  }

  private static abstract class RobotServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    RobotServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return ds.robot.rpc.Robot.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("RobotService");
    }
  }

  private static final class RobotServiceFileDescriptorSupplier
      extends RobotServiceBaseDescriptorSupplier {
    RobotServiceFileDescriptorSupplier() {}
  }

  private static final class RobotServiceMethodDescriptorSupplier
      extends RobotServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    RobotServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (RobotServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new RobotServiceFileDescriptorSupplier())
              .addMethod(getLoginMethod())
              .addMethod(getHeartbeatMethod())
              .build();
        }
      }
    }
    return result;
  }
}
