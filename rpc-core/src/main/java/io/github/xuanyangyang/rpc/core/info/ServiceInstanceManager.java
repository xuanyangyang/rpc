package io.github.xuanyangyang.rpc.core.info;

import io.github.xuanyangyang.rpc.core.net.Client;
import io.github.xuanyangyang.rpc.core.net.ClientManager;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务实例管理
 *
 * @author xuanyangyang
 * @since 2020/10/27 13:02
 */
public class ServiceInstanceManager {
    /**
     * serviceName -> serviceId -> serviceInstance
     */
    private final Map<String, Map<String, ServiceInstance>> name2InstanceMap = new ConcurrentHashMap<>();
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
        Map<String, ServiceInstance> instanceMap = name2InstanceMap.computeIfAbsent(serviceInfo.getName(), key -> new ConcurrentHashMap<>());
        instanceMap.put(instance.getServiceInfo().getId(), instance);
        return instance;
    }

    public boolean hasInstance(ServiceInfo serviceInfo) {
        return getInstance(serviceInfo) != null;
    }

    public ServiceInstance getInstance(ServiceInfo serviceInfo) {
        Map<String, ServiceInstance> instanceMap = name2InstanceMap.get(serviceInfo.getName());
        if (CollectionUtils.isEmpty(instanceMap)) {
            return null;
        }
        return instanceMap.get(serviceInfo.getId());
    }

    public Collection<ServiceInstance> getInstances(String serviceName) {
        return name2InstanceMap.getOrDefault(serviceName, Collections.emptyMap()).values();
    }

    public ServiceInstance removeInstance(String serviceName, String serviceId) {
        Map<String, ServiceInstance> instanceMap = name2InstanceMap.get(serviceName);
        if (CollectionUtils.isEmpty(instanceMap)) {
            return null;
        }
        return instanceMap.remove(serviceId);
    }
}
