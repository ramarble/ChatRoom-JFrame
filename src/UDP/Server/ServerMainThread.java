package UDP.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.LinkedList;

public class ServerMainThread implements Runnable {

    private static final boolean serverTerminated = false;
    private static final LinkedList<ClientDirectionInfo> CONNECTIONS_ACTIVE = new LinkedList<>();
    private static final LinkedList<String> USERNAMES = new LinkedList<>();
    private static final LinkedList<String> CHAT_HISTORY = new LinkedList<>();
    private static final String LOGIN_MESSAGE = "<SYSTEM>: Login";

    public static boolean isServerTerminated() {
        return serverTerminated;
    }

    public static boolean isClientInList(int port, InetAddress address) {

        synchronized (CONNECTIONS_ACTIVE) {
            if (!CONNECTIONS_ACTIVE.isEmpty()) {
                for (ClientDirectionInfo cd : CONNECTIONS_ACTIVE) {
                    if (cd.getDirection() == address && cd.getPort() == port) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String getMessageStringFromDatagramPacket(DatagramPacket datagramPacket) throws IOException {
        return new String(datagramPacket.getData(), 0, datagramPacket.getLength());
    }

    public void onInitialServerConnection(ClientDirectionInfo cdi) throws IOException {

        USERNAMES.add(cdi.getUsername());
        serveChatHistory(cdi);
        writeMessageToAll((LOGIN_MESSAGE + String.join(",", USERNAMES)));
        writeMessageToAll("User connected: " + cdi.getUsername() + "\n");
    }

    public void writeMessageToAll(String message) throws IOException {

        for (ClientDirectionInfo cdi : CONNECTIONS_ACTIVE) {
            new DatagramSocket().send(new DatagramPacket(message.getBytes(), 0, message.getBytes().length, cdi.getDirection(), cdi.getPort()));
        }
    }

    private static void updateChatHistory(String s) {
        if (CHAT_HISTORY.size() >= 10) {
            CHAT_HISTORY.removeFirst();
        }
        CHAT_HISTORY.add(s);
    }

    private static void serveChatHistory(ClientDirectionInfo cdi) throws IOException {
        for (String s : CHAT_HISTORY) {
            new DatagramSocket().send(new DatagramPacket(s.getBytes(), 0, s.getBytes().length, cdi.getDirection(), cdi.getPort()));
        }
    }

    private static ClientDirectionInfo getClientByPacket(DatagramPacket datagramPacket) throws IOException {
        for (ClientDirectionInfo cdi : CONNECTIONS_ACTIVE) {
            if (cdi.getDirection() == datagramPacket.getAddress() && cdi.getPort() == datagramPacket.getPort()) {
                return cdi;
            }
        }
        return null;
    }

    @Override
    public void run() {
        try {
            DatagramSocket datagramSocket = new DatagramSocket(4491);
            while (!serverTerminated) {

                DatagramPacket datagramPacket = new DatagramPacket(new byte[1024], 1024);
                datagramSocket.receive(datagramPacket);
                String messageReceived = getMessageStringFromDatagramPacket(datagramPacket);
                ClientDirectionInfo client = new ClientDirectionInfo(datagramPacket.getPort(), datagramPacket.getAddress());
                if (!isClientInList(client.getPort(), client.getDirection())) {
                    client.setUsername(messageReceived);
                    CONNECTIONS_ACTIVE.add(client);
                    onInitialServerConnection(client);
                } else {
                    client = getClientByPacket(datagramPacket);
                    assert client != null;
                    messageReceived = client.getUsername() + ": " + messageReceived;
                    updateChatHistory(messageReceived);
                    writeMessageToAll(messageReceived);
                }
            }

        } catch (IOException e) {

            System.out.println("UDP.Server terminated");
            throw new RuntimeException(e);
        }
    }
}
