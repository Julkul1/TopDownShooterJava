package project.server;

import lombok.Getter;

import java.net.InetAddress;
import java.util.Objects;

public class ClientDataKey {
    @Getter
    private final InetAddress address;
    @Getter
    private final int port;

    public ClientDataKey(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ClientDataKey that = (ClientDataKey) obj;
        return port == that.port &&
                Objects.equals(address, that.address);
    }

    // hashCode method
    @Override
    public int hashCode() {
        return Objects.hash(address, port);
    }
}
