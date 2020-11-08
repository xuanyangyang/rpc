package io.github.xuanyangyang.rpc.spring.reference;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

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
     * @return 版本
     */
    int version() default 0;

    /**
     * @return 超时时间
     */
    long timeout() default 0;

    /**
     * @return 超时时间单位
     */
    TimeUnit timeoutTimeUnit() default TimeUnit.SECONDS;
}
