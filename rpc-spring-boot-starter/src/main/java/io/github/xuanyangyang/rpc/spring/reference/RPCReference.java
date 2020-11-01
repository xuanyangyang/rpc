package io.github.xuanyangyang.rpc.spring.reference;

import io.github.xuanyangyang.rpc.core.net.NetConstants;

import java.lang.annotation.*;

/**
 * rpc引用
 *
 * @author xuanyangyang
 * @since 2020/10/31 23:43
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RPCReference {
    /**
     * @return 服务名
     */
    String serviceName() default "";

    /**
     * @return 协议ID
     */
    short protocolId() default 0;

    /**
     * @return 版本
     */
    int version() default 0;
}
