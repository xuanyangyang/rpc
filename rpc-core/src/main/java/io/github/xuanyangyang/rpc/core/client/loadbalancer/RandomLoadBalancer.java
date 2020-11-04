package io.github.xuanyangyang.rpc.core.client.loadbalancer;

import io.github.xuanyangyang.rpc.core.client.RemoteServiceClient;
import io.github.xuanyangyang.rpc.core.protocol.support.RPCInvocationInfo;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机负载均衡
 *
 * @author xuanyangyang
 * @since 2020/11/4 22:46
 */
public class RandomLoadBalancer implements LoadBalancer {
    @Override
    public RemoteServiceClient select(List<RemoteServiceClient> clients, RPCInvocationInfo invocationInfo) {
        int size = clients.size();
        int index = ThreadLocalRandom.current().nextInt(size);
        return clients.get(index);
    }
}
