package mcprot.proxy.cache;

import mcprot.proxy.api.get.Analytics;
import mcprot.proxy.api.get.Plans;
import mcprot.proxy.api.get.Proxies;
import mcprot.proxy.api.get.Servers;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExtraCacheUtils {

    public static HashMap<String, Analytics> analyticsCache = new HashMap<>();
    public static HashMap<String, Plans> plansCache = new HashMap<>();
    public static HashMap<String, Servers> serversCache = new HashMap<>();
    public static HashMap<String, Proxies> proxiesCache = new HashMap<>();

    public static void updateAnalyticsCache(Analytics analytics) {
        analyticsCache.put(analytics.getProxy_id(), analytics);
    }

    public static HashMap<String, Boolean> canJoin = new HashMap<>();

    public static void updateServersCache(Servers servers) {
        serversCache.put(servers.get_id(), servers);
    }

    public static void updatePlansCache(Plans plans) {
        plansCache.put(plans.get_id(), plans);
    }

    public static void updateProxiesCache(Proxies proxies) {
        proxiesCache.put(proxies.get_id(), proxies);
    }

    public static boolean proxyJoinable(String proxyID) {
        int totalConnections = 0;
        for (Map.Entry<String, Integer> connections : analyticsCache.get(proxyID).getConnections().entrySet()) {
            if (serversCache.containsKey(connections.getKey())) {
                Servers servers = serversCache.get(connections.getKey());
                if (isRecentlyUpdated(servers.getLast_request(), 45)) {
                    totalConnections += connections.getValue();
                }
            }
        }

        Plans plan = plansCache.get(proxiesCache.get(proxyID).getPlan());

        if (plan.getConnections() > totalConnections) {
            return true;
        }
        return false;
    }

    public static boolean canJoin(String hostname) {
        return canJoin.get(Cache.getCachedServer(hostname).getProxies().get_id());
    }

    private static Boolean isRecentlyUpdated(Date lastUpdate, int secondsRange) {
        Calendar lastCal = Calendar.getInstance();
        lastCal.setTime(lastUpdate);
        lastCal.add(Calendar.SECOND, secondsRange);

        if (lastCal.getTime().compareTo(new Date()) >= 0) {
            return true;
        }

        return false;
    }


}
