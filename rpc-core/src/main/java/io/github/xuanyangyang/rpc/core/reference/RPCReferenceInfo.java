package io.github.xuanyangyang.rpc.core.reference;

/**
 * @author xuanyangyang
 * @since 2020/11/3 17:22
 */
public interface RPCReferenceInfo {
    /**
     * @return 服务名
     */
    String getName();

    /**
     * @return 协议id
     */
    Short getProtocolId();

    /**
     * @return 服务版本
     */
    int getVersion();

    /**
     * @return 引用的接口class
     */
    Class<?> getClz();
}
