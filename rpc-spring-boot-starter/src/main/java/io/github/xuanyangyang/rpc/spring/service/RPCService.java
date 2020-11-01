package io.github.xuanyangyang.rpc.spring.service;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * rpc服务
 *
 * @author xuanyangyang
 * @since 2020/11/1 00:00
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface RPCService {
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
    short protocolId() default 0;
}
