package mcprot.proxy.api.put;

import java.util.List;

public class Analytic {

    private String proxy_id;
    private int connections = 0;

    public Analytic(String proxy_id) {
        this.proxy_id = proxy_id;
    }

    public String getProxy_id() {
        return proxy_id;
    }

    public int getConnections() {
        return connections;
    }

    public void setConnections(int count) {
        this.connections = count;
    }

    public static class Analytics {
        private List<Analytic> proxies;

        public Analytics(List<Analytic> proxies) {
            this.proxies = proxies;
        }
    }
}
