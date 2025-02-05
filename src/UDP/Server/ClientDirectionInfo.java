package UDP.Server;

import java.net.InetAddress;

public class ClientDirectionInfo {
    private final int port;
    private final InetAddress direction;
    private String username;
    private boolean alreadyConnected = false;

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAlreadyConnected() {
        return alreadyConnected;
    }

    public void setAlreadyConnected(boolean alreadyConnected) {
        this.alreadyConnected = alreadyConnected;
    }

    public int getPort() {
        return port;
    }

    public InetAddress getDirection() {
        return direction;
    }

    public String getUsername() {
        return username;
    }

    public ClientDirectionInfo(int port, InetAddress direction) {
        this.port = port;
        this.direction = direction;
    }


}
