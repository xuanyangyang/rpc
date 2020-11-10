package io.github.xuanyangyang.rpc.demo.spring.consumer;

import io.github.xuanyangyang.rpc.demo.spring.AsyncService;
import io.github.xuanyangyang.rpc.demo.spring.CalcService;
import io.github.xuanyangyang.rpc.demo.spring.HelloService;
import io.github.xuanyangyang.rpc.spring.reference.RPCReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * spring 消费者demo
 *
 * @author xuanyangyang
 * @since 2020/11/2 00:22
 */
@SpringBootApplication
public class SpringConsumerDemo {
    @RPCReference(timeout = 3)
    private HelloService helloService;

    @RPCReference
    private CalcService calcService;

    @RPCReference
    private AsyncService asyncService;

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringConsumerDemo.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SpringConsumerDemo.class, args);
        SpringConsumerDemo springConsumerDemo = context.getBean(SpringConsumerDemo.class);
        // 负载均衡检查
        for (int i = 0; i < 10; i++) {
            // 应该有不同的ip:port
            String result = springConsumerDemo.helloService.hello("xyy");
            System.out.println(result);
        }
        springConsumerDemo.calcService.multiply(6, 7).whenComplete((res, throwable) -> {
            if (throwable == null) {
                System.out.println("6 * 7 = " + res);
            } else {
                LOGGER.error("multiply调用异常", throwable);
            }
        });
        System.out.println(springConsumerDemo.calcService.add(5, 7));
        System.out.println(springConsumerDemo.calcService.minus(10, 7));
        String delayHello = springConsumerDemo.helloService.delayHello("xyy", 2 * 1000);
        System.out.println(delayHello);
        springConsumerDemo.asyncService.hi("xyy").thenAccept(System.out::println);
    }
}
