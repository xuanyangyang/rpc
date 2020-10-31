package io.github.xuanyangyang.rpc.core.reference;

import io.github.xuanyangyang.rpc.core.info.ServiceInstanceManager;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * rpc代理工厂
 *
 * @author xuanyangyang
 * @since 2020/10/31 23:36
 */
public class RPCProxyFactory {
    private final ServiceInstanceManager serviceInstanceManager;

    private final Map<String, Object> rpcProxyMap = new ConcurrentHashMap<>();

    public RPCProxyFactory(ServiceInstanceManager serviceInstanceManager) {
        this.serviceInstanceManager = serviceInstanceManager;
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrCreateProxy(RPCReferenceInfo referenceInfo) {
        return (T) rpcProxyMap.computeIfAbsent(referenceInfo.getName(), name -> createProxy(referenceInfo));
    }

    private Object createProxy(RPCReferenceInfo referenceInfo) {
        return Proxy.newProxyInstance(referenceInfo.getClz().getClassLoader(), referenceInfo.getClz().getInterfaces(),
                new RPCProxyHandler(referenceInfo, serviceInstanceManager));
    }
}
