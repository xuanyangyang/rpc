package io.github.xuanyangyang.rpc.demo.common;

import io.github.xuanyangyang.rpc.core.DefaultRPCContext;
import io.github.xuanyangyang.rpc.core.RPCContext;
import io.github.xuanyangyang.rpc.core.client.DefaultRemoteServiceClientManager;
import io.github.xuanyangyang.rpc.core.client.RemoteServiceClientManager;
import io.github.xuanyangyang.rpc.core.codec.CodecManager;
import io.github.xuanyangyang.rpc.core.codec.DefaultCodecManager;
import io.github.xuanyangyang.rpc.core.codec.ProtostuffCodec;
import io.github.xuanyangyang.rpc.core.common.RPCConstants;
import io.github.xuanyangyang.rpc.core.config.RPCConfig;
import io.github.xuanyangyang.rpc.core.net.ClientManager;
import io.github.xuanyangyang.rpc.core.net.DefaultClientManager;
import io.github.xuanyangyang.rpc.core.net.NetUtils;
import io.github.xuanyangyang.rpc.core.net.Server;
import io.github.xuanyangyang.rpc.core.net.dispatcher.DefaultMessageDispatcher;
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
import io.github.xuanyangyang.rpc.core.service.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 服务提供者DEMO
 *
 * @author xuanyangyang
 * @since 2020/11/1 00:43
 */
public class ProviderDemo {
    public static void main(String[] args) throws UnknownHostException {
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
        // 创建rpc代理工厂
        RPCProxyFactory rpcProxyFactory = new DefaultRPCProxyFactory(remoteServiceClientManager);
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
    }
}
