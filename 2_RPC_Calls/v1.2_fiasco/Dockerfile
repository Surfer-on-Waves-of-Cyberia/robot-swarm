FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app
COPY . .

RUN apt-get update && apt-get install -y make
RUN make

EXPOSE 1243

CMD ["java", "-cp", "src", "ControllerServer"]
