package io.github.xuanyangyang.rpc.core.client;

import io.github.xuanyangyang.rpc.core.client.filter.RemoteServiceClientFilterChain;
import io.github.xuanyangyang.rpc.core.service.ServiceInfo;

import java.util.Collection;

/**
 * 远程服务客户端管理
 *
 * @author xuanyangyang
 * @since 2020/11/3 13:08
 */
public interface RemoteServiceClientManager {
    /**
     * 通过服务信息创建远程服务客户端
     *
     * @param serviceInfo 服务信息
     * @return 远程服务客户端
     */
    RemoteServiceClient addClient(ServiceInfo serviceInfo);

    /**
     * 通过服务信息判断是否有远程服务客户端
     *
     * @param serviceInfo 服务信息
     * @return 是否有远程服务客户端
     */
    default boolean hasClient(ServiceInfo serviceInfo) {
        return getClient(serviceInfo) != null;
    }

    /**
     * 通过服务信息获取远程服务客户端
     *
     * @param serviceInfo 服务信息
     * @return 远程服务客户端
     */
    RemoteServiceClient getClient(ServiceInfo serviceInfo);

    /**
     * 通过服务名获取远程服务客户端
     *
     * @param serviceName 服务名
     * @return 远程服务客户端
     */
    Collection<RemoteServiceClient> getClients(String serviceName);

    /**
     * 通过服务名和服务ID删除远程服务客户端
     *
     * @param serviceName 服务名
     * @param serviceId   服务id
     * @return 被删除的远程服务客户端
     */
    RemoteServiceClient removeClient(String serviceName, String serviceId);

    /**
     * 添加监听
     *
     * @param listener 监听器
     */
    void addListener(RemoteServiceClientListener listener);

    /**
     * 初始化
     */
    void init();

    /**
     * 销毁
     */
    void destroy();

    interface RemoteServiceClientListener {
        /**
         * 远程客户端集合发生变化之后
         *
         * @param serviceName 服务名
         * @param clients     远程客户端集合
         */
        void afterRemoteServiceClientsChange(String serviceName, Collection<RemoteServiceClient> clients);
    }
}
