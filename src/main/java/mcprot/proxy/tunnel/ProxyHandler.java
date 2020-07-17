package mcprot.proxy.tunnel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

public class ProxyHandler extends ChannelDuplexHandler {
    //Represents the channel that we receive from
    // minecraft client to us (the server).
    private Channel originalChannel;

    public ProxyHandler(Channel originalChannel) {
        this.originalChannel =
                originalChannel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        try {
            byte[] bytes = new byte[buf.readableBytes()];
            //System.out.println(buf.readableBytes());
            buf.readBytes(bytes);

            originalChannel.writeAndFlush(Unpooled.buffer().writeBytes(bytes));
        } finally {
            buf.release();
        }
    }
}
