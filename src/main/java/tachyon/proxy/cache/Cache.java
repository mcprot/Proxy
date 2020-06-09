package tachyon.proxy.cache;

import com.tekgator.queryminecraftserver.api.Protocol;
import com.tekgator.queryminecraftserver.api.QueryException;
import com.tekgator.queryminecraftserver.api.QueryStatus;
import com.tekgator.queryminecraftserver.api.Status;
import org.javatuples.Pair;
import tachyon.proxy.Main;
import tachyon.proxy.api.get.Data;
import tachyon.proxy.api.get.Job;
import tachyon.proxy.log.Log;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.*;

public class Cache {

    public static HashMap<String, Server> cache = new HashMap<>();

    public static void updateCache(Job job) {
        if (cache.containsKey(job.getListen().getHostname().toLowerCase())) {
            cache.get(job.getListen().getHostname().toLowerCase()).setJob(job);
        } else {
            cache.put(job.getListen().getHostname().toLowerCase(), new Server(job.getListen().getHostname(), job));
        }
    }

    public static void cleanUpCache(Data data) {
        List<String> jobHosts = new ArrayList<>();

        for (Job job : data.getJobs()) {
            jobHosts.add(job.getListen().getHostname().toLowerCase());
        }

        for (Map.Entry<String, Server> cacheHost : cache.entrySet()) {
            if (!jobHosts.contains(cacheHost.getKey())) {
                cache.remove(cacheHost.getKey());
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

    public static class Server {
        private String hostname;
        private Job job;

        private Status status;

        public Server(String hostname, Job job) {
            this.hostname = hostname;
            this.job = job;

            updateStatus();
        }

        public Status getStatus() {
            return status;
        }

        public String getHostname() {
            return hostname;
        }

        public Pair<String, Integer> getBackend() {
            // TODO make this use connection count data

            Random r = new Random();
            int randomTarget = r.nextInt(job.getTargets().size());
            return new Pair(job.getTargets().get(randomTarget).getHostname(),
                    job.getTargets().get(randomTarget).getPort());
        }

        public void setJob(Job job) {
            this.job = job;
        }

        public void updateStatus() {
            try {
                Random r = new Random();
                int randomTarget = r.nextInt(job.getTargets().size());
                Pair<String, Integer> randomBackend = new Pair(job.getTargets().get(randomTarget).getHostname(),
                        job.getTargets().get(randomTarget).getPort());

                this.status = new QueryStatus.Builder(randomBackend.getValue0())
                        .setPort(randomBackend.getValue1())
                        .setProtocol(Protocol.TCP)
                        .build()
                        .getStatus();

                if (Main.isDebug())
                    Log.log(Log.MessageType.DEBUG, hostname + " -> " + randomBackend.getValue0() + ":" + randomBackend.getValue1());

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
    }
}
