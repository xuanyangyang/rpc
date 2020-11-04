package io.github.xuanyangyang.rpc.core.net;

import io.github.xuanyangyang.rpc.core.net.dispatcher.MessageDispatcher;
import io.github.xuanyangyang.rpc.core.net.netty.NettyChannel;
import io.github.xuanyangyang.rpc.core.net.netty.NettyUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 分发器
 *
 * @author xuanyangyang
 * @since 2020/11/1 18:00
 */
@ChannelHandler.Sharable
public class DispatcherHandler extends ChannelInboundHandlerAdapter {
    private final MessageDispatcher messageDispatcher;

    public DispatcherHandler(MessageDispatcher messageDispatcher) {
        this.messageDispatcher = messageDispatcher;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        NettyUtils.setChannel(ctx.channel(), new NettyChannel(ctx.channel()));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        messageDispatcher.dispatch(NettyUtils.getChannel(ctx.channel()), msg);
    }
}
