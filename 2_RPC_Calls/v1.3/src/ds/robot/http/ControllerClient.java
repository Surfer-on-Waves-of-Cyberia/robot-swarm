package ds.robot.http;

import java.io.*;
import java.net.*;

public class ControllerClient {

    private Socket socket;
    private BufferedWriter output;
    private BufferedReader input;
    public ControllerClient(String host, int port) throws IOException {
        int retries = 10;
            while (true) {
                try {
                    socket = new Socket("controller-server", port);
                    output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    break;
            } catch (IOException e) {
                    retries--;
                    if (retries <= 0) throw e;
                    System.out.println("Server not ready, retrying...");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Interrupted while waiting for server", ie);
                    }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("--- VALID CASES ---");
        ControllerClient client;

        client = new ControllerClient("controller-server", 1243);
        System.out.println("RTT GET /status = " + client.measureRTT("GET", "/status", null) + " ms");

        client = new ControllerClient("controller-server", 1243);
        System.out.println("RTT GET /captain = " + client.measureRTT("GET", "/captain", null) + " ms");

        client = new ControllerClient("controller-server", 1243);
        System.out.println("RTT GET /health = " + client.measureRTT("GET", "/health", null) + " ms");

        client = new ControllerClient("controller-server", 1243);
        System.out.println("RTT POST /le = " + client.measureRTT("POST", "/le", "Start Leader Election!") + " ms");

        System.out.println("--- INVALID CASES ---");

        client = new ControllerClient("controller-server", 1243);
        client.sendRequest("POST", "/status", "Body");

        client = new ControllerClient("controller-server", 1243);
        client.sendRequest("POST", "/captain", "Body");

        client = new ControllerClient("controller-server", 1243);
        client.sendRequest("POST", "/health", "Body");

        client = new ControllerClient("controller-server", 1243);
        client.sendRequest("GET", "/le", null);

        client = new ControllerClient("controller-server", 1243);
        client.sendRequest("GET", "/cake", null);

        // System.out.println("--- STATISTICAL VALIDATION ---");
        // statisticalValidation("POST", "/le", "Start Leader Election!", 500000);
    }

    private void sendRequest(String method, String path, String body) throws IOException {
        if (body == null) body = "";

        output.write(method + " " + path + " HTTP/1.0\r\n");
        output.write("Host: controller-server\r\n");
        if ("POST".equals(method)) {
            output.write("Content-Length: " + body.length() + "\r\n");
        }
        output.write("\r\n");
        if ("POST".equals(method)) {
            output.write(body);
        }
        output.flush();

        // OPTIMIZATION 3X after commenting these lines
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
        double[] rtts = new double[repetitions];
        double sum = 0, min = Double.MAX_VALUE, max = 0;

        for (int i = 0; i < repetitions; i++) {
            ControllerClient client = new ControllerClient("controller-server", 1243);
            double rtt = client.measureRTT(method, path, body);
            rtts[i] = rtt;
            sum += rtt;
            if (rtt < min) min = rtt;
            if (rtt > max) max = rtt;
        }

        double avg = sum / repetitions;

        /*
         * Mean Absolute Deviation - measurement of how much each RTT vary from average.
         * Standard Deviation - like MAD, but larger distances are "punished" more.
         * Confidence Interval - sureness that true average lies in some interval (95%).
         * Standard Error - measurement of how precise your average is.
         * Margin of Error - the "±" value in the confidence interval.
         */

        // Mean Absolute Deviation
        double mad = 0;
        for (double rtt : rtts) {
            mad += Math.abs(rtt - avg);
        }
        mad /= repetitions;

        // Standard Deviation
        double variance = 0;
        for (double rtt : rtts) {
            variance += Math.pow(rtt - avg, 2);
        }
        double stdev = Math.sqrt(variance / (repetitions - 1));

        // 95% Confidence Interval
        double t = 1.96; // approx for 95%
        double standardError = stdev / Math.sqrt(repetitions);
        double errorMargin = t * standardError;

        System.out.println("Performed " + repetitions + " tests.");
        System.out.println("Total RTT = " + String.format("%.2f", sum) + " ms");
        System.out.println("Avg RTT = " + String.format("%.4f", avg) + " ms");
        System.out.println("Min RTT = " + String.format("%.4f", min) + " ms");
        System.out.println("Max RTT = " + String.format("%.4f", max) + " ms");
        System.out.println("Mean Absolute Deviation = " + String.format("%.4f", mad) + " ms");
        System.out.println("Standard Deviation = " + String.format("%.4f", stdev) + " ms");
        System.out.println("95% Confidence Interval for mean = [" +
                           String.format("%.6f", avg - errorMargin) + ", " +
                           String.format("%.6f", avg + errorMargin) + "] ms (±" +
                           String.format("%.6f", errorMargin) + " ms)");
    }
}
