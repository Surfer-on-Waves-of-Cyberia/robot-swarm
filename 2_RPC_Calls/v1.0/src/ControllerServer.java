import java.io.*;
import java.net.*;
import java.util.*;

public class ControllerServer {

    private static final ControllerData controllerData = new ControllerData();
    private final ServerSocket socket;

    public ControllerServer(int port) throws IOException {
        socket = new ServerSocket(port);
    }

    public static void main(String[] args) throws IOException {
        controllerData.setRobotsActive(5);
        controllerData.setCaptain("King");
        controllerData.setHealth("OK");

        int port = 1243;
        ControllerServer server = new ControllerServer(port);
        System.out.println("Waiting for connections on port " + port + "...");

        while (true) {
            Socket clientSocket = server.socket.accept();
            handleConnection(clientSocket);
        }
    }

    private static void handleConnection(Socket clientSocket) {
        try (
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))
        ) {
            handleRequest(input, output);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try { clientSocket.close(); } catch (IOException ignored) {}
        }
    }

    private static void handleRequest(BufferedReader input, BufferedWriter output) throws IOException {
        String line = input.readLine();
        if (line == null || line.isEmpty()) return;

        String[] requestParts = line.split(" ");
        if (requestParts.length != 3) {
            sendResponse(output, "400 Bad Request", "text-plain", "Malformed request line");
            return;
        }

        String method = requestParts[0];
        String path = requestParts[1];

        Map<String, String> headers = new HashMap<>();
        String headerLine;
        while ((headerLine = input.readLine()) != null && !headerLine.isEmpty()) {
            int idx = headerLine.indexOf(':');
            if (idx != -1) {
                headers.put(headerLine.substring(0, idx).trim().toLowerCase(),
                            headerLine.substring(idx + 1).trim());
            }
        }

        String body = "";
        if ("POST".equals(method)) {
            String cl = headers.get("content-length");
            if (cl != null) {
                int contentLength = Integer.parseInt(cl);
                char[] buffer = new char[contentLength];
                int read = 0;
                while (read < contentLength) {
                    int r = input.read(buffer, read, contentLength - read);
                    if (r == -1) break;
                    read += r;
                }
                body = new String(buffer, 0, read);
            }
        }

        switch (path) {
            case "/status":
                if (!"GET".equals(method)) { sendResponse(output, "405 Method Not Allowed", "text/plain", "Only GET is allowed"); return; }
                sendResponse(output, "200 OK", "json", "{ \"robotsActive\": " + controllerData.getRobotsActive() + " }");
                break;
            case "/captain":
                if (!"GET".equals(method)) { sendResponse(output, "405 Method Not Allowed", "text/plain", "Only GET is allowed"); return; }
                sendResponse(output, "200 OK", "json", "{ \"captain\": \"" + controllerData.getCaptain() + "\" }");
                break;
            case "/health":
                if (!"GET".equals(method)) { sendResponse(output, "405 Method Not Allowed", "text/plain", "Only GET is allowed"); return; }
                sendResponse(output, "200 OK", "json", "{ \"health\": \"" + controllerData.getHealth() + "\" }");
                break;
            case "/le":
                if (!"POST".equals(method)) { sendResponse(output, "405 Method Not Allowed", "text/plain", "Only POST is allowed"); return; }
                sendResponse(output, "200 OK", "json", "{ \"leBegun\": true, \"body\": \"" + body + "\" }");
                break;
            default:
                sendResponse(output, "404 Not Found", "text-plain", "Path not found");
        }
    }

    private static void sendResponse(BufferedWriter out, String status, String contentType, String body) throws IOException {
        out.write("HTTP/1.0 " + status + "\r\n");
        out.write("Content-Type: " + contentType + "\r\n");
        out.write("Content-Length: " + body.length() + "\r\n");
        out.write("\r\n");
        out.write(body);
        out.flush();
    }
}
