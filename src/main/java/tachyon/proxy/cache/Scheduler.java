package tachyon.proxy.cache;

import tachyon.proxy.api.RemoteAPI;
import tachyon.proxy.api.get.Job;
import tachyon.proxy.api.get.Servers;

import java.util.Timer;
import java.util.TimerTask;

public class Scheduler {

    public static void getServers() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Servers servers = RemoteAPI.getServers();
                for (Job job : servers.getData().getJobs()) {
                    Cache.updateCache(job);
                }

                Cache.cleanUpCache(servers.getData());

                Cache.updateStatuses();
            }
        };
        timer.schedule(task, 0, 1 * 60 * 1000 /* (min * second * millisecond) */);
    }
}
