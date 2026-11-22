FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY . .

RUN apt-get update && apt-get install -y make && rm -rf /var/lib/apt/lists/*

RUN make compile

EXPOSE 1243
EXPOSE 9524


CMD ["java", "-cp", "src:lib/*", "ds.robot.http.ControllerServer"]
