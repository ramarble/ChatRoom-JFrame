package TCP.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedList;


public class ServerMainThread implements Runnable{

    private static boolean serverTerminated = false;
    private static final LinkedList<ServerConnectionThread> CONNECTIONS_ACTIVE = new LinkedList<>();
    private static ServerSocket serverSocket;

    public static boolean isServerTerminated() {
        return serverTerminated;
    }

    public static void terminateServer() throws IOException {
        serverSocket.close();
        System.exit(0);
    }

    @Override
    public void run() {
            try {
                serverSocket = new ServerSocket(4490);
                while (!serverTerminated) {
                    ServerConnectionThread st = new ServerConnectionThread(
                            serverSocket.accept(),
                            CONNECTIONS_ACTIVE);
                    st.addReadWriteStreams();
                    CONNECTIONS_ACTIVE.add(st);

                    Thread t = new Thread(st);
                    t.start();
                }
            } catch (IOException e) {
                System.out.println("TCP.Server terminated");
                throw new RuntimeException(e);
            }

        }
    }
