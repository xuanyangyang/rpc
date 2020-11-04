package io.github.xuanyangyang.rpc.demo.spring.consumer;

import io.github.xuanyangyang.rpc.demo.spring.CalcService;
import io.github.xuanyangyang.rpc.demo.spring.HelloService;
import io.github.xuanyangyang.rpc.spring.reference.RPCReference;
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
    @RPCReference
    private HelloService helloService;

    @RPCReference
    private CalcService calcService;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SpringConsumerDemo.class, args);
        SpringConsumerDemo springConsumerDemo = context.getBean(SpringConsumerDemo.class);
        String result = springConsumerDemo.helloService.hello("xyy");
        System.out.println(result);

        springConsumerDemo.calcService.multiply(6, 7).thenAccept(res -> System.out.println("6 * 7 = " + res));

        System.out.println(springConsumerDemo.calcService.add(5, 7));

        System.out.println(springConsumerDemo.calcService.minus(10, 7));
    }
}
