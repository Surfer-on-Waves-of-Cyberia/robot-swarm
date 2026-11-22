import java.io.*;
import java.net.*;
import java.util.*;

public class ControllerServer {

    private static final ControllerData controllerData = new ControllerData();
    private final ServerSocket socket;

    public ControllerServer(int port) throws IOException {
        socket = new ServerSocket(port);
    }

    public static void main(String[] args) {
        controllerData.setRobotsActive(5);
        controllerData.setCaptain("King");
        controllerData.setHealth("OK");

        int port = 1243;

        try {
            ControllerServer server = new ControllerServer(1243);
            System.out.println("Waiting for connections on port " + port + "...");

            while (true) {
                Socket clientSocket = server.socket.accept();
                System.out.println("New connection: " + clientSocket.getInetAddress());
                handleConnection(clientSocket);
            }

        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    private static void handleConnection(Socket clientSocket) {
        try (
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))
        ) {
            boolean keepAlive;

            do {
                keepAlive = handleRequest(input, output);
            } while (keepAlive);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try { clientSocket.close(); } catch (IOException ignored) {}
            System.out.println("Connection closed");
        }
    }

    private static boolean handleRequest(BufferedReader input, BufferedWriter output) throws IOException {

        boolean keepAlive = false;

        String line = input.readLine();

        if (line == null || line.isEmpty()) {
            return false;
        }

        String[] requestLines = line.split(" ");
        if (requestLines.length != 3) {
            sendResponse(output, "400 Bad Request", "text/plain", "Malformed request line", keepAlive);
            return false;
        }

        String method = requestLines[0];
        String path = requestLines[1];
        String version = requestLines[2];

        Map<String, String> headers = new HashMap<>();

        String headerLine = input.readLine();
        while (headerLine != null && !headerLine.isEmpty()) {
            int index = headerLine.indexOf(':');
            if (index != -1) {
                String name = headerLine.substring(0, index).trim().toLowerCase();
                String value = headerLine.substring(index + 1).trim();
                headers.put(name, value);
            }

            headerLine = input.readLine();
        }

        if (version.equals("HTTP/1.1")) {
            keepAlive = !"close".equalsIgnoreCase(headers.get("connection"));
        } else {
            keepAlive = false;
        }

        String body = "";
        if (method.equals("POST")) {
            String cl = headers.get("content-length");
            if (cl == null) {
                sendResponse(output, "400 Bad Request", "text/plain", "POST requires Content-Length", keepAlive);
                return keepAlive;
            }

            int contentLength = Integer.parseInt(cl);
            char[] buffer = new char[contentLength];
            int read = input.read(buffer);
            body = new String(buffer, 0, read);
        }


        String responseBody;
        switch (path) {
            case "/status":
                if (!method.equals("GET")) {
                    sendResponse(output, "405 Method Not Allowed", "text/plain", "Only GET allowed", keepAlive);
                    return keepAlive;
                }
                responseBody = "{ \"robotsActive\": " + controllerData.getRobotsActive() + " }";
                sendResponse(output, "200 OK", "application/json", responseBody, keepAlive);
                break;

            case "/captain":
                if (!method.equals("GET")) {
                    sendResponse(output, "405 Method Not Allowed", "text/plain", "Only GET allowed", keepAlive);
                    return keepAlive;
                }
                responseBody = "{ \"captain\": " + controllerData.getCaptain() + " }";
                sendResponse(output, "200 OK", "application/json", responseBody, keepAlive);
                break;

            case "/health":
                if (!method.equals("GET")) {
                    sendResponse(output, "405 Method Not Allowed", "text/plain", "Only GET allowed", keepAlive);
                    return keepAlive;
                }
                responseBody = "{ \"health\": " + controllerData.getHealth() + " }";
                sendResponse(output, "200 OK", "application/json", responseBody, keepAlive);
                break;

            case "/le":
                if (!method.equals("POST")) {
                    sendResponse(output, "405 Method Not Allowed", "text/plain", "Only POST allowed", keepAlive);
                    return keepAlive;
                }
                responseBody = "{ \"leCreated\": true, \"body\": " + body + " }";
                sendResponse(output, "200 OK", "application/json", responseBody, keepAlive);
                break;

            default:
                sendResponse(output, "404 Not Found", "text/plain", "Path not found", keepAlive);
                break;
        }

        return keepAlive;
    }

    private static void sendResponse(BufferedWriter out, String status, String contentType, String body, boolean keepAlive) throws IOException {
        out.write("HTTP/1.1 " + status + "\r\n");
        out.write("Content-Type: " + contentType + "\r\n");
        out.write("Content-Length: " + body.length() + "\r\n");
        out.write("Connection: " + (keepAlive ? "keep-alive" : "close") + "\r\n");
        out.write("\r\n");
        out.write(body);
        out.flush();
    }
}
