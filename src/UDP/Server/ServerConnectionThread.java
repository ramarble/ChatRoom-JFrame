package UDP.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

public class ServerConnectionThread implements Runnable {

    private final Socket clientConnection;
    private volatile boolean exitThread = false;
    private String username;
    private static final ArrayList<String> USERS_LIST = new ArrayList<>();
    private static final String LOGIN_MESSAGE = "<SYSTEM>: Login";
    private static final ArrayList<String> CHAT_HISTORY = new ArrayList<>();
    private DataInputStream datais;
    private DataOutputStream dataos;
    private volatile static LinkedList<ServerConnectionThread> connectionsActive;



    public ServerConnectionThread(Socket serverConnection, LinkedList<ServerConnectionThread> CONNECTIONS_ACTIVE) {

        clientConnection = serverConnection;
        connectionsActive = CONNECTIONS_ACTIVE;
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


    private void closeServerThread(ServerConnectionThread serverThread) {

        connectionsActive.remove(serverThread);
        try {
            serverThread.datais.close();
            serverThread.dataos.close();
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


    private static void writeMessageToAll(String s) throws IOException {

        updateChatHistory(s);
        for (ServerConnectionThread server : connectionsActive) {
            server.dataos.writeUTF(s);

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

    public void addReadWriteStreams() throws IOException {
        datais = new DataInputStream(clientConnection.getInputStream());
        dataos = new DataOutputStream(clientConnection.getOutputStream());
    }

    public void onInitialServerConnection(String messageCaught) throws IOException {

        username = messageCaught;
        addUserToList(username);
        serveChatHistory(dataos);
        writeMessageToAll((LOGIN_MESSAGE + String.join(",", USERS_LIST)));
        writeMessageToAll("User connected: " + username + "\n");
    }

    @Override
    public void run() {

        try {
            String messageCaught = datais.readUTF();
            onInitialServerConnection(messageCaught);

            while (!exitThread) {

                messageCaught = datais.readUTF();
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

    public Socket getClientConnection() {
        return clientConnection;
    }



}
