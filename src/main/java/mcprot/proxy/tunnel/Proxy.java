package mcprot.proxy.tunnel;

import com.tekgator.queryminecraftserver.api.Status;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import mcprot.proxy.DataQueue;
import mcprot.proxy.Main;
import mcprot.proxy.api.get.Proxies;
import mcprot.proxy.api.put.Analytic;
import mcprot.proxy.api.put.Connection;
import mcprot.proxy.cache.Cache;
import mcprot.proxy.cache.ExtraCache;
import mcprot.proxy.log.Log;
import mcprot.proxy.util.ByteUtil;
import mcprot.proxy.util.PacketUtil;
import org.javatuples.Pair;

import java.util.Date;
import java.util.UUID;

public class Proxy extends ChannelInboundHandlerAdapter {
    final static AttributeKey<SocketState> SOCKET_STATE = AttributeKey.valueOf("socketstate");
    final static AttributeKey<Channel> PROXY_CHANNEL = AttributeKey.valueOf("proxychannel");
    public final static AttributeKey<Proxies> PROXY_ID = AttributeKey.valueOf("proxy_id");
    public final static AttributeKey<UUID> CONNECTION_UUID = AttributeKey.valueOf("connection_uuid");

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        final String[] ipAddress = {null};

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

                String fmlRemoved = hostname.replace("\0FML\0", "");

                if (state == 1) {
                    if (!Cache.cache.containsKey(fmlRemoved.toLowerCase())) {
                        for (ByteBuf messageOfTheDay :
                                PacketUtil.sendMOTD(PacketUtil.createErrorMOTD(clientVersion,
                                        "Unknown Server. Please check the address."))) {
                            ctx.writeAndFlush(messageOfTheDay);
                        }
                    } else {
                        Status status = Cache.getCachedServer(fmlRemoved).getStatus();
                        if (status == null) {
                            for (ByteBuf messageOfTheDay :
                                    PacketUtil.sendMOTD(PacketUtil.createErrorMOTD(clientVersion,
                                            "Server Offline. Check back in a bit."))) {
                                ctx.writeAndFlush(messageOfTheDay);
                            }
                        } else {
                            status.getVersion().setProtocol(clientVersion);
                            for (ByteBuf messageOfTheDay : PacketUtil.sendMOTD(status.toJson())) {
                                ctx.writeAndFlush(messageOfTheDay);
                            }
                        }
                    }
                    ctx.channel().attr(SOCKET_STATE).set(SocketState.STATUS);
                } else {
                    if (!Cache.cache.containsKey(fmlRemoved.toLowerCase())) {
                        for (ByteBuf kick : PacketUtil.kickOnLogin("Unknown Server. Please check the address.")) {
                            ctx.writeAndFlush(kick);
                        }
                    } else if (!ExtraCache.canPlayerJoin(fmlRemoved.toLowerCase())) {
                        for (ByteBuf kick : PacketUtil.kickOnLogin("Too many connections to the server. Check back later.")) {
                            ctx.writeAndFlush(kick);
                        }
                    } else {
                        Cache.Server server = Cache.getCachedServer(fmlRemoved);

                        if (server != null) {
                            String[] backend = server.getBackend().getValue0().split(":");
                            final ChannelFuture cf = b.connect(backend[0], Integer.parseInt(backend[1]));

                            cf.addListener((ChannelFutureListener) future -> {
                                if (future.isSuccess()) {
                                    ByteBuf sendBuf = Unpooled.buffer();

                                    try {
                                        String[] connectingAddress = ctx.channel().remoteAddress().toString()
                                                .replace("/", "").split(":");
                                        Pair<String, Integer> newHostname = PacketUtil.makeHostname(hostname,
                                                connectingAddress[0], connectingAddress[1]);

                                        ipAddress[0] = connectingAddress[0];
                                        ctx.channel().attr(PROXY_ID).set(server.getProxies());
                                        cf.channel().attr(PROXY_ID).set(server.getProxies());
                                        UUID uuid = UUID.randomUUID();
                                        ctx.channel().attr(CONNECTION_UUID).set(uuid);
                                        cf.channel().attr(CONNECTION_UUID).set(uuid);

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
                                    } finally {
                                        //sendBuf.release();
                                    }
                                    ctx.channel().attr(SOCKET_STATE).set(SocketState.PROXY);
                                    ctx.channel().attr(PROXY_CHANNEL).set(cf.channel());

                                    Analytic analytic = DataQueue.analytics.get(ctx.channel().attr(PROXY_ID).get().get_id());
                                    analytic.setConnections(analytic.getConnections() + 1);
                                    ctx.channel().closeFuture().addListener((ChannelFutureListener) future1 -> {
                                        analytic.setConnections(analytic.getConnections() - 1);
                                        //todo remove connection and add to dataqueue
                                        Main.connectionCache.removeConnection(ctx.channel().attr(CONNECTION_UUID).get());
                                    });

                                    Main.connectionCache.addConnection(ctx.channel().attr(CONNECTION_UUID).get(),
                                            new Connection(ctx.channel().attr(PROXY_ID).get().get_id(), clientVersion,
                                                    hostname.contains("FML") ? true : false, ipAddress[0],
                                                    (new Date()).toString(), true));
                                } else {
                                    for (ByteBuf kick : PacketUtil.kickOnLogin(
                                            "Server Offline. Check back in a bit.")) {
                                        ctx.writeAndFlush(kick);
                                    }
                                    DataQueue.connections.add(
                                            new Connection(ctx.channel().attr(PROXY_ID).get().get_id(), clientVersion,
                                                    hostname.contains("FML") ? true : false, ipAddress[0],
                                                    (new Date()).toString(), false));
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
            try {
                Channel proxiedChannel = ctx.channel().attr(PROXY_CHANNEL).get();
                byte[] bytes = new byte[buf.readableBytes()];
                Main.connectionCache.getConnection(ctx.channel().attr(Proxy.CONNECTION_UUID).get()).addBytes_ingress(buf.readableBytes());
                buf.readBytes(bytes);
                proxiedChannel.writeAndFlush(Unpooled.buffer().writeBytes(bytes));
            } finally {
                buf.release();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
