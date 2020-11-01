package io.github.xuanyangyang.rpc.core.service;

/**
 * rpc服务
 *
 * @author xuanyangyang
 * @since 2020/11/1 00:00
 */
public @interface RPCService {
    /**
     * @return 服务ID
     */
    String id() default "";

    /**
     * @return 服务名
     */
    String name() default "";

    /**
     * @return 服务版本
     */
    int version() default 0;

    /**
     * @return 协议ID
     */
    short protocolId();
}
