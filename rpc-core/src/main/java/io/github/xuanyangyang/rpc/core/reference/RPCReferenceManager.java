package io.github.xuanyangyang.rpc.core.reference;

import java.util.Collection;

/**
 * rpc引用信息管理
 *
 * @author xuanyangyang
 * @since 2020/11/3 23:20
 */
public interface RPCReferenceManager {
    /**
     * 添加rpc引用信息
     *
     * @param referenceInfo 引用信息
     */
    void addInfo(RPCReferenceInfo referenceInfo);

    /**
     * @return 引用信息集合
     */
    Collection<RPCReferenceInfo> getInfos();

    /**
     * 通过服务名获取引用信息
     *
     * @param serviceName 服务名
     * @return 引用信息
     */
    RPCReferenceInfo getInfo(String serviceName);

    /**
     * 是否有serviceName对应的引用信息
     *
     * @param serviceName 服务名
     * @return 是否有
     */
    boolean hasInfo(String serviceName);

    /**
     * 通过服务名获取引用，如果不存在且服务信息存在则创建
     *
     * @param serviceName 服务名
     * @param <T>         引用接口
     * @return 引用
     */
    <T> T getOrCreateReference(String serviceName);
}
