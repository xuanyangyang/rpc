package io.github.xuanyangyang.rpc.core.proxy;

import io.github.xuanyangyang.rpc.core.protocol.support.Request;
import io.github.xuanyangyang.rpc.core.protocol.support.RpcInvocationInfo;
import io.github.xuanyangyang.rpc.core.service.ServiceInfo;
import io.github.xuanyangyang.rpc.core.service.ServiceInstance;
import io.github.xuanyangyang.rpc.core.service.ServiceInstanceManager;

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
public class RpcProxyHandler implements InvocationHandler {
    private final ProxyInfo proxyInfo;
    private final ServiceInstanceManager serviceInstanceManager;

    public RpcProxyHandler(ProxyInfo proxyInfo, ServiceInstanceManager serviceInstanceManager) {
        this.proxyInfo = proxyInfo;
        this.serviceInstanceManager = serviceInstanceManager;
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
        request.setProtocolId(proxyInfo.getProtocolId());

        RpcInvocationInfo invocationInfo = new RpcInvocationInfo();
        invocationInfo.setMethodName(methodName);
        invocationInfo.setArgs(args);
        invocationInfo.setServiceName(proxyInfo.getName());
        invocationInfo.setVersion(proxyInfo.getVersion());

        request.setInvocationInfo(invocationInfo);

        List<ServiceInstance> instances = serviceInstanceManager.getInstances(proxyInfo.getName());
        ServiceInstance instance = selectInstance(instances);
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
    protected ServiceInstance selectInstance(List<ServiceInstance> instances) {
        for (ServiceInstance instance : instances) {
            ServiceInfo serviceInfo = instance.getServiceInfo();
            if (serviceInfo.getVersion() < proxyInfo.getVersion()) {
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
