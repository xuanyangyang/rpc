package io.github.xuanyangyang.rpc.spring;

import io.github.xuanyangyang.rpc.core.DefaultRPCContext;
import io.github.xuanyangyang.rpc.core.RPCContext;
import io.github.xuanyangyang.rpc.core.client.DefaultRemoteServiceClientManager;
import io.github.xuanyangyang.rpc.core.client.RemoteServiceClientManager;
import io.github.xuanyangyang.rpc.core.client.filter.BaseFilter;
import io.github.xuanyangyang.rpc.core.client.filter.DefaultRemoteServiceClientFilterChainFactory;
import io.github.xuanyangyang.rpc.core.client.filter.RemoteServiceClientFilterChainFactory;
import io.github.xuanyangyang.rpc.core.client.loadbalancer.LoadBalancerFactory;
import io.github.xuanyangyang.rpc.core.client.loadbalancer.RandomLoadBalancerFactory;
import io.github.xuanyangyang.rpc.core.codec.Codec;
import io.github.xuanyangyang.rpc.core.codec.CodecManager;
import io.github.xuanyangyang.rpc.core.codec.DefaultCodecManager;
import io.github.xuanyangyang.rpc.core.codec.ProtostuffCodec;
import io.github.xuanyangyang.rpc.core.common.RPCConstants;
import io.github.xuanyangyang.rpc.core.config.RPCConfig;
import io.github.xuanyangyang.rpc.core.net.ClientManager;
import io.github.xuanyangyang.rpc.core.net.DefaultClientManager;
import io.github.xuanyangyang.rpc.core.net.Server;
import io.github.xuanyangyang.rpc.core.net.dispatcher.MessageDispatcher;
import io.github.xuanyangyang.rpc.core.net.dispatcher.support.AsyncReturnValueHandler;
import io.github.xuanyangyang.rpc.core.net.dispatcher.support.DefaultMessageDispatcher;
import io.github.xuanyangyang.rpc.core.net.dispatcher.support.ReturnValueHandler;
import io.github.xuanyangyang.rpc.core.net.netty.NettyServer;
import io.github.xuanyangyang.rpc.core.protocol.DefaultProtocolManager;
import io.github.xuanyangyang.rpc.core.protocol.ProtocolManager;
import io.github.xuanyangyang.rpc.core.protocol.support.DefaultProtocol;
import io.github.xuanyangyang.rpc.core.reference.DefaultRPCProxyFactory;
import io.github.xuanyangyang.rpc.core.reference.DefaultRPCReferenceManager;
import io.github.xuanyangyang.rpc.core.reference.RPCProxyFactory;
import io.github.xuanyangyang.rpc.core.reference.RPCReferenceManager;
import io.github.xuanyangyang.rpc.core.registry.Registry;
import io.github.xuanyangyang.rpc.core.registry.support.redis.RedisConfig;
import io.github.xuanyangyang.rpc.core.registry.support.redis.RedisRegistry;
import io.github.xuanyangyang.rpc.core.registry.support.zookeeper.ZookeeperConfig;
import io.github.xuanyangyang.rpc.core.registry.support.zookeeper.ZookeeperRegistry;
import io.github.xuanyangyang.rpc.core.service.DefaultServiceInstanceManager;
import io.github.xuanyangyang.rpc.core.service.ServiceInstanceManager;
import io.github.xuanyangyang.rpc.spring.common.SpringConstants;
import io.github.xuanyangyang.rpc.spring.config.SpringRPCProperties;
import io.github.xuanyangyang.rpc.spring.reference.RPCReferenceBeanProcessor;
import io.github.xuanyangyang.rpc.spring.service.RPCServiceBeanPostProcessor;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * rpc自动配置
 *
 * @author xuanyangyang
 * @since 2020/11/1 22:33
 */
