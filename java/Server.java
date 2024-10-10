// Server.java
import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        // The port number on which the server listens
        int port = 1234;
        try {
            // Create a server socket on the specified port
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server is listening on port " + port);

            // Accept a connection from a client
            Socket clientSocket = serverSocket.accept();
            System.out.println("A client has connected");

            // Create input and output streams for communication
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Read a message from the client
            String message = in.readLine();
            System.out.println("Received from client: " + message);

            // Send a response to the client
            String response = "Hello, this is the server. You said: " + message;
            out.println(response);
            System.out.println("Sent to client: " + response);

            // Close the streams and the socket
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            // Handle any IO exceptions
            e.printStackTrace();
        }
    }
}

