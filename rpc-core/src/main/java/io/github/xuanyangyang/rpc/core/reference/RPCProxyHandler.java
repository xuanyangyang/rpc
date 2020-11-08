package io.github.xuanyangyang.rpc.core.reference;

import io.github.xuanyangyang.rpc.core.client.RemoteServiceClient;
import io.github.xuanyangyang.rpc.core.client.filter.RemoteServiceClientFilterChain;
import io.github.xuanyangyang.rpc.core.client.loadbalancer.LoadBalancer;
import io.github.xuanyangyang.rpc.core.common.RPCException;
import io.github.xuanyangyang.rpc.core.protocol.support.DefaultRPCInvocationInfo;
import io.github.xuanyangyang.rpc.core.protocol.support.Request;
import io.github.xuanyangyang.rpc.core.service.ServiceInfo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;

/**
 * rpc代理
 *
 * @author xuanyangyang
 * @since 2020/10/6 17:27
 */
public class RPCProxyHandler implements InvocationHandler {
    private final LoadBalancer loadBalancer;
    private final RPCReferenceInfo rpcReferenceInfo;
    private final RemoteServiceClientFilterChain remoteServiceClientFilterChain;

    public RPCProxyHandler(LoadBalancer loadBalancer, RPCReferenceInfo rpcReferenceInfo, RemoteServiceClientFilterChain remoteServiceClientFilterChain) {
        this.loadBalancer = loadBalancer;
        this.rpcReferenceInfo = rpcReferenceInfo;
        this.remoteServiceClientFilterChain = remoteServiceClientFilterChain;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 0) {
            if ("toString".equals(methodName)) {
                return rpcReferenceInfo.toString();
            } else if ("hashCode".equals(methodName)) {
                return rpcReferenceInfo.hashCode();
            }
        }

        Request request = new Request();

        DefaultRPCInvocationInfo invocationInfo = new DefaultRPCInvocationInfo();
        invocationInfo.setMethodName(methodName);
        invocationInfo.setArgs(args);
        invocationInfo.setServiceName(rpcReferenceInfo.getName());
        invocationInfo.setVersion(rpcReferenceInfo.getVersion());
        invocationInfo.setParameterTypes(method.getParameterTypes());
        request.setInvocationInfo(invocationInfo);

        List<RemoteServiceClient> clients = remoteServiceClientFilterChain.filter(invocationInfo);
        if (clients.isEmpty()) {
            throw new RPCException("没有可用的" + rpcReferenceInfo.getName() + "服务");
        }
        RemoteServiceClient instance = loadBalancer.select(clients, invocationInfo);
        ServiceInfo serviceInfo = instance.getServiceInfo();
        request.setProtocolId(serviceInfo.getProtocolId());
        request.setCodecId(serviceInfo.getCodecId());
        CompletableFuture<Object> future = instance.getClient().send(request, rpcReferenceInfo.getTimeout(), rpcReferenceInfo.getTimeoutTimeUnit());
        Class<?> returnType = method.getReturnType();
        if (Future.class.isAssignableFrom(returnType) || CompletionStage.class.isAssignableFrom(returnType)) {
            return future;
        }
        return future.get();
    }
}
