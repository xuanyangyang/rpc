package io.github.xuanyangyang.rpc.core.proxy;

import io.github.xuanyangyang.rpc.core.future.DefaultFuture;
import io.github.xuanyangyang.rpc.core.net.Client;
import io.github.xuanyangyang.rpc.core.protocol.support.DefaultProtocolMessageWrapper;
import io.github.xuanyangyang.rpc.core.protocol.support.Request;
import io.github.xuanyangyang.rpc.core.protocol.support.RpcInvocationInfo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;

/**
 * rpc代理
 *
 * @author xuanyangyang
 * @since 2020/10/6 17:27
 */
public class RpcProxyHandler implements InvocationHandler {
    private final ProxyInfo proxyInfo;

    public RpcProxyHandler(ProxyInfo proxyInfo) {
        this.proxyInfo = proxyInfo;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 0) {
            if ("toString".equals(methodName)) {
                return proxyInfo.toString();
            } else if ("hashCode".equals(methodName)) {
                return proxyInfo.hashCode();
            }
        }

        Request request = new Request();

        RpcInvocationInfo invocationInfo = new RpcInvocationInfo();
        invocationInfo.setMethodName(methodName);
        invocationInfo.setArgs(args);
        invocationInfo.setServiceName(proxyInfo.getName());
        invocationInfo.setVersion(proxyInfo.getVersion());

        request.setInvocationInfo(invocationInfo);

        DefaultProtocolMessageWrapper protocolMessage = DefaultProtocolMessageWrapper.createProtocolMessage(proxyInfo.getProtocolId(), request);

        DefaultFuture<Object> future = DefaultFuture.newFuture(request.getId());
        Client client = null;
        client.send(protocolMessage);
        Class<?> returnType = method.getReturnType();
        if (returnType.isAssignableFrom(Future.class) || returnType.isAssignableFrom(CompletionStage.class)) {
            return future;
        }
        return future.get();
    }
}
