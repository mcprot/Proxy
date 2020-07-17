package mcprot.proxy.cache;

import mcprot.proxy.api.get.Analytics;
import mcprot.proxy.api.get.Plans;
import mcprot.proxy.api.get.Proxies;
import mcprot.proxy.api.get.Servers;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExtraCache {

    public static HashMap<String, Analytics> analyticsCache = new HashMap<>();
    public static HashMap<String, Plans> plansCache = new HashMap<>();
    public static HashMap<String, Servers> serversCache = new HashMap<>();

    public static void updateAnalyticsCache(Analytics analytics) {
        analyticsCache.put(analytics.getProxy_id(), analytics);
    }

    public static void updateServersCache(Servers servers) {
        serversCache.put(servers.get_id(), servers);
    }

    public static void updatePlansCache(Plans plans) {
        plansCache.put(plans.get_id(), plans);
    }

    public static boolean canPlayerJoin(String hostname) {
        Proxies proxies = Cache.getCachedServer(hostname.toLowerCase()).getProxies();
        String id = proxies.get_id();

        int totalConnections = 0;
        for (Map.Entry<String, Integer> connections : analyticsCache.get(id).getConnections().entrySet()) {
            for (Map.Entry<String, Servers> servers : serversCache.entrySet()) {
                if (isLastRequestRecent(new Date(servers.getValue().getLast_request()), new Date(), 45)) {
                    if (servers.getKey().equalsIgnoreCase(connections.getKey())) {
                        totalConnections += connections.getValue();
                    }
                }
            }
        }

        Plans plan = plansCache.get(proxies.getPlan());

        if (plan.getConnections() > totalConnections) {
            return true;
        }

        return false;
    }

    private static Boolean isLastRequestRecent(Date lastUpdate, Date currentTime, int secondsRange) {
        Calendar lastCal = Calendar.getInstance();
        lastCal.setTime(lastUpdate);
        lastCal.add(Calendar.SECOND, secondsRange);

        if (lastCal.getTime().compareTo(currentTime) >= 0) {
            return true;
        }

        return false;
    }
}
