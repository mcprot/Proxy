package tachyon.proxy.util;

import com.google.gson.stream.JsonWriter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import tachyon.proxy.Main;
import tachyon.proxy.Settings;
import tachyon.proxy.tunnel.Proxy;

import java.io.IOException;
import java.io.StringWriter;

public class PacketUtil {
    /**
     * @param protocolVersion protocol verison of connecting client, because we always want to show we are the right version.
     * @return
     */
    public static ByteBuf createStatusPacket(int protocolVersion) {
        Settings settings = Main.getSettings();
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        try {
            writer.beginObject();
            writer.name("version").beginObject();
            writer.name("name").value(settings.getVersionName());
            writer.name("protocol").value(protocolVersion);
            writer.endObject();

            writer.name("players").beginObject();
            writer.name("max").value(settings.getMaxPlayers());
            writer.name("online").value(settings.getOnlinePlayers());
            writer.endObject();

            writer.name("description").beginObject();
            writer.name("text").value(settings.getMotd());
            writer.endObject();

            writer.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteBuf buf = Unpooled.buffer();
        Proxy.writeVarInt(0, buf);

        Proxy.writeString(sw.toString(), buf);
        return buf;
    }
}
