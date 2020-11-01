package io.github.xuanyangyang.rpc.core.service;

import io.github.xuanyangyang.rpc.core.common.RPCException;
import io.github.xuanyangyang.rpc.core.protocol.support.RpcInvocationInfo;

/**
 * 服务实例
 *
 * @author xuanyangyang
 * @since 2020/11/1 17:09
 */
public interface ServiceInstance {
    /**
     * @return 服务名
     */
    default String getServiceName() {
        return getServiceInfo().getName();
    }

    /**
     * 调用
     *
     * @param invocationInfo 调用信息
     * @return 调用结果
     */
    Object invoke(RpcInvocationInfo invocationInfo) throws RPCException;

    /**
     * @return 服务信息
     */
    ServiceInfo getServiceInfo();

    /**
     * 获取实际实例
     *
     * @return 实际实例
     */
    Object getRealInstance();
}
