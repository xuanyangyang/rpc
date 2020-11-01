package io.github.xuanyangyang.rpc.demo.common;

import io.github.xuanyangyang.rpc.core.RPCContext;
import io.github.xuanyangyang.rpc.core.codec.CodecConstants;
import io.github.xuanyangyang.rpc.core.codec.DefaultCodecManager;
import io.github.xuanyangyang.rpc.core.codec.ProtostuffCodec;
import io.github.xuanyangyang.rpc.core.net.ClientManager;
import io.github.xuanyangyang.rpc.core.net.NetConstants;
import io.github.xuanyangyang.rpc.core.net.netty.NettyServer;
import io.github.xuanyangyang.rpc.core.net.dispatcher.DefaultMessageDispatcher;
import io.github.xuanyangyang.rpc.core.protocol.DefaultProtocolManager;
import io.github.xuanyangyang.rpc.core.protocol.support.DefaultProtocol;
import io.github.xuanyangyang.rpc.core.reference.RPCProxyFactory;
import io.github.xuanyangyang.rpc.core.reference.RPCReferenceInfo;
import io.github.xuanyangyang.rpc.core.reference.RPCReferenceInfoProvider;
import io.github.xuanyangyang.rpc.core.registry.Registry;
import io.github.xuanyangyang.rpc.core.registry.support.redis.RedisRegistry;
import io.github.xuanyangyang.rpc.core.service.RemoteServiceClientManager;
import io.github.xuanyangyang.rpc.core.service.ServiceInfoProvider;
import io.github.xuanyangyang.rpc.core.service.ServiceInstanceManager;

import java.util.Collections;

/**
 * 消费者demo
 *
 * @author xuanyangyang
 * @since 2020/11/1 00:29
 */
public class ConsumerDemo {
    public static void main(String[] args) {
        Registry registry = new RedisRegistry();

        DefaultCodecManager codecManager = new DefaultCodecManager();
        codecManager.addCodec(new ProtostuffCodec(CodecConstants.DEFAULT_CODEC_ID));

        DefaultProtocolManager protocolManager = new DefaultProtocolManager();
        protocolManager.addProtocol(new DefaultProtocol(codecManager));
        ServiceInstanceManager serviceInstanceManager = new ServiceInstanceManager();
        ClientManager clientManager = new ClientManager(protocolManager, new DefaultMessageDispatcher(serviceInstanceManager));

        RemoteServiceClientManager remoteServiceClientManager = new RemoteServiceClientManager(clientManager);


        ServiceInfoProvider serviceInfoProvider = Collections::emptyList;

        RPCReferenceInfo rpcReferenceInfo = new RPCReferenceInfo();
        rpcReferenceInfo.setClz(HiService.class);
        rpcReferenceInfo.setName(HiService.class.getName());
        rpcReferenceInfo.setProtocolId(NetConstants.DEFAULT_PROTOCOL_ID);
        rpcReferenceInfo.setVersion(0);
        RPCReferenceInfoProvider rpcReferenceInfoProvider = () -> Collections.singletonList(rpcReferenceInfo);

        RPCProxyFactory rpcProxyFactory = new RPCProxyFactory(remoteServiceClientManager);
        HiService hiService = rpcProxyFactory.getOrCreateProxy(rpcReferenceInfo);
        RPCContext rpcContext = new RPCContext(new NettyServer(protocolManager, new DefaultMessageDispatcher(serviceInstanceManager)), registry, remoteServiceClientManager, serviceInfoProvider, rpcReferenceInfoProvider);
        rpcContext.init();
        String res = hiService.sayHi();
        System.out.println("收到：" + res);
    }
}
