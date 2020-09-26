package mcprot.proxy.cache;

import mcprot.proxy.api.DataQueue;
import mcprot.proxy.api.put.Connection;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class ConnectionCache {
    public static HashMap<UUID, Connection> connectionHashMap = new HashMap<>();

    public static void addConnection(UUID uuid, Connection connection) {
        connectionHashMap.put(uuid, connection);
    }

    public static void removeConnection(UUID uuid) {
        if (connectionHashMap.containsKey(uuid)) {
            connectionHashMap.get(uuid).setDate_disconnect((new Date()).toString());
            DataQueue.connections.add(connectionHashMap.get(uuid));
            connectionHashMap.remove(uuid);
        }
    }

    public static Connection getConnection(UUID uuid) {
        return connectionHashMap.get(uuid);
    }

}
