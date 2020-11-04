package io.github.xuanyangyang.rpc.core.client.loadbalancer;

import io.github.xuanyangyang.rpc.core.client.RemoteServiceClient;
import io.github.xuanyangyang.rpc.core.protocol.support.RPCInvocationInfo;

import java.util.List;

/**
 * 轮询负载均衡器
 *
 * @author xuanyangyang
 * @since 2020/11/4 23:04
 */
public class RoundRobinLoadBalancer implements LoadBalancer {
    /**
     * 选择次数
     */
    private int selectCount;

    @Override
    public RemoteServiceClient select(List<RemoteServiceClient> clients, RPCInvocationInfo invocationInfo) {
        int size = clients.size();
        int index = selectCount++ % size;
        return clients.get(index);
    }
}
