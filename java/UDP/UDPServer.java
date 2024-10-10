// UDP Server code
import java.io.*;
import java.net.*;
public class UDPServer {
    public static void main(String[] args) throws IOException {
        DatagramSocket serverSocket = new DatagramSocket(5000);
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        while(true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String message = new String(receivePacket.getData());
            System.out.println("Received: " + message);
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            String reply = "Hello Client";
            sendData = reply.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            serverSocket.send(sendPacket);
        }
    }
}
