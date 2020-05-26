package tachyon.proxy.util;

import io.netty.buffer.ByteBuf;

public class ByteUtil {
    /*
        All of these varint implementations comes from SpigotMC's bungeecord.
        Source: https://github.com/SpigotMC/BungeeCord/blob/master/protocol/src/main/java/net/md_5/bungee/protocol/DefinedPacket.java
     */
    public static int readVarInt(ByteBuf input) {
        return readVarInt(input, 5);
    }

    public static int readVarInt(ByteBuf input, int maxBytes) {
        int out = 0;
        int bytes = 0;
        byte in;
        while (true) {
            in = input.readByte();

            out |= (in & 0x7F) << (bytes++ * 7);

            if (bytes > maxBytes) {
                throw new RuntimeException("VarInt too big");
            }

            if ((in & 0x80) != 0x80) {
                break;
            }
        }

        return out;
    }

    public static String readString(ByteBuf buf) {
        int len = readVarInt(buf);
        if (len > Short.MAX_VALUE) {
        }

        byte[] b = new byte[len];
        buf.readBytes(b);

        return new String(b);
    }

    public static void writeVarInt(int value, ByteBuf output) {
        int part;
        while (true) {
            part = value & 0x7F;

            value >>>= 7;
            if (value != 0) {
                part |= 0x80;
            }

            output.writeByte(part);

            if (value == 0) {
                break;
            }
        }
    }

    public static void writeString(String s, ByteBuf buf) {
        if (s.length() > Short.MAX_VALUE) {
        }

        byte[] b = s.getBytes();
        writeVarInt(b.length, buf);
        buf.writeBytes(b);
    }

    public static void writeVarShort(ByteBuf buf, int toWrite) {
        int low = toWrite & 0x7FFF;
        int high = (toWrite & 0x7F8000) >> 15;
        if (high != 0) {
            low = low | 0x8000;
        }
        buf.writeShort(low);
        if (high != 0) {
            buf.writeByte(high);
        }
    }
}
