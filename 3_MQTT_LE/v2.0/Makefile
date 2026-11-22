# Configs

SRC = src
LIB = lib
CP = $(LIB)/*:$(SRC)

JAVA = javac
JAVA_RUN = java

# Rules

compile:
	$(JAVA) -cp "$(CP)" $(SRC)/ds/robot/http/*.java $(SRC)/ds/robot/rpc/*.java

run-server: compile
	$(JAVA_RUN) -cp "$(CP)" ds.robot.http.ControllerServer

run-client: compile
	$(JAVA_RUN) -cp "$(CP)" ds.robot.http.ControllerClient

run-single-robot: compile # rtt measurement
	$(JAVA_RUN) -cp "$(CP)" ds.robot.rpc.RobotClient

clean:
	rm -f $(SRC)/ds/robot/http/*.class $(SRC)/ds/robot/rpc/*.class

debug-classpath:
	@echo "Classpath: $(CP)"
