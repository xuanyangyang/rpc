package io.github.xuanyangyang.rpc.core.service;

import io.github.xuanyangyang.rpc.core.net.Client;
import io.github.xuanyangyang.rpc.core.net.ClientManager;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 远程服务实例管理
 *
 * @author xuanyangyang
 * @since 2020/10/27 13:02
 */
public class RemoteServiceClientManager {
    /**
     * serviceName -> serviceId -> serviceInstance
     */
    private final Map<String, Map<String, RemoteServiceClient>> name2InstanceMap = new ConcurrentHashMap<>();
    private final ClientManager clientManager;

    public RemoteServiceClientManager(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    public RemoteServiceClient addInstance(ServiceInfo serviceInfo) {
        RemoteServiceClient instance = getInstance(serviceInfo);
        if (instance != null) {
            return instance;
        }
        Client client = clientManager.getOrCreateClient(serviceInfo.getIp(), serviceInfo.getPort());
        client.connect();
        instance = new RemoteServiceClient(serviceInfo, client);
        Map<String, RemoteServiceClient> instanceMap = name2InstanceMap.computeIfAbsent(serviceInfo.getName(), key -> new ConcurrentHashMap<>());
        instanceMap.put(instance.getServiceInfo().getId(), instance);
        return instance;
    }

    public boolean hasInstance(ServiceInfo serviceInfo) {
        return getInstance(serviceInfo) != null;
    }

    public RemoteServiceClient getInstance(ServiceInfo serviceInfo) {
        Map<String, RemoteServiceClient> instanceMap = name2InstanceMap.getOrDefault(serviceInfo.getName(), Collections.emptyMap());
        return instanceMap.get(serviceInfo.getId());
    }

    public Collection<RemoteServiceClient> getInstances(String serviceName) {
        return name2InstanceMap.getOrDefault(serviceName, Collections.emptyMap()).values();
    }

    public RemoteServiceClient removeInstance(String serviceName, String serviceId) {
        Map<String, RemoteServiceClient> instanceMap = name2InstanceMap.getOrDefault(serviceName, Collections.emptyMap());
        return instanceMap.remove(serviceId);
    }

    public void init() {
        clientManager.init();
    }

    public void destroy() {
        clientManager.destroy();
    }
}
