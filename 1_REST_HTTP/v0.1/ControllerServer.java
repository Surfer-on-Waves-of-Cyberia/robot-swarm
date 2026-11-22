import java.io.*;
import java.net.*;

public class ControllerServer {

    public static void main(String[] args) {
        int port = 1243;

        // new server socket for ALL inputs
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Waiting for connections...");

            // endless loop waiting for new connections
            while (true) {
                // if connected, create new socket ONLY for this client
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection: " + clientSocket.getInetAddress());

                // handling logic
                handleClient(clientSocket);
            }

        } catch (IOException error) {
            error.printStackTrace(); // if an error happened - print error type, message and full stack trace
        }
    }


    private static void handleClient(Socket clientSocket) {
        try (
            // input and output data streams to get requests and send responses
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        ) {
            // waiting for request and reading it
            String request = input.readLine();
            System.out.println("New request received: " + request);

            // simplified logic of request generation, later we will implement the REST logic in here
            String response = "Controller received: " + request + "\n";

            // writing and sending the response to client
            output.write(response);
            output.flush();

        } catch (IOException error) {
            error.printStackTrace(); // if an error happened - print error type, message and full stack trace
        } finally {
            try { clientSocket.close(); } catch (IOException ignored) {} // close the connection
        }
    }
}
