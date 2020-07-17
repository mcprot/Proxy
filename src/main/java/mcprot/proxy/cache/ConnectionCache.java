package mcprot.proxy.cache;

import mcprot.proxy.DataQueue;
import mcprot.proxy.api.put.Connection;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class ConnectionCache {
    private HashMap<UUID, Connection> connectionHashMap;

    public ConnectionCache() {
        this.connectionHashMap = new HashMap<>();
    }

    public void addConnection(UUID uuid, Connection connection) {
        connectionHashMap.put(uuid, connection);
    }

    public void removeConnection(UUID uuid) {
        connectionHashMap.get(uuid).setDate_disconnect((new Date()).toString());
        DataQueue.connections.add(connectionHashMap.get(uuid));
        connectionHashMap.remove(uuid);
    }

    public Connection getConnection(UUID uuid) {
        return this.connectionHashMap.get(uuid);
    }

}
