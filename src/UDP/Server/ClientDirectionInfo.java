package UDP.Server;

import java.net.InetAddress;

public class ClientDirectionInfo {
    private final int port;
    private final InetAddress direction;
    private String username;

    public void setUsername(String username) {
        this.username = username;
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
