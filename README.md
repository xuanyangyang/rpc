# rpc
本项目用于深入研究rpc原理，若是用于项目请优先选择已有的开源框架(`dubbo`,`grpc`)

## 使用方法

### SNAPSHOT版本问题
由于目前版本是SNAPSHOT版本，所以需要在pom文件上启用SNAPSHOT
```
<repositories>
    <repository>
        <id>oss.sonatype.org-snapshot</id>
        <url>http://oss.sonatype.org/content/repositories/snapshots</url>
        <releases>
            <enabled>false</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

### Spring Boot方式

#### 添加依赖
```
<dependency>
    <groupId>io.github.xuanyangyang</groupId>
    <artifactId>rpc-spring-boot-starter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

#### 定义接口
```java
/**
 * hello 服务
 *
 * @author xuanyangyang
 * @since 2020/11/2 00:23
 */
public interface HelloService {
    /**
     * hello
     *
     * @param name 名称
     * @return hello结果
     */
    String hello(String name);

    /**
     * 延迟delay毫秒hello
     *
     * @param name  名称
     * @param delay 延迟时间，单位毫秒
     * @return hello结果
     */
    String delayHello(String name, long delay);
}
```

#### 提供服务
使用`@RPCService`进行服务提供标记
```java
/**
 * A hello
 *
 * @author xuanyangyang
 * @since 2020/11/2 00:24
 */
@RPCService
public class AHelloService implements HelloService, InitializingBean {
    @Autowired
    private RPCConfig rpcConfig;
    private String ip;

    @Override
    public String hello(String name) {
        return "hello " + name + " i am " + AHelloService.class.getName() + ", ip: " + ip + "，port:" + rpcConfig.getPort();
    }

    @Override
    public String delayHello(String name, long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello " + name + " i am " + AHelloService.class.getName() + ", ip: " + ip + "，port:" + rpcConfig.getPort();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ip = NetUtils.getLocalAddress().getHostAddress();
    }
}
```

#### 服务消费
在Spring Bean里使用`@RPCReference`注解注入服务
```java
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

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SpringConsumerDemo.class, args);
        SpringConsumerDemo springConsumerDemo = context.getBean(SpringConsumerDemo.class);
        String hello = springConsumerDemo.helloService.hello("xyy");
        System.out.println(hello);
        String delayHello = springConsumerDemo.helloService.delayHello("xyy", 10 * 1000);
        System.out.println(delayHello);
    }
}
```

### 通用方式
添加依赖
```
<dependency>
    <groupId>io.github.xuanyangyang</groupId>
    <artifactId>rpc-core</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```