package mcprot.proxy.api.get;

public class Server {
    private String _id;

    public String get_id() {
        return _id;
    }

    public class Response {
        private String message;
        private float status;
        private Server data;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Server getData() {
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
