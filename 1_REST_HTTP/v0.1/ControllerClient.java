import java.io.*;
import java.net.*;

public class ControllerClient {

    public static void main(String[] args) {

        // trying to connect to the server with ip and port
        try (Socket socket = new Socket("localhost", 1243)) {

            // input and output data streams to get responses and send requests
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // writing a request
            output.write("Hello TCP World!\n");
            output.flush();

            // waiting for response and reading it
            String response = input.readLine();
            System.out.println("I've got a response: " + response);

            // closing the connection server-side?
        } catch (IOException error) {
            error.printStackTrace(); // if an error happened - print error type, message and full stack trace
        }
    }
}
