package TCP.Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

public class ServerConnectionThread implements Runnable {

    private static final ArrayList<String> USERS_LIST = new ArrayList<>();
    private static final String LOGIN_MESSAGE = "<SYSTEM>: Login";
    private static final ArrayList<String> CHAT_HISTORY = new ArrayList<>();
    private volatile static LinkedList<ServerConnectionThread> connectionsActive;


    private final Socket clientConnection;
    private volatile boolean exitThread = false;
    private String username;
    private final DataInputStream dis;
    private final DataOutputStream dos;

    public ServerConnectionThread(Socket serverConnection, LinkedList<ServerConnectionThread> CONNECTIONS_ACTIVE) throws IOException {
        clientConnection = serverConnection;
        connectionsActive = CONNECTIONS_ACTIVE;
        dis = new DataInputStream(clientConnection.getInputStream());
        dos = new DataOutputStream(clientConnection.getOutputStream());
    }

    public void addUserToList(String message) {
        synchronized (USERS_LIST) {
            USERS_LIST.add(message);
        }
    }

    public void removeUserFromList(String message) {
        synchronized (USERS_LIST) {
            USERS_LIST.remove(message);
        }
    }


    public Socket getClientConnection() {
        return clientConnection;
    }


    private void closeServerThread(ServerConnectionThread serverThread) {

        connectionsActive.remove(serverThread);
        try {
            serverThread.dis.close();
            serverThread.dos.close();
            serverThread.getClientConnection().close();
            removeUserFromList(username);
            writeMessageToAll(LOGIN_MESSAGE + String.join(",", USERS_LIST));
            writeMessageToAll("User disconnected " + username + "\n");
            System.out.println("Connection with client closed");
            serverThread.exitThread = true;
            if (connectionsActive.isEmpty()) {
                System.out.println("TCP.Server closed gracefully because all users left");
                ServerMainThread.terminateServer();
                System.exit(0);
            }

        } catch (Exception e) {
            System.out.println("TCP.Server closed");
        }
    }


    private static void writeMessageToAll(String message) throws IOException {
        updateChatHistory(message);
        for (ServerConnectionThread server : connectionsActive) {
            server.dos.writeUTF(message);
        }
    }

    private static void updateChatHistory(String s) {
        if (CHAT_HISTORY.size() >= 10) {
            CHAT_HISTORY.removeFirst();
        }
        CHAT_HISTORY.add(s);
    }

    private static void serveChatHistory(DataOutputStream dos) throws IOException {
        for (String s : CHAT_HISTORY) {
            dos.writeUTF(s);
        }
    }

    public void onInitialServerConnection(String messageCaught) throws IOException {
        username = messageCaught;
        if (!isUserLoggedIn(username)) {
            addUserToList(username);
            serveChatHistory(dos);
            writeMessageToAll((LOGIN_MESSAGE + String.join(",", USERS_LIST)));
            writeMessageToAll("User connected: " + username + "\n");
        } else {
            dos.writeUTF(username + " is already logged in");
            exitThread = true;
        }
    }

    public boolean isUserLoggedIn(String username) throws IOException {
        for (String s : USERS_LIST) {
            if (s.equals(username)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void run() {


        String messageCaught;
        try {
            messageCaught = dis.readUTF();
            onInitialServerConnection(messageCaught);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            while (!exitThread) {
                messageCaught = dis.readUTF();
                writeMessageToAll(username + ": " + messageCaught);

                if (messageCaught.equals("exit")) {
                    closeServerThread(this);
                }
            }
        } catch (IOException e) {
            System.out.println("TCP.Client closed forcefully");
        } finally {
            closeServerThread(this);
        }
    }

}
