# ===== CONFIG =====

SRC=src
PROTO=proto
LIB=lib
CP=$(LIB)/*:$(SRC)

PROTOC=$(LIB)/protoc
GRPC_PLUGIN=$(LIB)/protoc-gen-grpc-java-1.78.0-linux-x86_64.exe

# ===== RULES =====

generate:
	$(PROTOC) \
	  -I $(PROTO) \
	  --java_out=$(SRC) \
	  --grpc-java_out=$(SRC) \
	  --plugin=protoc-gen-grpc-java=$(GRPC_PLUGIN) \
	  $(PROTO)/robot.proto

compile: generate
	javac -cp "$(CP)" \
	  $(SRC)/ds/robot/http/*.java \
	  $(SRC)/ds/robot/rpc/*.java

run-server: compile
	java -cp "$(CP)" ds.robot.http.ControllerServer

run-client: compile
	java -cp "$(CP)" ds.robot.http.ControllerClient R1

clean:
	rm -f $(SRC)/ds/robot/http/*.class
	rm -f $(SRC)/ds/robot/rpc/*.class
