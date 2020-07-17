package mcprot.proxy.cache;

import mcprot.proxy.api.get.Analytics;
import mcprot.proxy.api.get.Plans;
import mcprot.proxy.api.get.Proxies;

import java.util.HashMap;
import java.util.Map;

public class ExtraCache {

    public static HashMap<String, Analytics> analyticsCache = new HashMap<>();
    public static HashMap<String, Plans> plansCache = new HashMap<>();

    public static void updateAnalyticsCache(Analytics analytics) {
        analyticsCache.put(analytics.getProxy_id(), analytics);
    }

    public static void updatePlansCache(Plans plans) {
        plansCache.put(plans.get_id(), plans);
    }

    public static boolean canPlayerJoin(String hostname) {
        Proxies proxies = Cache.getCachedServer(hostname.toLowerCase()).getProxies();
        String id = proxies.get_id();

        int totalConnections = 0;
        for (Map.Entry<String, Integer> connections : analyticsCache.get(id).getConnections().entrySet()) {
            totalConnections += connections.getValue();
        }

        Plans plan = plansCache.get(proxies.getPlan());

        if (plan.getConnections() > totalConnections) {
            return true;
        }

        return false;
    }
}
