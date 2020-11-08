# rpc
本项目用于深入研究rpc原理，若是用于项目请优先选择已有的开源框架(`dubbo`,`grpc`)

## 项目架构
![项目架构](/images/rpc架构.png)

## 详细文档
[WIKI](https://github.com/xuanyangyang/rpc/wiki)

## 快速开始

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

#### 配置注册中心
启用redis，默认地址为redis://127.0.0.1:6379
```
# rpc配置
rpc:
  #  注册中心配置
  registry:
    #    redis 配置
    redis:
      #      是否启用
      enable: true
```

如需自定义redis地址，设置address属性
```
# rpc配置
rpc:
  #  注册中心配置
  registry:
    #    redis 配置
    redis:
      #      是否启用
      enable: true
      #      redis地址
      address: redis://127.0.0.1:8888
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

#### 定义接口
```java
/**
 * 嗨服务
 *
 * @author xuanyangyang
 * @since 2020/11/1 00:38
 */
public interface HiService {
    /**
     * @return hi结果
     */
    String sayHi();
}
```

#### 提供服务
```java
// 创建redis注册中心
Registry registry = new RedisRegistry(new RedisConfig());
// 创建默认codec管理
CodecManager codecManager = new DefaultCodecManager();
// 添加默认codec
codecManager.addCodec(new ProtostuffCodec(RPCConstants.DEFAULT_CODEC_ID));
// 创建默认协议管理
ProtocolManager protocolManager = new DefaultProtocolManager();
// 添加默认协议
protocolManager.addProtocol(new DefaultProtocol(codecManager));
// 创建服务实例管理
ServiceInstanceManager serviceInstanceManager = new DefaultServiceInstanceManager();
// 创建客户端管理
ClientManager clientManager = new DefaultClientManager(protocolManager, new DefaultMessageDispatcher(serviceInstanceManager));
// 创建远程服务客户端管理
RemoteServiceClientManager remoteServiceClientManager = new DefaultRemoteServiceClientManager(clientManager);
// 创建过滤工厂
RemoteServiceClientFilterChainFactory filterChainFactory = new DefaultRemoteServiceClientFilterChainFactory();
filterChainFactory.addFilter(new BaseFilter());
// 创建rpc代理工厂
RPCProxyFactory rpcProxyFactory = new DefaultRPCProxyFactory(new RandomLoadBalancerFactory(), remoteServiceClientManager, filterChainFactory);
// rpc引用管理
RPCReferenceManager rpcReferenceManager = new DefaultRPCReferenceManager(rpcProxyFactory);
// rpc配置
RPCConfig config = new RPCConfig();
config.setPort(10000);
// 构建服务信息
ServiceInfo serviceInfo = new ServiceInfo();
serviceInfo.setName(HiService.class.getName());
serviceInfo.setProtocolId(RPCConstants.DEFAULT_PROTOCOL_ID);
serviceInfo.setVersion(0);
InetAddress localAddress = NetUtils.getLocalAddress();
serviceInfo.setIp(localAddress.getHostAddress());
serviceInfo.setPort(config.getPort());
serviceInfo.setId(serviceInfo.getName() + ":" + serviceInfo.getIp() + ":" + serviceInfo.getPort());
// 创建本地服务实例
ServiceInstance hiServiceInstance = new LocalServiceInstance(serviceInfo, new DefaultHiService());
serviceInstanceManager.addInstance(hiServiceInstance);
// 创建服务端
Server server = new NettyServer(protocolManager, new DefaultMessageDispatcher(serviceInstanceManager));
// 构建rpc上下文
RPCContext rpcContext = new DefaultRPCContext(server, registry, serviceInstanceManager,
        remoteServiceClientManager, rpcReferenceManager, config);
// 初始化rpc
rpcContext.start();
```

#### 服务消费
```java
// 创建redis注册中心
Registry registry = new RedisRegistry(new RedisConfig());
// 创建默认codec管理
CodecManager codecManager = new DefaultCodecManager();
// 添加默认codec
codecManager.addCodec(new ProtostuffCodec(RPCConstants.DEFAULT_CODEC_ID));
// 创建默认协议管理
ProtocolManager protocolManager = new DefaultProtocolManager();
// 添加默认协议
protocolManager.addProtocol(new DefaultProtocol(codecManager));
// 创建服务实例管理
ServiceInstanceManager serviceInstanceManager = new DefaultServiceInstanceManager();
// 创建客户端管理
ClientManager clientManager = new DefaultClientManager(protocolManager, new DefaultMessageDispatcher(serviceInstanceManager));
// 创建远程服务客户端管理
RemoteServiceClientManager remoteServiceClientManager = new DefaultRemoteServiceClientManager(clientManager);
// 创建过滤工厂
RemoteServiceClientFilterChainFactory filterChainFactory = new DefaultRemoteServiceClientFilterChainFactory();
filterChainFactory.addFilter(new BaseFilter());
// 创建rpc代理工厂
RPCProxyFactory rpcProxyFactory = new DefaultRPCProxyFactory(new RandomLoadBalancerFactory(), remoteServiceClientManager, filterChainFactory);
// 构造一个rpc引用
DefaultRPCReferenceInfo rpcReferenceInfo = new DefaultRPCReferenceInfo();
rpcReferenceInfo.setClz(HiService.class);
rpcReferenceInfo.setName(HiService.class.getName());
rpcReferenceInfo.setProtocolId(RPCConstants.DEFAULT_PROTOCOL_ID);
rpcReferenceInfo.setVersion(0);
// 创建引用管理
RPCReferenceManager referenceManager = new DefaultRPCReferenceManager(rpcProxyFactory);
referenceManager.addInfo(rpcReferenceInfo);
// 创建服务端
Server server = new NettyServer(protocolManager, new DefaultMessageDispatcher(serviceInstanceManager));
// rpc配置
RPCConfig config = new RPCConfig();
// 构建rpc上下文
RPCContext rpcContext = new DefaultRPCContext(server, registry, serviceInstanceManager,
        remoteServiceClientManager, referenceManager, config);
rpcContext.start();
// 创建嗨服务代理
HiService hiService = referenceManager.getOrCreateReference(rpcReferenceInfo.getName());
// 调用服务
String res = hiService.sayHi();
System.out.println("收到：" + res);
```