package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Array;
import java.util.ArrayList;
import java.util.LinkedList;

public class ServerThread implements Runnable {

    private final Socket clientConnection;
    private volatile boolean exitThread = false;
    private static boolean serverTerminated = false;
    private static final LinkedList<ServerThread> CONNECTIONS_ACTIVE = new LinkedList<>();
    private String username;
    private static final ArrayList<String> USERS_LIST = new ArrayList<>();
    private static final String LOGIN_MESSAGE = "<SYSTEM>: Login";
    private static final ArrayList<String> CHAT_HISTORY = new ArrayList<>();


    private DataInputStream datais;
    private DataOutputStream dataos;

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(4490)) {
            while (!serverTerminated) {
                ServerThread st = new ServerThread(serverSocket.accept());
                st.addReadWriteStreams();
                CONNECTIONS_ACTIVE.add(st);

                Thread t = new Thread(st);
                t.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }



    private void closeServerThread(ServerThread serverThread) {

        CONNECTIONS_ACTIVE.remove(serverThread);
        try {
            serverThread.datais.close();
            serverThread.dataos.close();
            serverThread.getClientConnection().close();
            removeUserFromList(username);
            writeMessageToAll(LOGIN_MESSAGE + String.join(",", USERS_LIST));
            writeMessageToAll("User disconnected " + username + "\n");
            System.out.println("Connection with client closed");
            serverThread.exitThread = true;
            if (CONNECTIONS_ACTIVE.isEmpty()) {
                System.out.println("Server closed gracefully because all users left");
                serverTerminated = true;
                System.exit(0);
            }

        } catch (Exception e) {
            System.out.println("Server closed");
        }
    }


    private static void writeMessageToAll(String s) throws IOException {
        synchronized (CONNECTIONS_ACTIVE) {
            updateChatHistory(s);
            for (ServerThread server : CONNECTIONS_ACTIVE) {
                server.dataos.writeUTF(s);
            }
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

    private void addReadWriteStreams() throws IOException {
        datais = new DataInputStream(clientConnection.getInputStream());
        dataos = new DataOutputStream(clientConnection.getOutputStream());
    }

    @Override
    public void run() {

        try {
            String messageCaught = datais.readUTF();
            username = messageCaught;
            addUserToList(username);
            serveChatHistory(dataos);
            writeMessageToAll((LOGIN_MESSAGE + String.join(",", USERS_LIST)));
            writeMessageToAll("User connected: " + username + "\n");
            while (!exitThread) {

                messageCaught = datais.readUTF();
                writeMessageToAll(username + ": " + messageCaught);

                if (messageCaught.equals("exit")) {
                    closeServerThread(this);
                }
            }
        } catch (IOException e) {
            System.out.println("Client closed forcefully");
        } finally {
            closeServerThread(this);
        }
    }

    public ServerThread(Socket serverConnection) {
        clientConnection = serverConnection;
    }

    public Socket getClientConnection() {
        return clientConnection;
    }


    public void addUserToList(String message) {
        USERS_LIST.add(message);
    }

    public void removeUserFromList(String message) {
        if (USERS_LIST.contains(message)) {
            USERS_LIST.remove(message);
        }
    }

}
