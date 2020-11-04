package io.github.xuanyangyang.rpc.core.client.filter;

import io.github.xuanyangyang.rpc.core.client.RemoteServiceClient;
import io.github.xuanyangyang.rpc.core.protocol.support.RPCInvocationInfo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 基础过滤
 *
 * @author xuanyangyang
 * @since 2020/11/4 22:23
 */
public class BaseFilter implements RemoteServiceClientFilter {
    @Override
    public List<RemoteServiceClient> filter(List<RemoteServiceClient> clients, RPCInvocationInfo invocationInfo) {
        return clients.stream()
                .filter(remoteServiceClient -> remoteServiceClient.getServiceInfo().getVersion() >= invocationInfo.getVersion())
                .filter(remoteServiceClient -> remoteServiceClient.getClient().isConnected())
                .collect(Collectors.toList());
    }
}
