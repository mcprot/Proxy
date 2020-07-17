package mcprot.proxy;

import mcprot.proxy.api.put.Analytic;
import mcprot.proxy.api.put.Connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataQueue {
    public static List<Connection> connections = new ArrayList<>();
    public static HashMap<String, Analytic> analytics = new HashMap<>();

    //todo convert bandwidth from bytes to gbs
}
