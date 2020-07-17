package mcprot.proxy.api.get;

import java.util.ArrayList;

public class Plans {
    private String _id;
    private float connections;

    public String get_id() {
        return _id;
    }

    public float getConnections() {
        return connections;
    }

    public class Response {
        private String message;
        private float status;
        private ArrayList<Plans> data = new ArrayList<Plans>();

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ArrayList<Plans> getData() {
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
