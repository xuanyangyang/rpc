package io.github.xuanyangyang.rpc.core.registry;

import io.github.xuanyangyang.rpc.core.service.ServiceInfo;

/**
 * 服务信息监听
 *
 * @author xuanyangyang
 * @since 2020/10/31 16:18
 */
public interface ServiceInfoListener {
    /**
     * 移除服务之后
     *
     * @param serviceInfo 被移除的服务信息
     */
    void afterRemoveService(ServiceInfo serviceInfo);

    /**
     * 添加服务之后
     *
     * @param serviceInfo 新增的服务信息
     */
    void afterAddService(ServiceInfo serviceInfo);
}
