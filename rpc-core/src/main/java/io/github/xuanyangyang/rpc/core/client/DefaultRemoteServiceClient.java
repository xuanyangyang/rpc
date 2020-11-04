package io.github.xuanyangyang.rpc.core.client;

import io.github.xuanyangyang.rpc.core.net.Client;
import io.github.xuanyangyang.rpc.core.service.ServiceInfo;

/**
 * 远程服务客户端
 *
 * @author xuanyangyang
 * @since 2020/10/7 17:45
 */
public class DefaultRemoteServiceClient implements RemoteServiceClient {
    private final ServiceInfo serviceInfo;
    private final Client client;

    public DefaultRemoteServiceClient(ServiceInfo serviceInfo, Client client) {
        this.serviceInfo = serviceInfo;
        this.client = client;
    }

    @Override
    public ServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    @Override
    public Client getClient() {
        return client;
    }
}
