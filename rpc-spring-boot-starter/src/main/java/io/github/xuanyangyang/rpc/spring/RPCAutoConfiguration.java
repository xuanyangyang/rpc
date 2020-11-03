package io.github.xuanyangyang.rpc.spring;

import io.github.xuanyangyang.rpc.core.RPCContext;
import io.github.xuanyangyang.rpc.core.codec.CodecManager;
import io.github.xuanyangyang.rpc.core.codec.DefaultCodecManager;
import io.github.xuanyangyang.rpc.core.codec.ProtostuffCodec;
import io.github.xuanyangyang.rpc.core.common.RPCConstants;
import io.github.xuanyangyang.rpc.core.net.ClientManager;
import io.github.xuanyangyang.rpc.core.net.DefaultClientManager;
import io.github.xuanyangyang.rpc.core.net.Server;
import io.github.xuanyangyang.rpc.core.net.dispatcher.DefaultMessageDispatcher;
import io.github.xuanyangyang.rpc.core.net.dispatcher.MessageDispatcher;
import io.github.xuanyangyang.rpc.core.net.netty.NettyServer;
import io.github.xuanyangyang.rpc.core.protocol.DefaultProtocolManager;
import io.github.xuanyangyang.rpc.core.protocol.ProtocolManager;
import io.github.xuanyangyang.rpc.core.protocol.support.DefaultProtocol;
import io.github.xuanyangyang.rpc.core.reference.RPCProxyFactory;
import io.github.xuanyangyang.rpc.core.reference.RPCReferenceInfoProvider;
import io.github.xuanyangyang.rpc.core.registry.Registry;
import io.github.xuanyangyang.rpc.core.service.*;
import io.github.xuanyangyang.rpc.spring.common.SpringConstants;
import io.github.xuanyangyang.rpc.spring.config.RPCConfig;
import io.github.xuanyangyang.rpc.spring.reference.AnnotationRPCReferenceInfoProvider;
import io.github.xuanyangyang.rpc.spring.service.RPCServiceBeanPostProcessor;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * rpc自动配置
 *
 * @author xuanyangyang
 * @since 2020/11/1 22:33
 */
@EnableConfigurationProperties(RPCConfig.class)
public class RPCAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ServiceInstanceManager.class)
    public ServiceInstanceManager serviceInstanceManager() {
        return new DefaultServiceInstanceManager();
    }

    @Bean
    @ConditionalOnMissingBean(MessageDispatcher.class)
    public MessageDispatcher messageDispatcher(ServiceInstanceManager serviceInstanceManager, @Qualifier(SpringConstants.MESSAGE_EXECUTOR) Executor executor) {
        DefaultMessageDispatcher messageDispatcher = new DefaultMessageDispatcher(serviceInstanceManager);
        messageDispatcher.setExecutor(executor);
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
    public RPCProxyFactory rpcProxyFactory(RemoteServiceClientManager remoteServiceClientManager) {
        return new RPCProxyFactory(remoteServiceClientManager);
    }

    @Bean
    public AnnotationRPCReferenceInfoProvider rpcReferenceInfoProvider(RPCProxyFactory rpcProxyFactory) {
        return new AnnotationRPCReferenceInfoProvider(rpcProxyFactory);
    }

    @Bean
    @ConditionalOnMissingBean(RPCContext.class)
    public RPCContext rpcContext(Server server, Registry registry, RemoteServiceClientManager remoteServiceClientManager,
                                 ServiceInfoProvider serviceInfoProvider,
                                 RPCReferenceInfoProvider RPCReferenceInfoProvider) {
        return new RPCContext(server, registry, remoteServiceClientManager, serviceInfoProvider, RPCReferenceInfoProvider);
    }

    @Bean
    public RPCServiceBeanPostProcessor rpcServiceBeanPostProcessor(RPCConfig rpcConfig, ServiceInstanceManager serviceInstanceManager) {
        return new RPCServiceBeanPostProcessor(rpcConfig, serviceInstanceManager);
    }

    @Bean
    @ConditionalOnBean(RPCContext.class)
    public RPCBoostrap rpcBoostrap(RPCContext rpcContext, CodecManager codecManager, ProtocolManager protocolManager) {
        return new RPCBoostrap(rpcContext, codecManager, protocolManager);
    }

    @Bean
    @ConditionalOnMissingBean(CodecManager.class)
    public CodecManager codecManager() {
        return new DefaultCodecManager();
    }

    @Bean
    public ProtostuffCodec protostuffCodec() {
        return new ProtostuffCodec(RPCConstants.DEFAULT_CODEC_ID);
    }

    @Bean
    public DefaultProtocol defaultProtocol(CodecManager codecManager) {
        return new DefaultProtocol(codecManager);
    }
}
