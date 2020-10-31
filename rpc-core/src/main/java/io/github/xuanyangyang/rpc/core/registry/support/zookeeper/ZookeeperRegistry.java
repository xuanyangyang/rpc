package io.github.xuanyangyang.rpc.core.registry.support.zookeeper;

import io.github.xuanyangyang.rpc.core.registry.Registry;
import io.github.xuanyangyang.rpc.core.registry.ServiceInfoListener;
import io.github.xuanyangyang.rpc.core.info.ServiceInfo;

import java.util.Collection;

/**
 * zookeeper 注册中心
 *
 * @author xuanyangyang
 * @since 2020/10/31 22:51
 */
public class ZookeeperRegistry implements Registry {
    @Override
    public boolean addServiceInfo(ServiceInfo serviceInfo) {
        return false;
    }

    @Override
    public ServiceInfo getServiceInfo(String serviceName, String serviceId) {
        return null;
    }

    @Override
    public Collection<ServiceInfo> getServiceInfos(String serviceName) {
        return null;
    }

    @Override
    public ServiceInfo removeServiceInfo(String serviceName, String serviceId) {
        return null;
    }

    @Override
    public void addServiceInfoListener(ServiceInfoListener listener) {

    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }
}
