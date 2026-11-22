import java.io.*;
import java.net.*;

public class ControllerClient {

    // variables used for connection and communication
    private Socket socket;
    private BufferedWriter output;
    private BufferedReader input;

    // constructor to get these things away from main
    public ControllerClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public static void main(String[] args) {
        try {
            // establish new connection
            ControllerClient client = new ControllerClient("localhost", 1243);

            // test case 1
            long start = System.nanoTime();
            client.sendRequest("GET", "/status", null);
            long end = System.nanoTime();
            System.out.println("RTT for GET /status = " + (end - start)/1000000.0 + " ms");
            System.out.println();

            // test case 2
            client = new ControllerClient("localhost", 1243);
            start = System.nanoTime();
            client.sendRequest("GET", "/captain", null);
            end = System.nanoTime();
            System.out.println("RTT for GET /captain = " + (end - start)/1000000.0 + " ms");
            System.out.println();

            // test case 3
            client = new ControllerClient("localhost", 1243);
            start = System.nanoTime();
            client.sendRequest("GET", "/health", null);
            end = System.nanoTime();
            System.out.println("RTT for GET /health = " + (end - start)/1000000.0 + " ms");
            System.out.println();

            // test case 4
            client = new ControllerClient("localhost", 1243);
            start = System.nanoTime();
            client.sendRequest("POST", "/le", "HelloFromClient");
            end = System.nanoTime();
            System.out.println("RTT for POST /le = " + (end - start)/1000000.0 + " ms");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(String method, String path, String body) throws IOException {
        // for get-requests
        if (body == null) body = "";

        // althrough it can handle http/1.0
        output.write(method + " " + path + " HTTP/1.1\r\n");
        output.write("Host: localhost\r\n");

        // automatical sontent-length
        if (method.equals("POST")) {
            output.write("Content-Length: " + body.length() + "\r\n");
        }

        // althrough keepAlive can be used
        output.write("Connection: close\r\n");
        output.write("\r\n");

        // only for post-requests
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
}
