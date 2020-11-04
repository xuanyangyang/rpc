package io.github.xuanyangyang.rpc.core.client.filter;

import io.github.xuanyangyang.rpc.core.client.RemoteServiceClient;
import io.github.xuanyangyang.rpc.core.protocol.support.RPCInvocationInfo;

import java.util.List;

/**
 * 远程客户端过滤器
 *
 * @author xuanyangyang
 * @since 2020/11/4 21:22
 */
public interface RemoteServiceClientFilter {
    /**
     * 根据规则过滤远程客户端
     *
     * @param clients        远程客户端集合
     * @param invocationInfo 调用信息
     * @return 可用的远程客户端
     */
    List<RemoteServiceClient> filter(List<RemoteServiceClient> clients, RPCInvocationInfo invocationInfo);

    /**
     * 顺序越小越优先
     *
     * @return 顺序
     */
    default int order() {
        return 0;
    }
}
