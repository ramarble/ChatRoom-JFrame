package Client;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientConnection implements Runnable {
    private final String username;
    private final DataOutputStream dos;
    private final DataInputStream dis;
    private final JTextArea textArea;
    private final JTextArea userList;
    private static final String LOGIN_MESSAGE = "<SYSTEM>: Login";

    private ClientConnection (Socket socket, String username, ClientWindow clientWindow) throws IOException {
        this.dos = new DataOutputStream(socket.getOutputStream());
        this.dis = new DataInputStream(socket.getInputStream());
        this.textArea = clientWindow.getTextAreaChatHistory();
        this.username = username;
        this.userList = clientWindow.getTextAreaUserList();
    }

    public static ClientConnection createConnection(String username, ClientWindow clientWindow) throws IOException {
        return new ClientConnection(new Socket("localhost", 4490), username,  clientWindow);
    };

    public void sendMessage(String message) throws IOException {
        dos.writeUTF(message);
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


    @Override
    public void run() {
        try {
            login(username);
            Thread.sleep(250);
            boolean connected = true;
            while (connected) {

                String message = dis.readUTF();

                if (message.contains("<SYSTEM>: Login")) {
                    updateUserList(message);
                } else {
                    textArea.append(message + "\n");
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