@EnableConfigurationProperties(SpringRPCProperties.class)
@Import({RPCReferenceBeanProcessor.class, RPCServiceBeanPostProcessor.class})
public class RPCAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(ServiceInstanceManager.class)
    public ServiceInstanceManager serviceInstanceManager() {
        return new DefaultServiceInstanceManager();
    }

    @Bean
    @ConditionalOnMissingBean(MessageDispatcher.class)
    public MessageDispatcher messageDispatcher(ServiceInstanceManager serviceInstanceManager, @Qualifier(SpringConstants.MESSAGE_EXECUTOR) Executor executor, ApplicationContext applicationContext) {
        DefaultMessageDispatcher messageDispatcher = new DefaultMessageDispatcher(serviceInstanceManager);
        messageDispatcher.setExecutor(executor);
        Map<String, ReturnValueHandler> returnValueHandlerMap = applicationContext.getBeansOfType(ReturnValueHandler.class);
        messageDispatcher.addReturnValueHandlers(returnValueHandlerMap.values());
        return messageDispatcher;
    }

    @Bean
    @ConditionalOnBean(MessageDispatcher.class)
    @ConditionalOnMissingBean(name = SpringConstants.MESSAGE_EXECUTOR)
    public Executor messageExecutor() {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2, new DefaultThreadFactory("消息执行线程"));
    }

    @Bean
    @ConditionalOnMissingBean(ProtocolManager.class)
    public ProtocolManager protocolManager() {
        return new DefaultProtocolManager();
    }

    @Bean
    @ConditionalOnMissingBean(Server.class)
    public Server server(ProtocolManager protocolManager, MessageDispatcher messageDispatcher) {
        return new NettyServer(protocolManager, messageDispatcher);
    }

    @Bean
    @ConditionalOnMissingBean(ClientManager.class)
    public ClientManager clientManager(ProtocolManager protocolManager, MessageDispatcher messageDispatcher) {
        return new DefaultClientManager(protocolManager, messageDispatcher);
    }

    @Bean
    @ConditionalOnMissingBean(RemoteServiceClientManager.class)
    public RemoteServiceClientManager remoteServiceClientManager(ClientManager clientManager) {
        return new DefaultRemoteServiceClientManager(clientManager);
    }

    @Bean
    @ConditionalOnMissingBean(RemoteServiceClientFilterChainFactory.class)
    public RemoteServiceClientFilterChainFactory remoteServiceClientFilterChainFactory() {
        return new DefaultRemoteServiceClientFilterChainFactory();
    }

    @Bean
    @ConditionalOnMissingBean(RPCProxyFactory.class)
    public RPCProxyFactory rpcProxyFactory(LoadBalancerFactory loadBalancerFactory, RemoteServiceClientManager remoteServiceClientManager, RemoteServiceClientFilterChainFactory filterChainFactory) {
        return new DefaultRPCProxyFactory(loadBalancerFactory, remoteServiceClientManager, filterChainFactory);
    }

    @Bean
    @ConditionalOnMissingBean(RPCContext.class)
    public RPCContext rpcContext(Server server, Registry registry, ServiceInstanceManager serviceInstanceManager, RemoteServiceClientManager remoteServiceClientManager,
                                 RPCReferenceManager referenceInfoManager, RPCConfig config) {
        return new DefaultRPCContext(server, registry, serviceInstanceManager, remoteServiceClientManager, referenceInfoManager, config);
    }

    @Bean
    @ConditionalOnBean(RPCContext.class)
    public RPCBoostrap rpcBoostrap(RPCContext rpcContext, CodecManager codecManager, ProtocolManager protocolManager, RemoteServiceClientFilterChainFactory filterChainFactory) {
        return new RPCBoostrap(rpcContext, codecManager, protocolManager, filterChainFactory);
    }

    @Bean
    @ConditionalOnMissingBean(CodecManager.class)
    public CodecManager codecManager() {
        return new DefaultCodecManager();
    }

    @Bean(name = {SpringConstants.DEFAULT_CODEC, "protostuffCodec"})
    public ProtostuffCodec protostuffCodec() {
        return new ProtostuffCodec(RPCConstants.DEFAULT_CODEC_ID);
    }

    @Bean
    public DefaultProtocol defaultProtocol(CodecManager codecManager) {
        return new DefaultProtocol(codecManager);
    }

    @Bean
    @ConditionalOnMissingBean(RPCReferenceManager.class)
    public RPCReferenceManager referenceManager(RPCProxyFactory rpcProxyFactory) {
        return new DefaultRPCReferenceManager(rpcProxyFactory);
    }

    @Bean
    public RPCConfig rpcConfig(SpringRPCProperties rpcProperties) {
        RPCConfig rpcConfig = new RPCConfig();
        rpcConfig.setPort(rpcProperties.getPort());
        rpcConfig.setTimeout(rpcProperties.getTimeout());
        rpcConfig.setTimeoutTimeUnit(rpcProperties.getTimeoutTimeUnit());
        return rpcConfig;
    }

    @Bean
    @ConditionalOnProperty(prefix = "rpc.registry.redis", name = "enable")
    public RedisConfig redisConfig(SpringRPCProperties rpcProperties) {
        RedisConfig redisConfig = new RedisConfig();
        redisConfig.setConfigPath(rpcProperties.getRegistry().getRedis().getConfigPath());
        return redisConfig;
    }

    @Bean
    @ConditionalOnBean(RedisConfig.class)
    @ConditionalOnMissingBean(Registry.class)
    public Registry redisRegistry(RedisConfig redisConfig) {
        return new RedisRegistry(redisConfig);
    }

    @Bean
    public BaseFilter baseFilter() {
        return new BaseFilter();
    }

    @Bean
    @ConditionalOnMissingBean(LoadBalancerFactory.class)
    public LoadBalancerFactory loadBalancerFactory() {
        return new RandomLoadBalancerFactory();
    }

    @Bean
    @ConditionalOnProperty(prefix = "rpc.registry.zookeeper", name = "enable")
    public ZookeeperConfig zookeeperConfig(SpringRPCProperties rpcProperties) {
        SpringRPCProperties.SpringZookeeperProperties zookeeperProperties = rpcProperties.getRegistry().getZookeeper();
        ZookeeperConfig zookeeperConfig = new ZookeeperConfig();
        zookeeperConfig.setAddress(zookeeperProperties.getAddress());
        zookeeperConfig.setRootPath(zookeeperProperties.getRootPath());
        return zookeeperConfig;
    }

    @Bean
    @ConditionalOnBean(ZookeeperConfig.class)
    @ConditionalOnMissingBean(Registry.class)
    public Registry zookeeperRegistry(ZookeeperConfig zookeeperConfig, @Qualifier(SpringConstants.DEFAULT_CODEC) Codec codec) {
        return new ZookeeperRegistry(zookeeperConfig, codec);
    }

    @Bean
    public AsyncReturnValueHandler completionStageReturnValueHandler() {
        return new AsyncReturnValueHandler();
    }
}
