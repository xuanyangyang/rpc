package io.github.xuanyangyang.rpc.core.client.filter;

import io.github.xuanyangyang.rpc.core.client.RemoteServiceClient;
import io.github.xuanyangyang.rpc.core.protocol.support.RPCInvocationInfo;

import java.util.Collection;
import java.util.List;

/**
 * 远程客户端过滤链
 *
 * @author xuanyangyang
 * @since 2020/11/4 21:18
 */
public interface RemoteServiceClientFilterChain {
    /**
     * 根据规则过滤远程客户端
     *
     * @param invocationInfo 调用信息
     * @return 可用的远程客户端
     */
    List<RemoteServiceClient> filter(RPCInvocationInfo invocationInfo);

    /**
     * 添加过滤器
     *
     * @param filter 过滤器
     */
    void addFilter(RemoteServiceClientFilter filter);

    /**
     * 设置远程客户端集合
     *
     * @param clients 远程客户端集合
     */
    void setRemoteServiceClients(Collection<RemoteServiceClient> clients);
}
