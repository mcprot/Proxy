package tachyon.proxy.api;

import com.google.gson.Gson;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import tachyon.proxy.api.get.Servers;

public class RemoteAPI {
    public static Servers getServers() {
        HttpResponse<String> httpResponse = Unirest.get("https://panel.mcprot.com/api/v1/server/0").asString();
        Gson g = new Gson();
        Servers servers = g.fromJson(httpResponse.getBody(), Servers.class);

        return servers;
    }
}
