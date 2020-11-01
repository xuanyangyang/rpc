package io.github.xuanyangyang.rpc.demo.spring.provider;

import io.github.xuanyangyang.rpc.core.registry.Registry;
import io.github.xuanyangyang.rpc.core.registry.support.redis.RedisRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * spring 提供者 demo
 *
 * @author xuanyangyang
 * @since 2020/11/2 00:22
 */
@SpringBootApplication
public class SpringProviderDemo {
    @Bean
    public Registry registry() {
        return new RedisRegistry();
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringProviderDemo.class, args);
    }
}
