package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

public class ServerThread implements Runnable {

    private final Socket clientConnection;
    private volatile boolean exitThread = false;
    private static boolean serverTerminated = false;
    private static final LinkedList<ServerThread> connectionsActive = new LinkedList<>();
    private String username;
    private static final ArrayList<String> usersList = new ArrayList<>();
    private static final String LOGIN_MESSAGE = "<SYSTEM>: Login";


    private DataInputStream datais;
    private DataOutputStream dataos;

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(4490)) {
            while (!serverTerminated) {
                ServerThread st = new ServerThread(serverSocket.accept());
                st.addReadWriteStreams();
                connectionsActive.add(st);

                Thread t = new Thread(st);
                t.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }



    private void closeServerThread(ServerThread serverThread) {

        connectionsActive.remove(serverThread);
        try {
            serverThread.datais.close();
            serverThread.dataos.close();
            serverThread.getClientConnection().close();
            removeUserFromList(username);
            writeMessageToAll(LOGIN_MESSAGE + String.join(",", usersList));
            writeMessageToAll("User disconnected " + username + "\n");
            System.out.println("Connection with client closed");
            serverThread.exitThread = true;
            if (connectionsActive.isEmpty()) {
                System.out.println("Server closed gracefully because all users left");
                serverTerminated = true;
                System.exit(0);
            }

        } catch (Exception e) {
            System.out.println("Server closed");
        }
    }


    private static void writeMessageToAll(String s) throws IOException {
        synchronized (connectionsActive) {
            for (ServerThread server : connectionsActive) {
                server.dataos.writeUTF(s);
            }
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
            writeMessageToAll((LOGIN_MESSAGE + String.join(",", usersList)));
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
        usersList.add(message);
    }

    public void removeUserFromList(String message) {
        if (usersList.contains(message)) {
            usersList.remove(message);
        }
    }

}
