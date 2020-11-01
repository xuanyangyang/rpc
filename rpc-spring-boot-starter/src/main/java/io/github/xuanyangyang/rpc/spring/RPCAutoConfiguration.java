package io.github.xuanyangyang.rpc.spring;

import io.github.xuanyangyang.rpc.core.RPCContext;
import io.github.xuanyangyang.rpc.core.codec.CodecConstants;
import io.github.xuanyangyang.rpc.core.codec.CodecManager;
import io.github.xuanyangyang.rpc.core.codec.DefaultCodecManager;
import io.github.xuanyangyang.rpc.core.codec.ProtostuffCodec;
import io.github.xuanyangyang.rpc.core.net.ClientManager;
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
import io.github.xuanyangyang.rpc.core.service.RemoteServiceClientManager;
import io.github.xuanyangyang.rpc.core.service.ServiceInfoProvider;
import io.github.xuanyangyang.rpc.core.service.ServiceInstanceManager;
import io.github.xuanyangyang.rpc.spring.config.RPCConfig;
import io.github.xuanyangyang.rpc.spring.reference.AnnotationRPCReferenceInfoProvider;
import io.github.xuanyangyang.rpc.spring.service.RPCServiceBeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * rpc自动配置
 *
 * @author xuanyangyang
 * @since 2020/11/1 22:33
 */
@EnableConfigurationProperties(RPCConfig.class)
public class RPCAutoConfiguration {

    @Bean
    public ServiceInstanceManager serviceInstanceManager() {
        return new ServiceInstanceManager();
    }

    @Bean
    @ConditionalOnMissingBean(MessageDispatcher.class)
    public MessageDispatcher messageDispatcher(ServiceInstanceManager serviceInstanceManager) {
        return new DefaultMessageDispatcher(serviceInstanceManager);
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
    public ClientManager clientManager(ProtocolManager protocolManager, MessageDispatcher messageDispatcher) {
        return new ClientManager(protocolManager, messageDispatcher);
    }

    @Bean
    public RemoteServiceClientManager remoteServiceClientManager(ClientManager clientManager) {
        return new RemoteServiceClientManager(clientManager);
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
        return new ProtostuffCodec(CodecConstants.DEFAULT_CODEC_ID);
    }

    @Bean
    public DefaultProtocol defaultProtocol(CodecManager codecManager) {
        return new DefaultProtocol(codecManager);
    }
}
