package io.github.xuanyangyang.rpc.core.registry;

import io.github.xuanyangyang.rpc.core.info.ServiceInfo;

import java.util.Collection;

/**
 * 注册中心，服务发现与注册
 *
 * @author xuanyangyang
 * @since 2020/10/31 16:13
 */
public interface Registry {
    /**
     * 添加服务信息
     *
     * @param serviceInfo 服务信息
     * @return 添加是否成功
     */
    boolean addServiceInfo(ServiceInfo serviceInfo);

    /**
     * 获取服务信息
     *
     * @param serviceName 服务名
     * @param serviceId   服务ID
     * @return 服务信息
     */
    ServiceInfo getServiceInfo(String serviceName, String serviceId);

    /**
     * 通过服务名获取服务信息集合
     *
     * @param serviceName 服务名
     * @return 服务信息集合
     */
    Collection<ServiceInfo> getServiceInfos(String serviceName);

    /**
     * 通过服务名与服务ID删除服务
     *
     * @param serviceName 服务名
     * @param serviceId   服务ID
     * @return 被删除的服务信息
     */
    ServiceInfo removeServiceInfo(String serviceName, String serviceId);

    /**
     * 添加服务信息监听
     *
     * @param listener 服务信息监听
     */
    void addServiceInfoListener(ServiceInfoListener listener);

    /**
     * 初始化
     */
    void init();

    /**
     * 销毁
     */
    void destroy();
}
