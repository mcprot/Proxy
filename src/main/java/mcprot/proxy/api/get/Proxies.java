package mcprot.proxy.api.get;

import java.util.ArrayList;

public class Proxies {
    private ArrayList<String> targets = new ArrayList<String>();
    private String user;
    private String hostname;
    private String expiry;
    private String plan;
    private String _id;

    public ArrayList<String> getTargets() {
        return targets;
    }

    public String get_id() {
        return _id;
    }

    public String getUser() {
        return user;
    }

    public String getHostname() {
        return hostname;
    }

    public String getExpiry() {
        return expiry;
    }

    public String getPlan() {
        return plan;
    }

    public class Response {
        private String message;
        private float status;
        private ArrayList<Proxies> data = new ArrayList<Proxies>();

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ArrayList<Proxies> getData() {
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
