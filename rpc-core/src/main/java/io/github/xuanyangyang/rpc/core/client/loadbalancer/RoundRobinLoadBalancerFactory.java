package io.github.xuanyangyang.rpc.core.client.loadbalancer;

/**
 * 轮询负载均衡器工厂
 *
 * @author xuanyangyang
 * @since 2020/11/4 23:09
 */
public class RoundRobinLoadBalancerFactory implements LoadBalancerFactory {
    @Override
    public LoadBalancer newLoadBalancer() {
        return new RoundRobinLoadBalancer();
    }
}
