package UDP.Client;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class ClientConnection implements Runnable {
    private final String username;
    private final DatagramSocket datagramSocket;
    private final JTextArea textArea;
    private final JTextArea userList;
    private static final String LOGIN_MESSAGE = "<SYSTEM>: Login";
    private volatile boolean connected = true;
    private final DatagramPacket datagramPacket = new DatagramPacket(new byte[1024], 1024);

    private ClientConnection (DatagramSocket datagramSocket, String username, ChatWindow clientWindow) throws IOException {
        this.datagramSocket = datagramSocket;
        this.textArea = clientWindow.getTextAreaChatHistory();
        this.username = username;
        this.userList = clientWindow.getTextAreaUserList();
    }

    public static ClientConnection createConnection(String username, ChatWindow clientWindow) throws IOException {
        return new ClientConnection(new DatagramSocket(), username,  clientWindow);
    };

    public void sendMessage(String message) throws IOException {
        datagramSocket.send(new DatagramPacket(message.getBytes(), message.length(), InetAddress.getByName("localhost"), 4491));
    }

    public void login(String username) throws IOException {
        sendMessage(username);
    }


    public void updateUserList(String message) {
        userList.setText("Usuarios: \n");
        message = message.substring(LOGIN_MESSAGE.length());
        String[] usersList = message.split(",");
        for (String user : usersList) {
            userList.append(user + "\n");
        }
    }

    public void stopClientConnection() throws IOException {
        this.connected = false;
    }

    public String getMessageStringFromDatagramPacket(DatagramPacket datagramPacket) throws IOException {
        return new String(datagramPacket.getData(), 0, datagramPacket.getLength());
    }

    @Override
    public void run() {
        try {
            login(username);
            Thread.sleep(250);

            while (connected) {
                datagramSocket.receive(datagramPacket);

                String messageReceived = getMessageStringFromDatagramPacket(datagramPacket);
                if (messageReceived.contains("<SYSTEM>: Login")) {
                    updateUserList(messageReceived);
                } else {
                    textArea.append(messageReceived + "\n");
                }
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            textArea.append("Connection terminated");
            System.err.println("Connection terminated");
        }
    }
}
