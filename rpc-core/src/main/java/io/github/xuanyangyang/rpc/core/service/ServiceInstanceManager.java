package io.github.xuanyangyang.rpc.core.service;

import java.util.Collection;

/**
 * 服务实例管理
 *
 * @author xuanyangyang
 * @since 2020/11/3 12:51
 */
public interface ServiceInstanceManager {
    /**
     * 添加实例
     *
     * @param serviceInstance 新的实例
     */
    void addInstance(ServiceInstance serviceInstance);

    /**
     * 通过服务名移除实例
     *
     * @param serviceName 服务名
     */
    void removeInstance(String serviceName);

    /**
     * 通过服务名获取实例
     *
     * @param serviceName 服务名
     * @return 服务实例
     */
    ServiceInstance getInstance(String serviceName);

    /**
     * 是否有对应服务名的实例
     *
     * @param serviceName 服务名
     * @return 是否有对应服务名的实例
     */
    boolean hasInstance(String serviceName);

    /**
     * 获取服务信息集合
     *
     * @return 服务信息集合
     */
    Collection<ServiceInfo> getServiceInfos();
}
