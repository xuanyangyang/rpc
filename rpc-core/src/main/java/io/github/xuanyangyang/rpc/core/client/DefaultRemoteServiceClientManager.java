package io.github.xuanyangyang.rpc.core.client;

import io.github.xuanyangyang.rpc.core.net.Client;
import io.github.xuanyangyang.rpc.core.net.ClientManager;
import io.github.xuanyangyang.rpc.core.service.ServiceInfo;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 远程服务客户端管理
 *
 * @author xuanyangyang
 * @since 2020/10/27 13:02
 */
public class DefaultRemoteServiceClientManager implements RemoteServiceClientManager {
    /**
     * serviceName -> serviceId -> serviceInstance
     */
    private final Map<String, Map<String, RemoteServiceClient>> name2InstanceMap = new ConcurrentHashMap<>();
    private final ClientManager clientManager;

    public DefaultRemoteServiceClientManager(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public RemoteServiceClient addClient(ServiceInfo serviceInfo) {
        RemoteServiceClient remoteServiceClient = getClient(serviceInfo);
        if (remoteServiceClient != null) {
            return remoteServiceClient;
        }
        Client client = clientManager.getOrCreateClient(serviceInfo.getIp(), serviceInfo.getPort());
        client.connect();
        remoteServiceClient = new DefaultRemoteServiceClient(serviceInfo, client);
        Map<String, RemoteServiceClient> instanceMap = name2InstanceMap.computeIfAbsent(serviceInfo.getName(), key -> new ConcurrentHashMap<>());
        instanceMap.put(remoteServiceClient.getServiceInfo().getId(), remoteServiceClient);
        return remoteServiceClient;
    }

    @Override
    public RemoteServiceClient getClient(ServiceInfo serviceInfo) {
        Map<String, RemoteServiceClient> instanceMap = name2InstanceMap.getOrDefault(serviceInfo.getName(), Collections.emptyMap());
        return instanceMap.get(serviceInfo.getId());
    }

    @Override
    public Collection<RemoteServiceClient> getClients(String serviceName) {
        return name2InstanceMap.getOrDefault(serviceName, Collections.emptyMap()).values();
    }

    @Override
    public RemoteServiceClient removeClient(String serviceName, String serviceId) {
        Map<String, RemoteServiceClient> instanceMap = name2InstanceMap.getOrDefault(serviceName, Collections.emptyMap());
        return instanceMap.remove(serviceId);
    }

    @Override
    public void init() {
        clientManager.init();
    }

    @Override
    public void destroy() {
        clientManager.destroy();
    }
}
