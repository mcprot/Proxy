package mcprot.proxy.api.put;

import java.util.List;

public class Analytic {

    private String proxy_id;
    private double bandwidth = 0;
    private int connections = 0;

    public Analytic(String proxy_id) {
        this.proxy_id = proxy_id;
    }

    public String getProxy_id() {
        return proxy_id;
    }

    public double getBandwidth() {
        return bandwidth;
    }

    public int getConnections() {
        return connections;
    }

    public void setConnections(int count) {
        this.connections = 0;
    }

    public void addBandwidth(int bytes) {
        this.bandwidth += (bytes * 10 ^ -9);
    }

    public static class Analytics {
        private List<Analytic> proxies;

        public Analytics(List<Analytic> proxies) {
            this.proxies = proxies;
        }
    }
}
