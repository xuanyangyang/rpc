package io.github.xuanyangyang.rpc.demo.common;

import io.github.xuanyangyang.rpc.core.RPCContext;
import io.github.xuanyangyang.rpc.core.codec.CodecConstants;
import io.github.xuanyangyang.rpc.core.codec.DefaultCodecManager;
import io.github.xuanyangyang.rpc.core.codec.ProtostuffCodec;
import io.github.xuanyangyang.rpc.core.net.ClientManager;
import io.github.xuanyangyang.rpc.core.net.NetConstants;
import io.github.xuanyangyang.rpc.core.net.NetUtils;
import io.github.xuanyangyang.rpc.core.net.dispatcher.DefaultMessageDispatcher;
import io.github.xuanyangyang.rpc.core.net.netty.NettyServer;
import io.github.xuanyangyang.rpc.core.protocol.DefaultProtocolManager;
import io.github.xuanyangyang.rpc.core.protocol.support.DefaultProtocol;
import io.github.xuanyangyang.rpc.core.reference.RPCReferenceInfoProvider;
import io.github.xuanyangyang.rpc.core.registry.Registry;
import io.github.xuanyangyang.rpc.core.registry.support.redis.RedisRegistry;
import io.github.xuanyangyang.rpc.core.service.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;

/**
 * 服务提供者DEMO
 *
 * @author xuanyangyang
 * @since 2020/11/1 00:43
 */
public class ProviderDemo {
    public static void main(String[] args) throws UnknownHostException {
        Registry registry = new RedisRegistry();

        DefaultCodecManager codecManager = new DefaultCodecManager();
        codecManager.addCodec(new ProtostuffCodec(CodecConstants.DEFAULT_CODEC_ID));

        DefaultProtocolManager protocolManager = new DefaultProtocolManager();
        protocolManager.addProtocol(new DefaultProtocol(codecManager));
        ServiceInstanceManager serviceInstanceManager = new ServiceInstanceManager();
        ClientManager clientManager = new ClientManager(protocolManager, new DefaultMessageDispatcher(serviceInstanceManager));

        RemoteServiceClientManager remoteServiceClientManager = new RemoteServiceClientManager(clientManager);


        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setName(HiService.class.getName());
        serviceInfo.setProtocolId(NetConstants.DEFAULT_PROTOCOL_ID);
        serviceInfo.setVersion(0);
        InetAddress localAddress = NetUtils.getLocalAddress();
        serviceInfo.setIp(localAddress.getHostAddress());
        serviceInfo.setPort(10000);
        serviceInfo.setId(serviceInfo.getName() + ":" + serviceInfo.getIp() + ":" + serviceInfo.getPort());
        LocalServiceInstance hiServiceInstance = new LocalServiceInstance(serviceInfo, new DefaultHiService());
        serviceInstanceManager.addInstance(hiServiceInstance);
        ServiceInfoProvider serviceInfoProvider = () -> Collections.singletonList(serviceInfo);
        RPCReferenceInfoProvider rpcReferenceInfoProvider = Collections::emptyList;
        RPCContext rpcContext = new RPCContext(new NettyServer(protocolManager, new DefaultMessageDispatcher(serviceInstanceManager)),
                registry, remoteServiceClientManager, serviceInfoProvider, rpcReferenceInfoProvider);
        rpcContext.init();
    }
}
