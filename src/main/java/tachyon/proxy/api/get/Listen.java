
package tachyon.proxy.api.get;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Listen {

    @SerializedName("port")
    @Expose
    private Integer port;
    @SerializedName("hostname")
    @Expose
    private String hostname;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

}
