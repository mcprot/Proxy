package mcprot.proxy.cache;

import mcprot.proxy.DataQueue;
import mcprot.proxy.api.RemoteAPI;
import mcprot.proxy.api.get.Analytics;
import mcprot.proxy.api.get.Plans;
import mcprot.proxy.api.get.Proxies;
import mcprot.proxy.api.get.Servers;
import mcprot.proxy.api.put.Analytic;

import java.util.*;

public class Scheduler {

    public static void runScheduler() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //TODO make it update it's own analytics first.
                if (!DataQueue.connections.isEmpty()) {
                    RemoteAPI.putConnection(DataQueue.connections);
                    DataQueue.connections.clear();
                }

                if (!DataQueue.analytics.isEmpty()) {
                    List<Analytic> analyticList = new ArrayList<>();
                    for (Map.Entry<String, Analytic> analyticEntry : DataQueue.analytics.entrySet()) {
                        analyticList.add(analyticEntry.getValue());
                        //analyticEntry.getValue().resetBandwidth();
                    }

                    RemoteAPI.putAnalytic(analyticList);
                }

                Plans.Response plansResponse = RemoteAPI.getPlans();
                ExtraCacheUtils.plansCache.clear();
                for (Plans plans : plansResponse.getData()) {
                    ExtraCacheUtils.updatePlansCache(plans);
                }

                Servers.Response serversResponse = RemoteAPI.getServers();
                ExtraCacheUtils.serversCache.clear();
                for (Servers servers : serversResponse.getData()) {
                    ExtraCacheUtils.updateServersCache(servers);
                }

                Proxies.Response proxiesResponse = RemoteAPI.getProxies();
                ExtraCacheUtils.proxiesCache.clear();
                for (Proxies proxies : proxiesResponse.getData()) {
                    Cache.updateCache(proxies);
                    ExtraCacheUtils.updateProxiesCache(proxies);
                }
                Cache.cleanUpCache(proxiesResponse);
                Cache.updateStatuses();

                Analytics.Response analyticsResponse = RemoteAPI.getAnalytics();
                ExtraCacheUtils.canJoin.clear();
                for (Analytics analytics : analyticsResponse.getData()) {
                    ExtraCacheUtils.updateAnalyticsCache(analytics);

                    if (DataQueue.analytics.isEmpty()) {
                        DataQueue.analytics.put(analytics.getProxy_id(), new Analytic(analytics.getProxy_id()));
                    }

                    ExtraCacheUtils.canJoin.put(analytics.getProxy_id(),
                            ExtraCacheUtils.proxyJoinable(analytics.getProxy_id()));
                }
            }
        };
        timer.schedule(task, 0, (1 * 60 * 1000) / 2 /* (min * second * millisecond) */);
    }
}
