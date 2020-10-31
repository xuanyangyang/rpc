package io.github.xuanyangyang.rpc.core.info;

import io.github.xuanyangyang.rpc.core.net.Client;

/**
 * 服务实例
 *
 * @author xuanyangyang
 * @since 2020/10/7 17:45
 */
public class ServiceInstance {
    private final ServiceInfo serviceInfo;
    private final Client client;

    public ServiceInstance(ServiceInfo serviceInfo, Client client) {
        this.serviceInfo = serviceInfo;
        this.client = client;
    }

    public ServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    public Client getClient() {
        return client;
    }
}
