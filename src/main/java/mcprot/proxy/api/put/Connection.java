package mcprot.proxy.api.put;

import java.util.List;

public class Connection {
    private String proxy_id;
    private int version;
    private boolean forge;
    private String ip_address;
    private String date_connect;
    private String date_disconnect;
    private boolean success;
    private double bytes_ingress;
    private double bytes_egress;

    public Connection(String proxy_id, int version, boolean forge, String ip_address, String date_connect, boolean success) {
        this.proxy_id = proxy_id;
        this.version = version;
        this.forge = forge;
        this.ip_address = ip_address;
        this.date_connect = date_connect;
        this.date_disconnect = date_connect;
        this.bytes_ingress = 0;
        this.bytes_egress = 0;
        this.success = success;
    }

    public double getBytes_ingress() {
        return bytes_ingress;
    }

    public void setBytes_ingress(double bytes_ingress) {
        this.bytes_ingress = bytes_ingress;
    }

    public double getBytes_egress() {
        return bytes_egress;
    }

    public void setBytes_egress(double bytes_egress) {
        this.bytes_egress = bytes_egress;
    }

    public void addBytes_egress(double bytes_egress) {
        this.bytes_egress += bytes_egress;
    }

    public void addBytes_ingress(double bytes_ingress) {
        this.bytes_ingress += bytes_ingress;
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

    public void setDate_disconnect(String date_disconnect) {
        this.date_disconnect = date_disconnect;
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
