package io.github.xuanyangyang.rpc.core.net;

import io.github.xuanyangyang.rpc.core.common.RPCException;
import io.github.xuanyangyang.rpc.core.future.DefaultFuture;
import io.github.xuanyangyang.rpc.core.protocol.support.Request;
import io.github.xuanyangyang.rpc.core.protocol.support.Response;
import io.github.xuanyangyang.rpc.core.protocol.support.RpcInvocationInfo;
import io.github.xuanyangyang.rpc.core.service.ServiceInstance;
import io.github.xuanyangyang.rpc.core.service.ServiceInstanceManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 分发器
 *
 * @author xuanyangyang
 * @since 2020/11/1 18:00
 */
@ChannelHandler.Sharable
public class DispatcherHandler extends ChannelInboundHandlerAdapter {
    /**
     * 服务实例管理
     */
    private final ServiceInstanceManager serviceInstanceManager;
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(DispatcherHandler.class);

    public DispatcherHandler(ServiceInstanceManager serviceInstanceManager) {
        this.serviceInstanceManager = serviceInstanceManager;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Request) {
            Request request = (Request) msg;
            Response response = new Response(request.getId());
            RpcInvocationInfo invocationInfo = request.getInvocationInfo();
            ServiceInstance instance = serviceInstanceManager.getInstance(invocationInfo.getServiceName());
            if (instance == null) {
                response.setState(Response.STATE_SERVER_ERROR);
                response.setErrMsg("找不到" + invocationInfo.getServiceName() + "服务");
                return;
            }
            try {
                Object result = instance.invoke(invocationInfo);
                response.setData(result);
            } catch (RPCException e) {
                response.setState(Response.STATE_SERVER_ERROR);
                response.setErrMsg(e.getMessage());
            }
            ctx.channel().writeAndFlush(response);
        } else if (msg instanceof Response) {
            DefaultFuture.received((Response) msg);
        } else {
            logger.warn("消息" + msg + "被丢弃");
        }
    }
}
