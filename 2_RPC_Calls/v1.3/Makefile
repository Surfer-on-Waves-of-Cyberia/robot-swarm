# ===== CONFIG =====

SRC = src
LIB = lib
CP = $(LIB)/*:$(SRC)

JAVA = javac
JAVA_RUN = java

# ===== RULES =====

compile:
	$(JAVA) -cp "$(CP)" $(SRC)/ds/robot/http/*.java

run-server: compile
	$(JAVA_RUN) -cp "$(CP)" ds.robot.http.ControllerServer

run-client: compile
	$(JAVA_RUN) -cp "$(CP)" ds.robot.http.ControllerClient

run-robot-simulation: compile
	$(JAVA_RUN) -cp "$(CP)" ds.robot.http.RobotSimulation

run-single-robot: compile # rtt measurement
	$(JAVA_RUN) -cp "$(CP)" ds.robot.http.RobotClient

clean:
	rm -f $(SRC)/ds/robot/http/*.class

debug-classpath:
	@echo "Classpath: $(CP)"
