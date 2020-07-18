package mcprot.proxy.api.get;

import java.util.Date;
import java.util.List;

public class Servers {
    private String _id;
    private String api_key;
    private Date last_request;
    // Getter Methods

    public String get_id() {
        return _id;
    }

    public String getApi_key() {
        return api_key;
    }

    public Date getLast_request() {
        return last_request;
    }

    public class Response {
        private String message;
        private float status;
        private List<Servers> data;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<Servers> getData() {
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
