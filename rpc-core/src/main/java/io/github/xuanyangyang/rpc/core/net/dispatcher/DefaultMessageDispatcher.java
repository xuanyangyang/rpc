package io.github.xuanyangyang.rpc.core.net.dispatcher;

import io.github.xuanyangyang.rpc.core.common.RPCException;
import io.github.xuanyangyang.rpc.core.future.DefaultFuture;
import io.github.xuanyangyang.rpc.core.net.Channel;
import io.github.xuanyangyang.rpc.core.protocol.support.Request;
import io.github.xuanyangyang.rpc.core.protocol.support.Response;
import io.github.xuanyangyang.rpc.core.protocol.support.RpcInvocationInfo;
import io.github.xuanyangyang.rpc.core.service.ServiceInstance;
import io.github.xuanyangyang.rpc.core.service.ServiceInstanceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

/**
 * 消息分发器
 *
 * @author xuanyangyang
 * @since 2020/11/1 20:52
 */
public class DefaultMessageDispatcher implements MessageDispatcher {
    /**
     * 服务实例管理
     */
    private final ServiceInstanceManager serviceInstanceManager;
    /**
     * 执行器
     */
    private Executor executor;
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(DefaultMessageDispatcher.class);

    public DefaultMessageDispatcher(ServiceInstanceManager serviceInstanceManager) {
        this.serviceInstanceManager = serviceInstanceManager;
    }

    @Override
    public void dispatch(Channel channel, Object message) {
        if (executor == null) {
            dispatch0(channel, message);
        } else {
            executor.execute(() -> dispatch0(channel, message));
        }
    }

    private void dispatch0(Channel channel, Object message) {
        if (message instanceof Request) {
            dispatchRequest(channel, (Request) message);
        } else if (message instanceof Response) {
            DefaultFuture.received((Response) message);
        } else {
            logger.warn("消息" + message + "被丢弃");
        }
    }

    private void dispatchRequest(Channel channel, Request request) {
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
            if (result instanceof CompletionStage) {
                ((CompletionStage<?>) result).thenAccept(response::setData);
            } else if (result instanceof Future) {
                try {
                    response.setData(((Future<?>) result).get());
                } catch (InterruptedException | ExecutionException e) {
                    throw new RPCException(invocationInfo.getServiceName() + "执行" + invocationInfo.getMethodName() + "方法异常", e);
                }
            } else {
                response.setData(result);
            }
        } catch (RPCException e) {
            response.setState(Response.STATE_SERVER_ERROR);
            response.setErrMsg(e.getMessage());
        }
        // todo 异常处理
        channel.send(response);
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }
}
