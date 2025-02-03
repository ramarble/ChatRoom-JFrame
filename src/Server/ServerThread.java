package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class ServerThread implements Runnable {

    private final Socket clientConnection;
    private static String[] userList;
    private volatile boolean exitThread = false;
    private static volatile boolean serverTerminated = false;
    private static final LinkedList<ServerThread> connectionsActive = new LinkedList<>();

    private DataInputStream datais;
    private DataOutputStream dataos;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(4490)) {
            while (!serverTerminated) {
                Thread t = new Thread(new ServerThread(serverSocket.accept()));
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
            System.out.println("Connection with client closed");
            serverThread.exitThread = true;
            if (connectionsActive.isEmpty()) {
                System.out.println("Server closed gracefully because all users left");
                serverTerminated = true;
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

    private void addReadWriteStreams(ServerThread s) throws IOException {
        s.datais = new DataInputStream(s.clientConnection.getInputStream());
        s.dataos = new DataOutputStream(s.clientConnection.getOutputStream());
    }

    @Override
    public void run() {

        try {
            addReadWriteStreams(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String messageCaught;
        connectionsActive.add(this);

        while (!exitThread) {
            try {

                messageCaught = datais.readUTF();
                System.out.println(messageCaught );

                writeMessageToAll(messageCaught );

                if (messageCaught.equals("exit")) {
                    closeServerThread(this);
                }

            } catch (IOException e) {

                System.out.println("Client closed forcefully");

            } finally {
                closeServerThread(this);
            }
        }
    }

    public ServerThread(Socket serverConnection) {
        clientConnection = serverConnection;
    }

    public Socket getClientConnection() {
        return clientConnection;
    }

}
