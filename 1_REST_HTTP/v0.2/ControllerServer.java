import java.io.*;
import java.net.*;
import java.util.*;

public class ControllerServer {

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
            boolean keepAlive = false;

            // at least handle request once, then only if it is HTTP/1.1
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

        // if request is empty - close connection
        if (line == null || line.isEmpty()) {
            return false;
        }

        // System.out.println("Request line: " + line); // debugging

        String[] requestLines = line.split(" ");
        // only the 3 lines requests are valid - DEBUGGING
        if (requestLines.length != 3) {
            sendResponse(output, "400 Bad Request", "text/plain", "Malformed request line", keepAlive);
            return false;
        }

        String method = requestLines[0];
        String path = requestLines[1];
        String version = requestLines[2];   // HTTP/1.0 or HTTP/1.1


        // read headers
        List<String> headerNames = new ArrayList<>();
        List<String> headerValues = new ArrayList<>();

        String headerLine;
        while ((headerLine = input.readLine()) != null && !headerLine.isEmpty()) {
            int index = headerLine.indexOf(':');
            if (index != -1) {
                headerNames.add(headerLine.substring(0, index).trim().toLowerCase());
                headerValues.add(headerLine.substring(index + 1).trim());
            }
        }

        // determine keep-alive or close logic
        if (version.equals("HTTP/1.1")) {
            keepAlive = !"close".equalsIgnoreCase(getHeader("connection", headerNames, headerValues));
        } else { // HTTP/1.0
            keepAlive = false; // default close
        }


        // read body if POST
        String body = "";
        if (method.equals("POST")) {
            String cl = getHeader("content-length", headerNames, headerValues);
            if (cl == null) {
                sendResponse(output, "400 Bad Request", "text/plain", "POST requires Content-Length", keepAlive);
                return keepAlive;
            }

            int contentLength = Integer.parseInt(cl);
            char[] buffer = new char[contentLength];
            int read = input.read(buffer);
            body = new String(buffer, 0, read);
        }


        // routing
        String responseBody;

        // GET /status
        if (path.equals("/status") && method.equals("GET")) {
            responseBody = "{ \"robotsActive\": 5 }";
            sendResponse(output, "200 OK", "application/json", responseBody, keepAlive);

        // GET /capitan
        } else if (path.equals("/capitan") && method.equals("GET")) {
            responseBody = "{ \"captain\": \"Unit42\" }";
            sendResponse(output, "200 OK", "application/json", responseBody, keepAlive);

        // GET /health
        } else if (path.equals("/health") && method.equals("GET")) {
            responseBody = "{ \"status\": \"OK\" }";
            sendResponse(output, "200 OK", "application/json", responseBody, keepAlive);

        // POST /le
        } else if (path.equals("/le") && method.equals("POST")) {
            responseBody = "{ \"leCreated\": true, \"body\": \"" + body + "\" }"; // nur nachricht
            sendResponse(output, "200 OK", "application/json", responseBody, keepAlive);

        // default false request handling
        } else {
            sendResponse(output, "404 Not Found", "text/plain", "Path not found", keepAlive);
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

    // get the header logic for closing logic
    private static String getHeader(String name, List<String> headerNames, List<String> headerValues) {
        for (int i = 0; i < headerNames.size(); i++) {
            if (headerNames.get(i).equalsIgnoreCase(name)) {
                return headerValues.get(i);
            }
        }
        return null;
    }
}
