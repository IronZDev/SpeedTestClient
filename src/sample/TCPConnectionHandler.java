package sample;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class TCPConnectionHandler extends Thread
{
    private byte[] message;
    private Boolean running = true;
    private static Main guiClass;
    private String host;
    private String port;
    private Socket socket;
    private DataOutputStream out;

    @Override
    public void run() {
        try {
            out = new DataOutputStream(socket.getOutputStream());
            out.writeInt(message.length);
            System.out.println("Sent message size: "+message.length);
            while (running) {
                out.write(message);
            }
        } catch (Exception exception) {
            guiClass.writeTextOnError(exception.getMessage()+"\n");
        }
    }

    TCPConnectionHandler(Main guiClass, String host, String port, byte[] message) {
        this.message = message;
        TCPConnectionHandler.guiClass = guiClass;
        this.host = host;
        this.port = port;
        connect();
    }

    private void connect() {
        try {
            InetAddress address = InetAddress.getByName(host);
            socket = new Socket(address, Integer.parseInt(port));
            guiClass.writeText("Connection established with " + address.toString() + " on port " + port + ".\n");
            this.start();
        } catch(Exception e) {
            guiClass.writeTextOnError("Could not connect to the server: "+e.getMessage()+"\n");
        }
    }
    void disconnect() {
        try {
            running = false;
            if (out != null) out.close();
            if (socket != null) {
                socket.close();
                socket = null;
            }
            guiClass.writeText("Disconnected from server\n");
        } catch (Exception e) {
            guiClass.writeTextOnError("Could not disconnect: "+e.getMessage()+"\n");
        }
    }
}