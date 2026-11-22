JAVAC = javac
SRC = src

JAVAS = $(wildcard $(SRC)/*.java)
CLASSES = $(JAVAS:.java=.class)

all: $(CLASSES)

$(SRC)/%.class: $(SRC)/%.java
	$(JAVAC) -cp $(SRC) $<

run-server: all
	java -cp $(SRC) ControllerServer

run-client: all
	java -cp $(SRC) ControllerClient localhost 8080

clean:
	rm -f $(SRC)/*.class
