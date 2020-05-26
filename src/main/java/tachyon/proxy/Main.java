package tachyon.proxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import tachyon.proxy.cache.Cache;
import tachyon.proxy.log.Log;
import tachyon.proxy.signing.Signing;
import tachyon.proxy.tunnel.Proxy;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Main {
    private static EventLoopGroup bossGroup = new NioEventLoopGroup();
    private static EventLoopGroup workerGroup = new NioEventLoopGroup();

    private static boolean debug = true;

    public static void main(String args[]) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        Signing.init();

        Log.log(Log.MessageType.INFO, "Starting Tachyon Proxy on port 25565");
        Cache.updateCache("localhost", "mc.hypixel.net", 25565);

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new Proxy());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind("0.0.0.0", (short) 25565).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static boolean isDebug() {
        return debug;
    }

    public static EventLoopGroup getBossGroup() {
        return bossGroup;
    }

    public static EventLoopGroup getWorkerGroup() {
        return workerGroup;
    }
}
