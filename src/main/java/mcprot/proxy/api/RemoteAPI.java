package mcprot.proxy.api;

import com.google.gson.Gson;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import mcprot.proxy.Main;
import mcprot.proxy.api.get.Analytics;
import mcprot.proxy.api.get.Plans;
import mcprot.proxy.api.get.Proxies;
import mcprot.proxy.api.get.Servers;
import mcprot.proxy.api.put.Analytic;
import mcprot.proxy.api.put.Connection;

import java.util.List;

public class RemoteAPI {

    public static Proxies.Response getProxies() {
        HttpResponse<String> httpResponse = Unirest.get("http://localhost:3000/api/" + Main.getConfig().getApiKey() + "/proxies").asString();
        Gson g = new Gson();
        Proxies.Response response = g.fromJson(httpResponse.getBody(), Proxies.Response.class);
        return response;
    }

    public static Analytics.Response getAnalytics() {
        HttpResponse<String> httpResponse = Unirest.get("http://localhost:3000/api/" + Main.getConfig().getApiKey() + "/analytics").asString();
        Gson g = new Gson();
        Analytics.Response response = g.fromJson(httpResponse.getBody(), Analytics.Response.class);
        return response;
    }

    public static Plans.Response getPlans() {
        HttpResponse<String> httpResponse = Unirest.get("http://localhost:3000/api/" + Main.getConfig().getApiKey() + "/plans").asString();
        Gson g = new Gson();
        Plans.Response response = g.fromJson(httpResponse.getBody(), Plans.Response.class);
        return response;
    }

    public static Servers.Response getServers() {
        HttpResponse<String> httpResponse = Unirest.get("http://localhost:3000/api/" + Main.getConfig().getApiKey() + "/servers").asString();
        Gson g = new Gson();
        Servers.Response response = g.fromJson(httpResponse.getBody(), Servers.Response.class);
        return response;
    }

    public static void putConnection(List<Connection> connection) {
        Gson g = new Gson();
        //System.out.println(g.toJson(new Connection.Connections(connection)));
        HttpResponse<String> httpResponse = Unirest.put("http://localhost:3000/api/" + Main.getConfig().getApiKey() + "/connection")
                .header("Content-Type", "application/json")
                .body(g.toJson(new Connection.Connections(connection))).asString();

        //System.out.println(httpResponse.getBody());
    }

    public static void putAnalytic(List<Analytic> analytics) {
        Gson g = new Gson();
        System.out.println(g.toJson(new Analytic.Analytics(analytics)));
        HttpResponse<String> httpResponse = Unirest.put("http://localhost:3000/api/" + Main.getConfig().getApiKey() + "/analytic")
                .header("Content-Type", "application/json")
                .body(g.toJson(new Analytic.Analytics(analytics))).asString();
        System.out.println(httpResponse.getBody());
    }

}
