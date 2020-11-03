package io.github.xuanyangyang.rpc.core.reference;

import io.github.xuanyangyang.rpc.core.common.RPCException;
import io.github.xuanyangyang.rpc.core.protocol.support.Request;
import io.github.xuanyangyang.rpc.core.protocol.support.RpcInvocationInfo;
import io.github.xuanyangyang.rpc.core.service.RemoteServiceClient;
import io.github.xuanyangyang.rpc.core.service.RemoteServiceClientManager;
import io.github.xuanyangyang.rpc.core.service.ServiceInfo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
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
    private final RPCReferenceInfo rpcReferenceInfo;
    private final RemoteServiceClientManager remoteServiceClientManager;

    public RPCProxyHandler(RPCReferenceInfo rpcReferenceInfo, RemoteServiceClientManager remoteServiceClientManager) {
        this.rpcReferenceInfo = rpcReferenceInfo;
        this.remoteServiceClientManager = remoteServiceClientManager;
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
        request.setProtocolId(rpcReferenceInfo.getProtocolId());

        RpcInvocationInfo invocationInfo = new RpcInvocationInfo();
        invocationInfo.setMethodName(methodName);
        invocationInfo.setArgs(args);
        invocationInfo.setServiceName(rpcReferenceInfo.getName());
        invocationInfo.setVersion(rpcReferenceInfo.getVersion());
        invocationInfo.setParameterTypes(method.getParameterTypes());

        request.setInvocationInfo(invocationInfo);

        Collection<RemoteServiceClient> clients = remoteServiceClientManager.getClients(rpcReferenceInfo.getName());
        RemoteServiceClient instance = selectClient(clients);
        if (instance == null) {
            throw new RPCException("没有可用的" + rpcReferenceInfo.getName() + "服务");
        }
        CompletableFuture<Object> future = instance.getClient().send(request);
        Class<?> returnType = method.getReturnType();
        if (Future.class.isAssignableFrom(returnType) || CompletionStage.class.isAssignableFrom(returnType)) {
            return future;
        }
        return future.get();
    }

    /**
     * 选择一个客户端
     *
     * @param clients 客户端列表
     * @return 选择的客户端
     */
    protected RemoteServiceClient selectClient(Collection<RemoteServiceClient> clients) {
        for (RemoteServiceClient client : clients) {
            ServiceInfo serviceInfo = client.getServiceInfo();
            if (serviceInfo.getVersion() < rpcReferenceInfo.getVersion()) {
                continue;
            }
            if (!client.getClient().isConnected()) {
                continue;
            }
            return client;
        }
        return null;
    }
}
