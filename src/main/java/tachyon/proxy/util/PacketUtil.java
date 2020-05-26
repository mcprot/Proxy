package tachyon.proxy.util;

import com.google.gson.stream.JsonWriter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.javatuples.Pair;
import tachyon.proxy.signing.Signing;

import java.io.IOException;
import java.io.StringWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;

public class PacketUtil {
    /**
     * @param protocolVersion protocol verison of connecting client, because we always want to show we are the right version.
     * @return
     */
    public static String createErrorMOTD(int protocolVersion) {
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        try {
            writer.beginObject();
            writer.name("version").beginObject();
            writer.name("name").value("Tachyon");
            writer.name("protocol").value(protocolVersion);
            writer.endObject();

            writer.name("players").beginObject();
            writer.name("max").value(1);
            writer.name("online").value(1);
            writer.endObject();

            writer.name("description").beginObject();
            writer.name("text").value("Unknown server. Please check the address.");
            writer.name("color").value("red");
            writer.endObject();

            // Tachyon Logo in Base64
            writer.name("favicon").value("data:image/png;base64,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");

            writer.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sw.toString();
    }

    public static List<ByteBuf> sendMOTD(String msgOfTheDay) {
        ByteBuf messageOfTheDay = Unpooled.buffer();
        ByteUtil.writeVarInt(0, messageOfTheDay);
        ByteUtil.writeString(msgOfTheDay, messageOfTheDay);

        ByteBuf header = Unpooled.buffer();
        ByteUtil.writeVarInt(messageOfTheDay.readableBytes(), header);

        List<ByteBuf> bytes = new ArrayList<>();
        bytes.add(header);
        bytes.add(messageOfTheDay);

        return bytes;
    }

    public static List<ByteBuf> pong() {
        ByteBuf pong = Unpooled.buffer();
        ByteUtil.writeVarInt(1, pong);
        pong.writeLong(1);

        ByteBuf header = Unpooled.buffer();
        ByteUtil.writeVarInt(pong.readableBytes(), header);

        List<ByteBuf> bytes = new ArrayList<>();
        bytes.add(header);
        bytes.add(pong);

        return bytes;
    }

    public static List<ByteBuf> kickOnLogin(String message) {

        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        try {
            writer.beginObject();
            writer.name("text").value(message);
            writer.name("color").value("red");

            writer.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteBuf kickMessage = Unpooled.buffer();
        ByteUtil.writeVarInt(0, kickMessage);
        ByteUtil.writeString(sw.toString(), kickMessage);

        ByteBuf header = Unpooled.buffer();
        ByteUtil.writeVarInt(kickMessage.readableBytes(), header);

        List<ByteBuf> bytes = new ArrayList<>();
        bytes.add(header);
        bytes.add(kickMessage);

        return bytes;
    }

    public static Pair makeHostname(String hostname, String sourceIP, String sourcePort) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        ByteBuf oldHostname = Unpooled.buffer();
        ByteUtil.writeString(hostname, oldHostname);

        ByteBuf byteHostname = Unpooled.buffer();
        String[] forgeSplit = hostname.split("\0FML\0");
        String modifiedHostname = forgeSplit[0] + "///"
                + sourceIP + ":" + sourcePort + "///"
                + System.currentTimeMillis();

        String encodedHostname = modifiedHostname + "///" + Signing.encode(modifiedHostname.getBytes());

        if (forgeSplit.length > 1) {
            encodedHostname += "\0FML\0" + forgeSplit[1];
        }

        ByteUtil.writeString(encodedHostname, byteHostname);

        return new Pair(encodedHostname, byteHostname.readableBytes() - oldHostname.readableBytes());
    }
}
