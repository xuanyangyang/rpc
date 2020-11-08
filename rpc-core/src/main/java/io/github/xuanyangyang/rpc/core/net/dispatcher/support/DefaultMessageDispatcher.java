package io.github.xuanyangyang.rpc.core.net.dispatcher.support;

import io.github.xuanyangyang.rpc.core.future.DefaultFuture;
import io.github.xuanyangyang.rpc.core.net.Channel;
import io.github.xuanyangyang.rpc.core.net.dispatcher.MessageDispatcher;
import io.github.xuanyangyang.rpc.core.protocol.support.RPCInvocationInfo;
import io.github.xuanyangyang.rpc.core.protocol.support.Request;
import io.github.xuanyangyang.rpc.core.protocol.support.Response;
import io.github.xuanyangyang.rpc.core.service.ServiceInfo;
import io.github.xuanyangyang.rpc.core.service.ServiceInstance;
import io.github.xuanyangyang.rpc.core.service.ServiceInstanceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMessageDispatcher.class);
    /**
     * 返回值处理
     */
    private List<ReturnValueHandler> returnValueHandlers;

    public DefaultMessageDispatcher(ServiceInstanceManager serviceInstanceManager) {
        this.serviceInstanceManager = serviceInstanceManager;
        this.returnValueHandlers = new ArrayList<>();
        this.returnValueHandlers.add(new DefaultReturnValueHandler());
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
            LOGGER.warn("消息" + message + "被丢弃");
        }
    }

    private void dispatchRequest(Channel channel, Request request) {
        RPCInvocationInfo invocationInfo = request.getInvocationInfo();
        ServiceInstance instance = serviceInstanceManager.getInstance(invocationInfo.getServiceName());
        Supplier<Response> responseSupplier = () -> {
            Response response = new Response(request.getId());
            ServiceInfo serviceInfo = instance.getServiceInfo();
            response.setCodecId(serviceInfo.getCodecId());
            response.setProtocolId(serviceInfo.getProtocolId());
            return response;
        };
        if (instance == null) {
            Response response = responseSupplier.get();
            response.setState(Response.STATE_SERVER_ERROR);
            response.setErrMsg("找不到" + invocationInfo.getServiceName() + "服务");
            channel.send(response);
            return;
        }
        Object returnValue = instance.invoke(invocationInfo);
        if (returnValue == null) {
            Response response = responseSupplier.get();
            response.setState(Response.STATE_OK);
            channel.send(response);
        }
        try {
            for (ReturnValueHandler handler : returnValueHandlers) {
                if (handler.supports(returnValue)) {
                    handler.handleReturnValue(returnValue, responseSupplier)
                            .exceptionally(throwable -> {
                                Response response = responseSupplier.get();
                                response.setState(Response.STATE_SERVER_ERROR);
                                response.setErrMsg(throwable.getMessage());
                                LOGGER.error("处理返回值异常", throwable);
                                return response;
                            }).thenAccept(message -> handleSendFuture(channel.send(message)));
                    return;
                }
            }
            // 不应该走到这里！！！
            LOGGER.error("返回值{}被丢弃！！！", returnValue);
        } catch (Throwable throwable) {
            Response response = responseSupplier.get();
            response.setState(Response.STATE_SERVER_ERROR);
            response.setErrMsg(throwable.getMessage());
            handleSendFuture(channel.send(response));
            LOGGER.error("处理返回值异常", throwable);
        }
    }

    private void handleSendFuture(CompletionStage<Object> sendFuture) {
        sendFuture.whenComplete((data, throwable) -> LOGGER.error("发送失败", throwable));
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void addReturnValueHandlers(Collection<ReturnValueHandler> returnValueHandlers) {
        List<ReturnValueHandler> newReturnValueHandler = new ArrayList<>(this.returnValueHandlers.size() + returnValueHandlers.size());
        newReturnValueHandler.addAll(this.returnValueHandlers);
        newReturnValueHandler.addAll(returnValueHandlers);
        newReturnValueHandler.sort(Comparator.comparingInt(ReturnValueHandler::getOrder));
        this.returnValueHandlers = newReturnValueHandler;
    }
}
