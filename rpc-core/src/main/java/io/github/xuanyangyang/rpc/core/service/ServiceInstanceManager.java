package io.github.xuanyangyang.rpc.core.service;

import io.github.xuanyangyang.rpc.core.net.Client;
import io.github.xuanyangyang.rpc.core.net.ClientManager;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务实例管理
 *
 * @author xuanyangyang
 * @since 2020/10/27 13:02
 */
public class ServiceInstanceManager {
    private final Map<String, List<ServiceInstance>> instanceMap = new ConcurrentHashMap<>();
    private final ClientManager clientManager;

    public ServiceInstanceManager(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    public ServiceInstance addInstance(ServiceInfo serviceInfo) {
        ServiceInstance instance = getInstance(serviceInfo);
        if (instance != null) {
            return instance;
        }
        Client client = clientManager.getOrCreateClient(serviceInfo.getIp(), serviceInfo.getPort());
        client.connect();
        instance = new ServiceInstance(serviceInfo, client);
        instanceMap.computeIfAbsent(serviceInfo.getName(), key -> new LinkedList<>()).add(instance);
        return instance;
    }

    public boolean hasInstance(ServiceInfo serviceInfo) {
        return getInstance(serviceInfo) != null;
    }

    public ServiceInstance getInstance(ServiceInfo serviceInfo) {
        List<ServiceInstance> instances = instanceMap.getOrDefault(serviceInfo.getName(), Collections.emptyList());
        for (ServiceInstance instance : instances) {
            if (instance.getServiceInfo().getServiceKey().equals(serviceInfo.getServiceKey())) {
                return instance;
            }
        }
        return null;
    }

    public List<ServiceInstance> getInstances(String serviceName) {
        return instanceMap.getOrDefault(serviceName, Collections.emptyList());
    }
}
