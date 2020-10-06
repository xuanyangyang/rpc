package io.github.xuanyangyang.rpc.core.net;

import io.github.xuanyangyang.rpc.core.protocol.support.DefaultProtocolMessageWrapper;
import io.github.xuanyangyang.rpc.core.protocol.support.Request;
import io.github.xuanyangyang.rpc.core.protocol.support.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 显示处理器用于测试
 *
 * @author xuanyangyang
 * @since 2020/10/6 14:22
 */
public class EchoHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("收到：" + msg);
        if (msg instanceof Request) {
            Request request = (Request) msg;
            Response response = new Response(request.getId());
            response.setData("ok");
            ctx.writeAndFlush(DefaultProtocolMessageWrapper.createProtocolMessage(NetConstants.DEFAULT_PROTOCOL_ID, response));
        }
    }
}
