package io.github.xuanyangyang.rpc.core.service;

import io.github.xuanyangyang.rpc.core.net.Client;

/**
 * 远程服务实例
 *
 * @author xuanyangyang
 * @since 2020/10/7 17:45
 */
public class RemoteServiceClient {
    private final ServiceInfo serviceInfo;
    private final Client client;

    public RemoteServiceClient(ServiceInfo serviceInfo, Client client) {
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
