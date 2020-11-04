package io.github.xuanyangyang.rpc.core.client.loadbalancer;

/**
 * 负载均衡工厂
 *
 * @author xuanyangyang
 * @since 2020/11/4 22:56
 */
public interface LoadBalancerFactory {
    /**
     * 新建一个负载均衡器
     *
     * @return 负载均衡器
     */
    LoadBalancer newLoadBalancer();
}
