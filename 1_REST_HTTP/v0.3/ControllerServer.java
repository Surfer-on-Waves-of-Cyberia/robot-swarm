import java.io.*;
import java.net.*;
import java.util.*;

public class ControllerServer {

    // TODO: a separate class for data structure
    static int robotsActive = 5;       // integer number
    static String captain = "King";    // capitan name
    static String status = "OK";       // "OK", "STOPPED", "PAUSED",

    public static void main(String[] args) {
        int port = 1243;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Waiting for connections on port " + port + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
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

            // at least handle request once, then only if it is HTTP/1.1 with keep-alive
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

        // read request line
        String line = input.readLine();

        // if request is empty or client closed the data stream - close connection
        if (line == null || line.isEmpty()) {
            return false;
        }

        // System.out.println("Request line: " + line); // debugging

        // splitting first request line via whitespace
        String[] requestLines = line.split(" ");
        // only the 3 words in the first request line are valid
        if (requestLines.length != 3) {
            sendResponse(output, "400 Bad Request", "text/plain", "Malformed request line", keepAlive);
            return false;
        }

        String method = requestLines[0];    // GET or POST
        String path = requestLines[1];      // /path for routing
        String version = requestLines[2];   // HTTP version

        // read headers
        Map<String, String> headers = new HashMap<>();

        String headerLine = input.readLine();
        while (headerLine != null && !headerLine.isEmpty()) {
            int index = headerLine.indexOf(':');
            if (index != -1) {
                String name = headerLine.substring(0, index).trim().toLowerCase();
                String value = headerLine.substring(index + 1).trim();
                headers.put(name, value); // add to the hashmap name:value
            }

            headerLine = input.readLine();
        }

        // determine keep-alive or close logic
        if (version.equals("HTTP/1.1")) {
            keepAlive = !"close".equalsIgnoreCase(headers.get("connection"));
        } else {
            keepAlive = false; // default close - HTTP/1.0 case
        }


        // read body if POST
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


        // routing without further logic
        String responseBody;

        // TODO: validate allowed methods per path in one place
        // change: if-else to switch
        switch (path) {
            case "/status":
                if (!method.equals("GET")) {
                    sendResponse(output, "405 Method Not Allowed", "text/plain", "Only GET allowed", keepAlive);
                    return keepAlive;
                }
                responseBody = "{ \"robotsActive\": " + robotsActive + " }";
                sendResponse(output, "200 OK", "application/json", responseBody, keepAlive);
                break;

            case "/captain":
                if (!method.equals("GET")) {
                    sendResponse(output, "405 Method Not Allowed", "text/plain", "Only GET allowed", keepAlive);
                    return keepAlive;
                }
                responseBody = "{ \"captain\": " + captain + " }";
                sendResponse(output, "200 OK", "application/json", responseBody, keepAlive);
                break;

            case "/health":
                if (!method.equals("GET")) {
                    sendResponse(output, "405 Method Not Allowed", "text/plain", "Only GET allowed", keepAlive);
                    return keepAlive;
                }
                responseBody = "{ \"status\": " + status + " }";
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

    // response generation method
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
