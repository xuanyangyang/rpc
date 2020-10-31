package io.github.xuanyangyang.rpc.core.reference;

import java.util.Collection;

/**
 * rpc引用信息提供者
 *
 * @author xuanyangyang
 * @since 2020/11/1 00:12
 */
public interface RPCReferenceInfoProvider {
    /**
     * 获取rpc引用信息集合
     *
     * @return rpc引用集合
     */
    Collection<RPCReferenceInfo> getProxyInfos();
}
