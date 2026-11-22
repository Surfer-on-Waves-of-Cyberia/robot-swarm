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

    public static void main(String[] args) throws IOException {
        System.out.println("--- VALID CASES ---");
        ControllerClient client;

        client = new ControllerClient("localhost", 1243);
        System.out.println("RTT GET /status = " + client.measureRTT("GET", "/status", null) + " ms");

        client = new ControllerClient("localhost", 1243);
        System.out.println("RTT GET /captain = " + client.measureRTT("GET", "/captain", null) + " ms");

        client = new ControllerClient("localhost", 1243);
        System.out.println("RTT GET /health = " + client.measureRTT("GET", "/health", null) + " ms");

        client = new ControllerClient("localhost", 1243);
        System.out.println("RTT POST /le = " + client.measureRTT("POST", "/le", "Start Leader Election!") + " ms");

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
        statisticalValidation("POST", "/le", "Start Leader Election!", 100000);
    }

    private void sendRequest(String method, String path, String body) throws IOException {
        if (body == null) body = "";

        output.write(method + " " + path + " HTTP/1.0\r\n");
        output.write("Host: localhost\r\n");
        if ("POST".equals(method)) {
            output.write("Content-Length: " + body.length() + "\r\n");
        }
        output.write("\r\n");
        if ("POST".equals(method)) {
            output.write(body);
        }
        output.flush();

        String line;
        while ((line = input.readLine()) != null) {
            System.out.println(line);
        }

        socket.close();
    }

    public double measureRTT(String method, String path, String body) throws IOException {
        long start = System.nanoTime();
        sendRequest(method, path, body);
        long end = System.nanoTime();
        return (end - start) / 1000000.0;
    }

    public static void statisticalValidation(String method, String path, String body, int repetitions) throws IOException {
        double sum = 0, min = Double.MAX_VALUE, max = 0;

        for (int i = 0; i < repetitions; i++) {
            ControllerClient client = new ControllerClient("localhost", 1243);
            double rtt = client.measureRTT(method, path, body);
            sum += rtt;
            if (rtt < min) min = rtt;
            if (rtt > max) max = rtt;
        }

        double avg = sum / repetitions;
        System.out.println("Performed " + repetitions + " tests.");
        System.out.println("Total RTT = " + sum + " ms");
        System.out.println("Avg RTT = " + avg + " ms");
        System.out.println("Min RTT = " + min + " ms");
        System.out.println("Max RTT = " + max + " ms");
    }
}
