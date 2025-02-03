package Client;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientConnection implements Runnable {
    private Socket socket;
    private String username;
    private boolean connected = true;
    private DataOutputStream dos;
    private DataInputStream dis;
    private JTextArea textArea;

    private ClientConnection (Socket socket, ClientWindow clientWindow) throws IOException {
        this.socket = socket;
        this.dos = new DataOutputStream(socket.getOutputStream());
        this.dis = new DataInputStream(socket.getInputStream());
        this.username = username;
        this.textArea = clientWindow.getTextAreaChatHistory();
    }

    public Socket getSocket() {
        return socket;
    }

    public String getUsername() {
        return username;
    }

    public static ClientConnection createConnection(ClientWindow clientWindow) throws IOException {
        return new ClientConnection(new Socket("localhost", 4490), clientWindow);
    };

    public void sendMessage(String message) throws IOException {
        dos.writeUTF(message);
    }

    @Override
    public void run() {
        try {
            String incomingMessage;
            while (connected)  {
                incomingMessage = dis.readUTF();
                textArea.append(incomingMessage + "\n");
            }

        } catch (IOException _) {
        } finally {
            textArea.append("Connection terminated");
            System.err.println("Connection terminated");
        }
    }
}
