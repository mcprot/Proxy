package mcprot.proxy.api.put;

import java.util.List;

public class Connection {
    private String proxy_id;
    private int version;
    private boolean forge;
    private String ip_address;
    private String date;
    private boolean success;

    public Connection(String proxy_id, int version, boolean forge, String ip_address, String date, boolean success) {
        this.proxy_id = proxy_id;
        this.version = version;
        this.forge = forge;
        this.ip_address = ip_address;
        this.date = date;
        this.success = success;
    }

    public String getProxy_id() {
        return proxy_id;
    }

    public int getVersion() {
        return version;
    }

    public boolean isForge() {
        return forge;
    }

    public String getIp_address() {
        return ip_address;
    }

    public String getDate() {
        return date;
    }

    public boolean isSuccess() {
        return success;
    }

    public static class Connections {
        private List<Connection> connections;

        public Connections(List<Connection> connections) {
            this.connections = connections;
        }
    }
}
