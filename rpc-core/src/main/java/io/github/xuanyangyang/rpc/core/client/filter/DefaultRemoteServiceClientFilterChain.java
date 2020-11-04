package io.github.xuanyangyang.rpc.core.client.filter;

import io.github.xuanyangyang.rpc.core.client.RemoteServiceClient;
import io.github.xuanyangyang.rpc.core.protocol.support.RPCInvocationInfo;

import java.util.*;

/**
 * 远程客户端过滤链
 *
 * @author xuanyangyang
 * @since 2020/11/4 21:24
 */
public class DefaultRemoteServiceClientFilterChain implements RemoteServiceClientFilterChain {
    /**
     * 过滤器列表
     */
    private List<RemoteServiceClientFilter> filters = Collections.emptyList();
    /**
     * 客户端列表
     */
    private List<RemoteServiceClient> clients = Collections.emptyList();

    @Override
    public List<RemoteServiceClient> filter(RPCInvocationInfo invocationInfo) {
        List<RemoteServiceClient> finalClients = clients;
        for (RemoteServiceClientFilter filter : filters) {
            finalClients = filter.filter(finalClients, invocationInfo);
        }
        return finalClients;
    }

    @Override
    public void addFilter(RemoteServiceClientFilter filter) {
        List<RemoteServiceClientFilter> newFilters = new ArrayList<>(filters.size() + 1);
        newFilters.addAll(this.filters);
        newFilters.add(filter);
        newFilters.sort(Comparator.comparingInt(RemoteServiceClientFilter::order));
        this.filters = newFilters;
    }

    @Override
    public void setRemoteServiceClients(Collection<RemoteServiceClient> clients) {
        this.clients = new ArrayList<>(clients);
    }
}
