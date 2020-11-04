package io.github.xuanyangyang.rpc.core.client.loadbalancer;

import io.github.xuanyangyang.rpc.core.client.RemoteServiceClient;
import io.github.xuanyangyang.rpc.core.protocol.support.RPCInvocationInfo;

import java.util.List;

/**
 * 负载均衡器
 *
 * @author xuanyangyang
 * @since 2020/11/4 22:28
 */
public interface LoadBalancer {
    /**
     * 从远程服务客户端列表里选择一个
     *
     * @param clients        远程服务客户端列表
     * @param invocationInfo 调用信息
     * @return 远程服务客户端
     */
    RemoteServiceClient select(List<RemoteServiceClient> clients, RPCInvocationInfo invocationInfo);
}
