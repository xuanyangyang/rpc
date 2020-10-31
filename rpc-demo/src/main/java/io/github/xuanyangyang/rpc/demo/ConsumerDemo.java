package io.github.xuanyangyang.rpc.demo;

import io.github.xuanyangyang.rpc.core.RPCContext;
import io.github.xuanyangyang.rpc.core.codec.CodecConstants;
import io.github.xuanyangyang.rpc.core.codec.DefaultCodecManager;
import io.github.xuanyangyang.rpc.core.codec.ProtostuffCodec;
import io.github.xuanyangyang.rpc.core.info.ServiceInfoProvider;
import io.github.xuanyangyang.rpc.core.info.ServiceInstanceManager;
import io.github.xuanyangyang.rpc.core.net.ClientManager;
import io.github.xuanyangyang.rpc.core.net.NetConstants;
import io.github.xuanyangyang.rpc.core.net.NettyServer;
import io.github.xuanyangyang.rpc.core.protocol.DefaultProtocolManager;
import io.github.xuanyangyang.rpc.core.protocol.support.DefaultProtocol;
import io.github.xuanyangyang.rpc.core.reference.RPCProxyFactory;
import io.github.xuanyangyang.rpc.core.reference.RPCReferenceInfo;
import io.github.xuanyangyang.rpc.core.reference.RPCReferenceInfoProvider;
import io.github.xuanyangyang.rpc.core.registry.Registry;
import io.github.xuanyangyang.rpc.core.registry.support.redis.RedisRegistry;

import java.util.Collections;

/**
 * 消费组demo
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

        ClientManager clientManager = new ClientManager(protocolManager);

        ServiceInstanceManager serviceInstanceManager = new ServiceInstanceManager(clientManager);

        ServiceInfoProvider serviceInfoProvider = Collections::emptyList;

        RPCReferenceInfo rpcReferenceInfo = new RPCReferenceInfo();
        rpcReferenceInfo.setClz(HiService.class);
        rpcReferenceInfo.setName(HiService.class.getName());
        rpcReferenceInfo.setProtocolId(NetConstants.DEFAULT_PROTOCOL_ID);
        rpcReferenceInfo.setVersion(0);
        RPCReferenceInfoProvider rpcReferenceInfoProvider = () -> Collections.singletonList(rpcReferenceInfo);

        RPCProxyFactory rpcProxyFactory = new RPCProxyFactory(serviceInstanceManager);
        HiService hiService = rpcProxyFactory.getOrCreateProxy(rpcReferenceInfo);
        RPCContext rpcContext = new RPCContext(new NettyServer(protocolManager), registry, serviceInstanceManager, serviceInfoProvider, rpcReferenceInfoProvider);
        rpcContext.init();
        hiService.sayHi();
    }
}
