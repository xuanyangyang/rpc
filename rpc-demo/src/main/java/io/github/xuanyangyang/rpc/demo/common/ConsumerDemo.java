package io.github.xuanyangyang.rpc.demo.common;

import io.github.xuanyangyang.rpc.core.DefaultRPCContext;
import io.github.xuanyangyang.rpc.core.RPCContext;
import io.github.xuanyangyang.rpc.core.client.DefaultRemoteServiceClientManager;
import io.github.xuanyangyang.rpc.core.client.RemoteServiceClientManager;
import io.github.xuanyangyang.rpc.core.client.filter.BaseFilter;
import io.github.xuanyangyang.rpc.core.client.filter.DefaultRemoteServiceClientFilterChainFactory;
import io.github.xuanyangyang.rpc.core.client.filter.RemoteServiceClientFilterChainFactory;
import io.github.xuanyangyang.rpc.core.client.loadbalancer.RandomLoadBalancerFactory;
import io.github.xuanyangyang.rpc.core.codec.CodecManager;
import io.github.xuanyangyang.rpc.core.codec.DefaultCodecManager;
import io.github.xuanyangyang.rpc.core.codec.ProtostuffCodec;
import io.github.xuanyangyang.rpc.core.common.RPCConstants;
import io.github.xuanyangyang.rpc.core.config.RPCConfig;
import io.github.xuanyangyang.rpc.core.net.ClientManager;
import io.github.xuanyangyang.rpc.core.net.DefaultClientManager;
import io.github.xuanyangyang.rpc.core.net.Server;
import io.github.xuanyangyang.rpc.core.net.dispatcher.support.DefaultMessageDispatcher;
import io.github.xuanyangyang.rpc.core.net.netty.NettyServer;
import io.github.xuanyangyang.rpc.core.protocol.DefaultProtocolManager;
import io.github.xuanyangyang.rpc.core.protocol.ProtocolManager;
import io.github.xuanyangyang.rpc.core.protocol.support.DefaultProtocol;
import io.github.xuanyangyang.rpc.core.reference.*;
import io.github.xuanyangyang.rpc.core.registry.Registry;
import io.github.xuanyangyang.rpc.core.registry.support.redis.RedisConfig;
import io.github.xuanyangyang.rpc.core.registry.support.redis.RedisRegistry;
import io.github.xuanyangyang.rpc.core.service.DefaultServiceInstanceManager;
import io.github.xuanyangyang.rpc.core.service.ServiceInstanceManager;

/**
 * 消费者demo
 *
 * @author xuanyangyang
 * @since 2020/11/1 00:29
 */
public class ConsumerDemo {
    public static void main(String[] args) {
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
    }
}
