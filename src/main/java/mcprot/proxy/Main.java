package mcprot.proxy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import mcprot.proxy.cache.ConnectionCache;
import mcprot.proxy.cache.Scheduler;
import mcprot.proxy.log.Log;
import mcprot.proxy.signing.Signing;
import mcprot.proxy.tunnel.Proxy;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Main {
    private static EventLoopGroup bossGroup = new NioEventLoopGroup();
    private static EventLoopGroup workerGroup = new NioEventLoopGroup();

    private static boolean debug = true;

    private static Config config;

    public static ConnectionCache connectionCache;

    public static void main(String args[]) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        loadConfig();
        Signing.init();

        connectionCache = new ConnectionCache();

        Scheduler.runScheduler();
        Log.log(Log.MessageType.INFO, "Starting Proxy on port " + config.getPort());

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

            ChannelFuture f = b.bind(config.getIp(), config.getPort()).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private static void loadConfig() {

        File file = new File(System.getProperty("user.dir") + "/config.json");
        if (file.exists()) {
            Gson gson = new Gson();
            try {
                config = gson.fromJson(new FileReader(file), Config.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            //Create default configuration file.
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Could not create default configuration file.");
                System.exit(0);
                e.printStackTrace();
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Config defaultConfig = new Config();
            defaultConfig.setIp("0.0.0.0");
            defaultConfig.setPort((short) 25565);
            defaultConfig.setApiKey("secretKey");

            try {
                FileWriter writer = new FileWriter(file.getAbsolutePath());
                gson.toJson(defaultConfig, writer);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            config = defaultConfig;
        }
    }

    public static Config getConfig() {
        return config;
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
