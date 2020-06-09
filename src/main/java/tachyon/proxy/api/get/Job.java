
package tachyon.proxy.api.get;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Job {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("listen")
    @Expose
    private Listen listen;
    @SerializedName("targets")
    @Expose
    private List<Target> targets = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Listen getListen() {
        return listen;
    }

    public void setListen(Listen listen) {
        this.listen = listen;
    }

    public List<Target> getTargets() {
        return targets;
    }

    public void setTargets(List<Target> targets) {
        this.targets = targets;
    }

}
