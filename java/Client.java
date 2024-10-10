// Client.java
import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        // The host name and the port number of the server
        String host = "localhost";
        int port = 1234;
        try {
            // Create a client socket and connect to the server
            Socket clientSocket = new Socket(host, port);
            System.out.println("Connected to the server on " + host + ":" + port);

            // Create input and output streams for communication
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Send a message to the server
            String message = "Hello, this is the client. How are you?";
            out.println(message);
            System.out.println("Sent to server: " + message);

            // Receive a response from the server
            String response = in.readLine();
            System.out.println("Received from server: " + response);

            // Close the streams and the socket
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            // Handle any IO exceptions
            e.printStackTrace();
        }
    }}


