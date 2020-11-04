package io.github.xuanyangyang.rpc.core.client.filter;

import java.util.Collection;

/**
 * 远程客户端过滤链工厂
 *
 * @author xuanyangyang
 * @since 2020/11/4 21:56
 */
public interface RemoteServiceClientFilterChainFactory {
    /**
     * 创建新的过滤链
     *
     * @return 过滤链
     */
    RemoteServiceClientFilterChain newChain();

    /**
     * 添加过滤器
     *
     * @param filter 过滤器
     */
    void addFilter(RemoteServiceClientFilter filter);

    /**
     * 添加过滤器集合
     *
     * @param filters 过滤器集合
     */
    void addFilters(Collection<RemoteServiceClientFilter> filters);
}
