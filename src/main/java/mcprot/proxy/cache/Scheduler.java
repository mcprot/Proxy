package mcprot.proxy.cache;

import mcprot.proxy.api.DataQueue;
import mcprot.proxy.api.RemoteAPI;
import mcprot.proxy.api.get.Analytics;
import mcprot.proxy.api.get.Plans;
import mcprot.proxy.api.get.Proxies;
import mcprot.proxy.api.get.Servers;
import mcprot.proxy.api.put.Analytic;
import mcprot.proxy.log.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Scheduler {

    public static void startScheduler() {
        ScheduledExecutorService scheduledExecutorService =
                Executors.newScheduledThreadPool(8);

        ScheduledFuture scheduledFuture =
                scheduledExecutorService.scheduleAtFixedRate(
                        () -> {
                            try {
                                Log.log(Log.MessageType.INFO, "Running scheduler.");
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
                                Cache.cleanUpCache(proxiesResponse);
                                for (Proxies proxies : proxiesResponse.getData()) {
                                    Cache.updateCache(proxies);
                                    ExtraCacheUtils.updateProxiesCache(proxies);
                                }
                                Cache.updateStatuses();

                                Analytics.Response analyticsResponse = RemoteAPI.getAnalytics();
                                ExtraCacheUtils.canJoin.clear();
                                DataQueue.analytics.clear();

                                for (Analytics analytics : analyticsResponse.getData()) {
                                    ExtraCacheUtils.updateAnalyticsCache(analytics);

                                    if (DataQueue.analytics.size() != analyticsResponse.getData().size()) {
                                        DataQueue.analytics.put(analytics.getProxy_id(), new Analytic(analytics.getProxy_id()));
                                    }

                                    ExtraCacheUtils.canJoin.put(analytics.getProxy_id(),
                                            ExtraCacheUtils.proxyJoinable(analytics.getProxy_id()));
                                }
                                Log.log(Log.MessageType.INFO, "Finished running scheduler.");
                            } catch (Exception e) {
                                Log.log(Log.MessageType.ERROR, "An exception has occurred in the scheduler.\n");
                                e.printStackTrace();
                            }
                        }, 0,
                        30,
                        TimeUnit.SECONDS);
    }
}
