package sample;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPConnectionHandler extends Thread {
    private byte[] message;
    private Boolean running = true;
    private static Main guiClass;
    private String host;
    private Integer port;
    private DatagramSocket socket;
    private InetAddress address;
    @Override
    public void run() {
        byte[] messageSize = Integer.toString(message.length).getBytes();
        DatagramPacket packet = new DatagramPacket(messageSize, messageSize.length, address, port);
        try {
            socket.send(packet);
            packet = new DatagramPacket(message, message.length, address, port);
            while (running) {
                socket.send(packet);
            }
        } catch (IOException e) {
            guiClass.writeTextOnError(e.getMessage());
        }
    }
    UDPConnectionHandler(Main guiClass, String host, String port, byte[] message) {
        UDPConnectionHandler.guiClass = guiClass;
        this.host = host;
        this.port = Integer.parseInt(port);
        this.message = message;
        connect();
    }
    private void connect() {
        try {
            address = InetAddress.getByName(host);
            // Create Datagram Socket
            socket = new DatagramSocket();
            guiClass.writeText("UDP socket created");
            this.start();
        } catch(Exception e) {
            guiClass.writeTextOnError("Could not connect to the server: "+e.getMessage());
        }
    }
    void disconnect() {
        running = false;
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
            guiClass.writeText("Socket closed");
        } catch (Exception e) {
            guiClass.writeTextOnError("Could not close the socket: "+e.getMessage());
        }
    }
}
