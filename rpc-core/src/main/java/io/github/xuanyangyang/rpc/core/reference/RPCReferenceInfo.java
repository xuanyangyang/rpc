package io.github.xuanyangyang.rpc.core.reference;

import java.util.concurrent.TimeUnit;

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
     * @return 服务版本
     */
    int getVersion();

    /**
     * @return 引用的接口class
     */
    Class<?> getClz();

    /**
     * @return 超时时间
     */
    long getTimeout();

    /**
     * @return 超时时间单位
     */
    TimeUnit getTimeoutTimeUnit();
}
