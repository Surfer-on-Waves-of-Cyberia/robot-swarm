# ================= CONFIG =================

PROTOC       := ./lib/protoc
GRPC_PLUGIN  := ./lib/protoc-gen-grpc-java-1.78.0-linux-x86_64.exe
PROTO_DIR    := proto
LIBS         := $(wildcard lib/*.jar)

# ================= RULES =================

# Make plugin executable (run once)
$(GRPC_PLUGIN):
	chmod +x $(GRPC_PLUGIN)

generate:
	$(PROTOC) \
	  --proto_path=$(PROTO_DIR) \
	  --java_out=src \
	  --grpc-java_out=src \
	  --plugin=protoc-gen-grpc-java=$(GRPC_PLUGIN) \
	  $(PROTO_DIR)/robot.proto

compile: generate
	javac -cp "$$(echo lib/*.jar | tr ' ' ':'):src" \
	  src/ds/robot/http/*.java \
	  src/ds/robot/rpc/*.java

run-server: compile
	java -cp "$$(echo lib/*.jar | tr ' ' ':'):src" \
	  ds.robot.http.ControllerServer

run-client: compile
	java -cp "$$(echo lib/*.jar | tr ' ' ':'):src" \
	  ds.robot.http.ControllerClient localhost 1243

clean-classes:
	rm -rf src/ds/robot/http/*.class src/ds/robot/rpc/*.class

clean-generated:
	rm -rf src/ds/robot/rpc/*.java

debug-classpath:
	@echo "Classpath:"
	@echo "$$(echo lib/*.jar | tr ' ' ':'):src"

.PHONY: generate compile run-server run-client clean debug-cp
