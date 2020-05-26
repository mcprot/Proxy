package tachyon.proxy.cache;

import com.tekgator.queryminecraftserver.api.Protocol;
import com.tekgator.queryminecraftserver.api.QueryException;
import com.tekgator.queryminecraftserver.api.QueryStatus;
import com.tekgator.queryminecraftserver.api.Status;
import tachyon.proxy.Main;
import tachyon.proxy.log.Log;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;

public class Cache {

    public static HashMap<String, Server> cache = new HashMap<>();

    public static void updateCache(String hostname, String destinationAddress, int destinationPort) {
        if (cache.containsKey(hostname.toLowerCase())) {
            cache.remove(hostname.toLowerCase());
        }

        Server server = new Server(hostname.toLowerCase(), destinationAddress, destinationPort);
        cache.put(hostname.toLowerCase(), server);
    }

    public static Server getCachedServer(String hostname) {
        if (cache.containsKey(hostname.toLowerCase())) {
            return cache.get(hostname.toLowerCase());
        }

        return null;
    }

    public static class Server {
        private String hostname;
        private String destinationAddress;
        private int destinationPort;

        private Status status;

        public Server(String hostname, String destinationAddress, int destinationPort) {
            this.hostname = hostname;
            this.destinationAddress = destinationAddress;
            this.destinationPort = destinationPort;

            try {
                this.status = new QueryStatus.Builder(destinationAddress)
                        .setPort(destinationPort)
                        .setProtocol(Protocol.TCP)
                        .build()
                        .getStatus();

                if (Main.isDebug())
                    Log.log(Log.MessageType.DEBUG, hostname + " -> " + destinationAddress + ":" + destinationPort);

            } catch (QueryException e) {
                Log.log(Log.MessageType.ERROR, e.getMessage());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (SignatureException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }

        public Status getStatus() {
            return status;
        }

        public String getHostname() {
            return hostname;
        }

        public String getDestinationAddress() {
            return destinationAddress;
        }

        public int getDestinationPort() {
            return destinationPort;
        }
    }
}
