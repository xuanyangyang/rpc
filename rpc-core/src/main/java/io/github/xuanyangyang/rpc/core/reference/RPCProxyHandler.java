package io.github.xuanyangyang.rpc.core.reference;

import io.github.xuanyangyang.rpc.core.service.ServiceInfo;
import io.github.xuanyangyang.rpc.core.service.RemoteServiceClient;
import io.github.xuanyangyang.rpc.core.service.RemoteServiceClientManager;
import io.github.xuanyangyang.rpc.core.protocol.support.Request;
import io.github.xuanyangyang.rpc.core.protocol.support.RpcInvocationInfo;

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
    private final RPCReferenceInfo RPCReferenceInfo;
    private final RemoteServiceClientManager remoteServiceClientManager;

    public RPCProxyHandler(RPCReferenceInfo RPCReferenceInfo, RemoteServiceClientManager remoteServiceClientManager) {
        this.RPCReferenceInfo = RPCReferenceInfo;
        this.remoteServiceClientManager = remoteServiceClientManager;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 0) {
            if ("toString".equals(methodName)) {
                return RPCReferenceInfo.toString();
            } else if ("hashCode".equals(methodName)) {
                return RPCReferenceInfo.hashCode();
            }
        }

        Request request = new Request();
        request.setProtocolId(RPCReferenceInfo.getProtocolId());

        RpcInvocationInfo invocationInfo = new RpcInvocationInfo();
        invocationInfo.setMethodName(methodName);
        invocationInfo.setArgs(args);
        invocationInfo.setServiceName(RPCReferenceInfo.getName());
        invocationInfo.setVersion(RPCReferenceInfo.getVersion());

        request.setInvocationInfo(invocationInfo);

        Collection<RemoteServiceClient> instances = remoteServiceClientManager.getInstances(RPCReferenceInfo.getName());
        RemoteServiceClient instance = selectInstance(instances);
        CompletableFuture<Object> future = instance.getClient().send(request);
        Class<?> returnType = method.getReturnType();
        if (returnType.isAssignableFrom(Future.class) || returnType.isAssignableFrom(CompletionStage.class)) {
            return future;
        }
        return future.get();
    }

    /**
     * 选择一个实例
     *
     * @param instances 实例列表
     * @return 选择的实例
     */
    protected RemoteServiceClient selectInstance(Collection<RemoteServiceClient> instances) {
        for (RemoteServiceClient instance : instances) {
            ServiceInfo serviceInfo = instance.getServiceInfo();
            if (serviceInfo.getVersion() < RPCReferenceInfo.getVersion()) {
                continue;
            }
            if (!instance.getClient().isConnected()) {
                continue;
            }
            return instance;
        }
        return null;
    }
}
