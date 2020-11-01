package io.github.xuanyangyang.rpc.core.service;

import java.util.Collection;

/**
 * 服务信息提供者
 *
 * @author xuanyangyang
 * @since 2020/10/31 23:23
 */
public interface ServiceInfoProvider {
    /**
     * 获取服务信息集合
     *
     * @return 服务信息集合
     */
    Collection<ServiceInfo> getServiceInfos();
}
