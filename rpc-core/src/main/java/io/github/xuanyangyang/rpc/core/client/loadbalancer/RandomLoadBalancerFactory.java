package io.github.xuanyangyang.rpc.core.client.loadbalancer;

/**
 * 随机负载均衡工厂
 *
 * @author xuanyangyang
 * @since 2020/11/4 22:58
 */
public class RandomLoadBalancerFactory implements LoadBalancerFactory {
    @Override
    public LoadBalancer newLoadBalancer() {
        return new RandomLoadBalancer();
    }
}
