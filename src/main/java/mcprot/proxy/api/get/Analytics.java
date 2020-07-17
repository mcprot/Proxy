package mcprot.proxy.api.get;

import java.util.ArrayList;
import java.util.Map;

public class Analytics {
    Map<String, Integer> connections;
    private String proxy_id;

    public Map<String, Integer> getConnections() {
        return connections;
    }

    public String getProxy_id() {
        return proxy_id;
    }

    public class Response {
        private String message;
        private float status;
        private ArrayList<Analytics> data = new ArrayList<Analytics>();

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ArrayList<Analytics> getData() {
            return data;
        }

        public float getStatus() {
            return status;
        }

        public void setStatus(float status) {
            this.status = status;
        }
    }
}
