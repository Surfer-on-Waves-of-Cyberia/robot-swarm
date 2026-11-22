import java.io.*;
import java.net.*;

public class ControllerClient {

    public static void main(String[] args) {

        try (Socket socket = new Socket("localhost", 1243)) {

            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // example multiple line request via HTTP/1.1
            output.write("GET /status HTTP/1.1\r\n");
            output.write("Host: localhost\r\n");
            output.write("Connection: keep-alive\r\n");
            output.write("\r\n");
            output.flush();

            String line;
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException error) {
            error.printStackTrace();
        }
    }
}
