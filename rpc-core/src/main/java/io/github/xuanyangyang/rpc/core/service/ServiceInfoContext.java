package io.github.xuanyangyang.rpc.core.service;

import io.github.xuanyangyang.rpc.core.net.Channel;
import io.github.xuanyangyang.rpc.core.net.Client;
import io.github.xuanyangyang.rpc.core.net.ClientContext;
import io.github.xuanyangyang.rpc.core.protocol.support.RpcInvocationInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 服务信息上下文，管理服务信息
 *
 * @author xuanyangyang
 * @since 2020/10/7 15:01
 */
public class ServiceInfoContext {
    private final Map<String, List<ServiceInfo>> serviceInfoMap = new ConcurrentHashMap<>();

    private final ClientContext clientContext;

    public ServiceInfoContext(ClientContext clientContext) {
        this.clientContext = clientContext;
    }

    public List<ServiceInfo> getServiceInfos(String serviceName) {
        return serviceInfoMap.getOrDefault(serviceName, Collections.emptyList());
    }

    public void addServiceInfos(Collection<ServiceInfo> newServiceInfos) {
        for (ServiceInfo serviceInfo : newServiceInfos) {
            List<ServiceInfo> serviceInfos = serviceInfoMap.computeIfAbsent(serviceInfo.getName(), name -> new CopyOnWriteArrayList<>());
            serviceInfos.add(serviceInfo);
        }
    }

    public List<Client> getClient(RpcInvocationInfo invocationInfo) {
        List<ServiceInfo> serviceInfos = getServiceInfos(invocationInfo.getServiceName());
        return serviceInfos.stream()
                .filter(serviceInfo -> invocationInfo.getVersion() >= serviceInfo.getVersion())
                .map(serviceInfo -> clientContext.getClient(serviceInfo.getIp(), serviceInfo.getPort()))
                .filter(Objects::nonNull)
                .filter(Channel::isConnected)
                .collect(Collectors.toList());
    }
}
