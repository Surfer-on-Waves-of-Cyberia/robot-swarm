import java.io.*;
import java.net.*;

public class ControllerClient {

    private Socket socket;
    private BufferedWriter output;
    private BufferedReader input;

    public ControllerClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public static void main(String[] args) {
        try {
            ControllerClient client = new ControllerClient("localhost", 1243);
            System.out.println("--- VALID CASES ---");

            System.out.println("RTT for GET /status = " + client.measureRTT("GET", "/status", null) + " ms\n");

            client = new ControllerClient("localhost", 1243);
            System.out.println("RTT for GET /captain = " + client.measureRTT("GET", "/captain", null) + " ms\n");

            client = new ControllerClient("localhost", 1243);
            System.out.println("RTT for GET /health = " + client.measureRTT("GET", "/health", null) + " ms\n");

            client = new ControllerClient("localhost", 1243);
            System.out.println("RTT for POST /le = " + client.measureRTT("POST", "/le", "HelloFromClient") + " ms\n");

            System.out.println("--- INVALID CASES ---");

            client = new ControllerClient("localhost", 1243);
            client.sendRequest("POST", "/status", "Body");

            client = new ControllerClient("localhost", 1243);
            client.sendRequest("POST", "/captain", "Body");

            client = new ControllerClient("localhost", 1243);
            client.sendRequest("POST", "/health", "Body");

            client = new ControllerClient("localhost", 1243);
            client.sendRequest("GET", "/le", null);

            client = new ControllerClient("localhost", 1243);
            client.sendRequest("GET", "/cake", null);


            System.out.println("--- STATISTICAL VALIDATION ---");
            statisticalValidation("POST", "/le", null, 5000);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(String method, String path, String body) throws IOException {
        if (body == null) body = "";

        output.write(method + " " + path + " HTTP/1.1\r\n");
        output.write("Host: localhost\r\n");

        if (method.equals("POST")) {
            output.write("Content-Length: " + body.length() + "\r\n");
        }

        output.write("Connection: close\r\n");
        output.write("\r\n");

        if (method.equals("POST")) {
            output.write(body);
        }

        output.flush();
        readResponse(socket);
    }

    private void readResponse(Socket socket) throws IOException {
        String line;
        while ((line = input.readLine()) != null) {
            System.out.println(line);
        }
    }

    public double measureRTT(String method, String path, String body) throws IOException {
        long start = System.nanoTime();
        sendRequest(method, path, body);
        long end = System.nanoTime();
        return (end - start) / 1000000.0;
    }

    // statistical validation needs a number of repetitions
    public static void statisticalValidation(String method, String path, String body, int repetitions) throws IOException {
        double sum = 0;
        double min = 100;
        double max = 0;
        double rtt;

        for (int i = 0; i < repetitions; i++) {
            ControllerClient client = new ControllerClient("localhost", 1243);
            rtt = client.measureRTT(method, path, body);
            sum += rtt;
            if (rtt < min) min = rtt;
            if (rtt > max) max = rtt;
        }
        double avg = sum / repetitions;
        System.out.println("Performed " + repetitions + " tests, total RTT = " + sum + " ms, average RTT = " + avg + " ms, minimal RTT = " + min + " ms, maximal RTT = " + max + " ms");
    }
}
