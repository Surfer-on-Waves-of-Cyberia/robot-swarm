# Configs

SRC = src
LIB = lib
CP = $(LIB)/*:$(SRC)

JAVA = javac
JAVA_RUN = java

# Rules

compile:
	$(JAVA) -cp "$(CP)" $(SRC)/ds/robot/http/*.java $(SRC)/ds/robot/rpc/*.java $(SRC)/ds/robot/mom/*.java

run-server: compile
	$(JAVA_RUN) -cp "$(CP)" ds.robot.http.ControllerServer

run-client: compile
	$(JAVA_RUN) -cp "$(CP)" ds.robot.http.ControllerClient

run-single-robot: compile
	$(JAVA_RUN) -cp "$(CP)" ds.robot.rpc.RobotClient

clean:
	rm -f $(SRC)/ds/robot/http/*.class $(SRC)/ds/robot/rpc/*.class $(SRC)/ds/robot/mom/*.class

debug-classpath:
	@echo "Classpath: $(CP)"
