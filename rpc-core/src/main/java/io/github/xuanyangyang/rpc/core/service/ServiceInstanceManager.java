package io.github.xuanyangyang.rpc.core.service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务实例管理
 *
 * @author xuanyangyang
 * @since 2020/11/1 17:29
 */
public class ServiceInstanceManager implements ServiceInfoProvider{
    /**
     * serviceName -> ServiceInstance
     */
    private final Map<String, ServiceInstance> instanceMap = new ConcurrentHashMap<>();

    public void addInstance(ServiceInstance serviceInstance) {
        instanceMap.put(serviceInstance.getServiceName(), serviceInstance);
    }

    public void removeInstance(String serviceName) {
        instanceMap.remove(serviceName);
    }

    public ServiceInstance getInstance(String serviceName) {
        return instanceMap.get(serviceName);
    }

    @Override
    public Collection<ServiceInfo> getServiceInfos() {
        return null;
    }
}
