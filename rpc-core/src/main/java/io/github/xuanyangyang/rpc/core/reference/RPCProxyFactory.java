package io.github.xuanyangyang.rpc.core.reference;

/**
 * rpc代理工厂
 *
 * @author xuanyangyang
 * @since 2020/11/3 17:19
 */
public interface RPCProxyFactory {
    /**
     * 创建代理
     *
     * @param referenceInfo 引用信息
     * @param <T>           接口的类型
     * @return 接口的类型的代理
     */
    <T> T getOrCreateProxy(RPCReferenceInfo referenceInfo);
}
