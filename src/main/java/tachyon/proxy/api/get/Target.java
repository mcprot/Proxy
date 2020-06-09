
package tachyon.proxy.api.get;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Target {

    @SerializedName("port")
    @Expose
    private Integer port;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("hostname")
    @Expose
    private String hostname;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

}
