package mcprot.proxy.cache;

import com.tekgator.queryminecraftserver.api.Protocol;
import com.tekgator.queryminecraftserver.api.QueryException;
import com.tekgator.queryminecraftserver.api.QueryStatus;
import com.tekgator.queryminecraftserver.api.Status;
import mcprot.proxy.Main;
import mcprot.proxy.api.get.Proxies;
import mcprot.proxy.log.Log;
import org.javatuples.Pair;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.*;

public class Cache {

    public static HashMap<String, Server> cache = new HashMap<>();

    public static void updateCache(Proxies proxies) {
        if (cache.containsKey(proxies.getHostname().toLowerCase())) {
            cache.get(proxies.getHostname().toLowerCase()).setProxies(proxies);
        } else {
            cache.put(proxies.getHostname().toLowerCase(), new Server(proxies.getHostname().toLowerCase(), proxies));
        }
    }

    public static void cleanUpCache(Proxies.Response data) {
        List<String> jobHosts = new ArrayList<>();

        for (Proxies job : data.getData()) {
            jobHosts.add(job.getHostname());
        }

        Iterator cacheHost = cache.entrySet().iterator();
        while (cacheHost.hasNext()) {
            Map.Entry<String, Server> host = (Map.Entry<String, Server>) cacheHost.next();
            if (!jobHosts.contains(host.getKey())) {
                cache.remove(host.getKey());
            }
        }
    }

    public static void updateStatuses() {
        for (Map.Entry<String, Server> server : cache.entrySet()) {
            server.getValue().updateStatus();
        }
    }

    public static Server getCachedServer(String hostname) {
        if (cache.containsKey(hostname.toLowerCase())) {
            return cache.get(hostname);
        }

        return null;
    }

    public static class Target {
        private String ipAddress;
        private int port;
        private int connections;
        private boolean online;

        public Target(String ipAddress, int port, int connections) {
            this.ipAddress = ipAddress;
            this.port = port;
            this.connections = connections;
            this.online = true;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public int getPort() {
            return port;
        }

        public int getConnections() {
            return connections;
        }

        public boolean isOnline() {
            return online;
        }

        public void setOnline(boolean online) {
            this.online = online;
        }
    }

    public static class Server {

        private String hostname;
        private Proxies proxies;
        private ArrayList<Target> targets;

        public Server(String hostname, Proxies proxies) {
            this.hostname = hostname;
            this.proxies = proxies;
            targets = new ArrayList<>();
            for (String target : proxies.getTargets()) {
                String[] splitTarget = target.split(":");
                this.targets.add(new Target(splitTarget[0],
                        (splitTarget.length > 1 ? Integer.parseInt(splitTarget[1]) : 25565), 0));
            }

            if (Main.isDebug())
                Log.log(Log.MessageType.DEBUG, "Added " + hostname);
        }

        private Status status;

        public Proxies getProxies() {
            return proxies;
        }

        public void setProxies(Proxies proxies) {
            this.proxies = proxies;
        }

        public Status getStatus() {
            return status;
        }

        public String getHostname() {
            return hostname;
        }

        public Pair<String, Integer> getBackend() {
            // TODO make this use connection count data

            Pair<String, Integer> backend = null;

            for (Target target : this.targets) {
                if (target.isOnline()) {
                    if (backend == null) {
                        backend = new Pair(target.getIpAddress() + ":" + target.getPort(), target.getConnections());
                    } else if (target.getConnections() < backend.getValue1()) {
                        backend = new Pair(target.getIpAddress() + ":" + target.getPort(), target.getConnections());
                    }
                }
            }

            return backend;
        }

        public void updateStatus() {
            Random r = new Random();
            int randomTarget = r.nextInt(targets.size());
            try {
                Pair<String, Integer> randomBackend = new Pair(targets.get(randomTarget).getIpAddress(),
                        targets.get(randomTarget).getPort());

                this.status = new QueryStatus.Builder(randomBackend.getValue0())
                        .setPort(randomBackend.getValue1())
                        .setProtocol(Protocol.TCP)
                        .build()
                        .getStatus();

                targets.get(randomTarget).setOnline(true);

            } catch (QueryException e) {
                targets.get(randomTarget).setOnline(false);
                if (targets.size() > 1) {
                    updateStatus();
                }
                Log.log(Log.MessageType.ERROR, e.getMessage());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (SignatureException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }
    }
}
