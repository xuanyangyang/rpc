package io.github.xuanyangyang.rpc.core.client.filter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * 远程客户端过滤链工厂
 *
 * @author xuanyangyang
 * @since 2020/11/4 22:07
 */
public class DefaultRemoteServiceClientFilterChainFactory implements RemoteServiceClientFilterChainFactory {
    private final List<RemoteServiceClientFilter> filters = new LinkedList<>();

    @Override
    public RemoteServiceClientFilterChain newChain() {
        DefaultRemoteServiceClientFilterChain filterChain = new DefaultRemoteServiceClientFilterChain();
        for (RemoteServiceClientFilter filter : filters) {
            filterChain.addFilter(filter);
        }
        return filterChain;
    }

    @Override
    public void addFilter(RemoteServiceClientFilter filter) {
        filters.add(filter);
    }

    @Override
    public void addFilters(Collection<RemoteServiceClientFilter> filters) {
        this.filters.addAll(filters);
    }
}
