package mcprot.proxy.util;

import com.google.gson.stream.JsonWriter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mcprot.proxy.signing.Signing;
import org.javatuples.Pair;

import java.io.IOException;
import java.io.StringWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;

public class PacketUtil {

    private static String favicon =
            "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAFKklEQVR4nN2beagVVRzHP9prhfaNDAzKooiwsqKN9oUwXwVBUZalIiEFlRoVJCYtFJr+IxE9WixoV4u0DbIiS83W10K+1Chfm88W217hEr/H797mjTNzzp2zvDv3888d5px7zu/3nZnfzDnndwahHNGxkUjsBowBLgYOBcSGL4BngbnALzHM6Bzf1vc7OJbXwJHAg0A3MBs4GdgL2FOPZ2tZB3BULKNCC7AdcDmwBPgQGA/sVFB/R2Ac8AHwLjAa2D6kgaEEGArcCawFHgdOLNHG8cBjwLfA3cABAez0KoA8y2cDC4A1wK3A3h7alTZuBlYDzwPnal9e8CGABLXrgS+BV4ELAt1Z0mY78DKwErgB2N1Ho2VJBrVZwMHOLtozDLjPR9BsVIBGg1ponIOmrQA+gloe0zVmuFIqaBYJECqopZmlt/T5wCJgi2N7DQXNLAG2Ba6JENSSbAYWAiNVjBnAz45tpoPmRPVtq0pJ9tFn6f7IQS2JXLkpwP7A1cAKD22KqHOApcC+yYKkAG16FUZ46NAHvcAjwLHAccCjwD+O7R6tPrbVTiQFGAscM2DuFvMecJXeFfJ8f+3Q1gj1tY+kAKNDWe+R9cA9wEHAKH2+ywTNuq9JAQ5vJk8NSNB8EThPY9XMBofRdV/TMaCKrAIm6+Mht/b7Fj5kxoBoMyKB+Bt4WOPYSEMX9ccmKcDvVfLWwCpD+ebaQVIA11dMM7GzwZbfsgT4o4UE2MVQninAT+Hsic5+hg7X1Q6SAnzTGr73MdRQ/mPtICnA2nD2RMc0FO6uHQzOOtkCDDO4sDpLgJUtJMBwQ3n9NZkU4JNw9kRliC64FPFplgAbHEdZzYJpOP8d8EOWAMJHLSDAqYbyfmOFtADL/NsTndMMHfabYUoLsLjavrOHxRpBv4ucFmCFxoKqcpFhAlem2d4qEmAT8GaFBbjQUP6GDpvrZKm1yL9dUZDb/xxDR6+kT2QJMC85Xq4QV+rSXRELbQSQUeHrFRRggqFcgl9X+mRewHjSj03ROAM4zNDZ3KyTeQI8U7EJklsM5f/mXdQ8ATbkKdaEyKrwWQazFuStNRa9M+dURIC7LOrMyCsoEuDzCgRDWf093VBnsS6tZWJa9p7qx84g7KArQiZyr75gEmBJ1sdDkzDdYuZHlsNfchFAmNaEzsuS+Y0W9SabFk9tBBAVn7K3LTi76ittG0NHz+kdXIht6sukJvkuGKTrfwca6smA5yabBm0F6NZnbqCZqkNeE7clZ359CIBmcy0fQAHGWMajZZp5bkUjAmzUzIo/y9nvRLtmpZr4SxOrNoUQAB1NTYri8v+062aKrVLcMpiomy+sKZP/94BmZMZA8hXnWzrfoZlkDVE2AXKCp/y9POQVd6/mK9rYKLmN15XpqKwAvTr/FmI9cYiOQaZY1u/SbNbeMp25pMB2ax5uj0MbaS4BPgZOsazfo5li6yzqZuKaA/yZJiS5TqVL3t8L+oVnWter0aMzQaZ8oEJ8JEEv1zvh15L/n6mRe1QD/6k531myzzq+ssCX6m37fYn/jrWM8jW+Ak7y4bzgMw2+06dhObwDnOAzl8H3PoA1auDTnttFvz/O9Bx0g2yEkE/lS4Fr08tQJZFR6GX6UVTqVVdEqJ0gW3RSdbjjkvtrmtj8hEfb+hF6K0yXbrAal0xNs0C+Ma7Qt0vQ9L0Ym6dlnfEhTWufpjn/eazXsfwhujvNdQOVkZi7xyUZ+3bN4ZPv9rfVYQlqciznpOwOHdaGB/gPfPP7b2bFbCkAAAAASUVORK5CYII=";

    /**
     * @param protocolVersion protocol verison of connecting client, because we always want to show we are the right version.
     * @return
     */
    public static String createErrorMOTD(int protocolVersion, String errorMessage) {
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
            writer.name("text").value(errorMessage);
            writer.name("color").value("red");
            writer.endObject();

            // Tachyon Logo in Base64
            writer.name("favicon").value(favicon);
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
        String[] forgeSplit = hostname.split("\0", 2);
        String modifiedHostname = forgeSplit[0] + "///"
                + sourceIP + ":" + sourcePort + "///"
                + System.currentTimeMillis() / 1000;

        String encodedHostname = modifiedHostname + "///" + Signing.encode(modifiedHostname.getBytes()) + (forgeSplit.length > 1 ? "\0" + forgeSplit[1] : "");

        ByteUtil.writeString(encodedHostname, byteHostname);

        return new Pair(encodedHostname, byteHostname.readableBytes() - oldHostname.readableBytes());
    }
}
