package tachyon.proxy.tunnel;

import com.tekgator.queryminecraftserver.api.Status;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.javatuples.Pair;
import tachyon.proxy.Main;
import tachyon.proxy.cache.Cache;
import tachyon.proxy.log.Log;
import tachyon.proxy.util.ByteUtil;
import tachyon.proxy.util.PacketUtil;

public class Proxy extends ChannelInboundHandlerAdapter {
    final static AttributeKey<SocketState> SOCKET_STATE = AttributeKey.valueOf("socketstate");
    final static AttributeKey<Channel> PROXY_CHANNEL = AttributeKey.valueOf("proxychannel");

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        final ByteBuf buf = (ByteBuf) msg;
        SocketState socketState = ctx.channel().attr(SOCKET_STATE).get();
        if (socketState == null) {
            ctx.channel().attr(SOCKET_STATE).set(SocketState.HANDSHAKE);
            final int packetLength = ByteUtil.readVarInt(buf);
            final int packetID = ByteUtil.readVarInt(buf);
            if (packetID == 0) {
                final int clientVersion = ByteUtil.readVarInt(buf);
                final String hostname = ByteUtil.readString(buf);
                final int port = buf.readUnsignedShort();
                final int state = ByteUtil.readVarInt(buf);

                Bootstrap b = new Bootstrap();
                b.group(Main.getWorkerGroup());
                b.channel(NioSocketChannel.class);
                b.option(ChannelOption.SO_KEEPALIVE, true);
                b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000);
                b.handler(new ChannelInitializer<Channel>() {
                    @Override
                    public void initChannel(Channel ch) {
                        ch.pipeline().addLast(new ProxyHandler(ctx.channel()));
                    }
                });

                if (state == 1) {
                    if (!Cache.cache.containsKey(hostname.toLowerCase())) {
                        for (ByteBuf messageOfTheDay :
                                PacketUtil.sendMOTD(PacketUtil.createErrorMOTD(clientVersion))) {
                            ctx.writeAndFlush(messageOfTheDay);
                        }
                    } else {
                        Status status = Cache.getCachedServer(hostname).getStatus();
                        status.getVersion().setProtocol(clientVersion);
                        for (ByteBuf messageOfTheDay : PacketUtil.sendMOTD(status.toJson())) {
                            ctx.writeAndFlush(messageOfTheDay);
                        }
                    }
                    ctx.channel().attr(SOCKET_STATE).set(SocketState.STATUS);
                } else {
                    if (!Cache.cache.containsKey(hostname.toLowerCase())) {
                        for (ByteBuf kick : PacketUtil.kickOnLogin("Unknown Server. Please check the address.")) {
                            ctx.writeAndFlush(kick);
                        }
                    } else {
                        Cache.Server server = Cache.getCachedServer(hostname);

                        if (server != null) {
                            final ChannelFuture cf = b.connect(server.getDestinationAddress(),
                                    server.getDestinationPort());

                            cf.addListener((ChannelFutureListener) future -> {
                                if (future.isSuccess()) {
                                    String[] connectingAddress = ctx.channel().remoteAddress().toString()
                                            .replace("/", "").split(":");
                                    Pair<String, Integer> newHostname = PacketUtil.makeHostname(hostname,
                                            connectingAddress[0], connectingAddress[1]);

                                    ByteBuf sendBuf = Unpooled.buffer();
                                    ByteUtil.writeVarInt(packetLength + newHostname.getValue1(), sendBuf);
                                    ByteUtil.writeVarInt(packetID, sendBuf);
                                    ByteUtil.writeVarInt(clientVersion, sendBuf);
                                    // todo modify hostname to include custom values & implement a key
                                    ByteUtil.writeString(newHostname.getValue0(), sendBuf);
                                    ByteUtil.writeVarShort(sendBuf, port);
                                    ByteUtil.writeVarInt(state, sendBuf);

                                    if (Main.isDebug())
                                        Log.log(Log.MessageType.DEBUG,
                                                newHostname.getValue0() + "");


                                    while (buf.readableBytes() > 0) {
                                        byte b1 = buf.readByte();
                                        sendBuf.writeByte(b1);
                                    }

                                    //Send out the handshake + anything else we've gotten (Request or login start packet)
                                    future.channel().writeAndFlush(sendBuf);
                                    ctx.channel().attr(SOCKET_STATE).set(SocketState.PROXY);
                                    ctx.channel().attr(PROXY_CHANNEL).set(cf.channel());
                                } else {
                                    for (ByteBuf kick : PacketUtil.kickOnLogin(
                                            "Server Offline. Check back in a bit.")) {
                                        ctx.writeAndFlush(kick);
                                    }
                                }
                            });
                        } else {
                            for (ByteBuf kick : PacketUtil.kickOnLogin(
                                    "Server Offline. Check back in a bit.")) {
                                ctx.writeAndFlush(kick);
                            }
                        }
                    }
                }
            }
        } else if (socketState == SocketState.STATUS) {
            for (ByteBuf pong : PacketUtil.pong()) {
                ctx.writeAndFlush(pong);
            }
        } else {
            Channel proxiedChannel = ctx.channel().attr(PROXY_CHANNEL).get();
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            proxiedChannel.writeAndFlush(Unpooled.buffer().writeBytes(bytes));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
