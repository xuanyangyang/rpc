package io.github.xuanyangyang.rpc.core.reference;

import io.github.xuanyangyang.rpc.core.client.RemoteServiceClientManager;
import io.github.xuanyangyang.rpc.core.client.filter.RemoteServiceClientFilterChain;
import io.github.xuanyangyang.rpc.core.client.filter.RemoteServiceClientFilterChainFactory;
import io.github.xuanyangyang.rpc.core.client.loadbalancer.LoadBalancerFactory;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * rpc代理工厂
 *
 * @author xuanyangyang
 * @since 2020/10/31 23:36
 */
public class DefaultRPCProxyFactory implements RPCProxyFactory {
    private final LoadBalancerFactory loadBalancerFactory;
    private final RemoteServiceClientManager remoteServiceClientManager;
    private final RemoteServiceClientFilterChainFactory filterChainFactory;
    private final Map<String, Object> rpcProxyMap = new ConcurrentHashMap<>();

    public DefaultRPCProxyFactory(LoadBalancerFactory loadBalancerFactory, RemoteServiceClientManager remoteServiceClientManager, RemoteServiceClientFilterChainFactory filterChainFactory) {
        this.loadBalancerFactory = loadBalancerFactory;
        this.remoteServiceClientManager = remoteServiceClientManager;
        this.filterChainFactory = filterChainFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getOrCreateProxy(RPCReferenceInfo referenceInfo) {
        return (T) rpcProxyMap.computeIfAbsent(referenceInfo.getName(), name -> createProxy(referenceInfo));
    }

    private Object createProxy(RPCReferenceInfo referenceInfo) {
        RemoteServiceClientFilterChain chain = filterChainFactory.newChain();
        chain.setRemoteServiceClients(remoteServiceClientManager.getClients(referenceInfo.getName()));
        remoteServiceClientManager.addListener((serviceName, clients) -> {
            if (referenceInfo.getName().equals(serviceName)) {
                chain.setRemoteServiceClients(clients);
            }
        });
        return Proxy.newProxyInstance(referenceInfo.getClz().getClassLoader(), new Class[]{referenceInfo.getClz()},
                new RPCProxyHandler(loadBalancerFactory.newLoadBalancer(), referenceInfo, chain));
    }
}
