package io.github.xuanyangyang.rpc.demo;

import io.github.xuanyangyang.rpc.core.RPCContext;
import io.github.xuanyangyang.rpc.core.codec.CodecConstants;
import io.github.xuanyangyang.rpc.core.codec.DefaultCodecManager;
import io.github.xuanyangyang.rpc.core.codec.ProtostuffCodec;
import io.github.xuanyangyang.rpc.core.info.ServiceInfo;
import io.github.xuanyangyang.rpc.core.info.ServiceInfoProvider;
import io.github.xuanyangyang.rpc.core.info.ServiceInstanceManager;
import io.github.xuanyangyang.rpc.core.net.ClientManager;
import io.github.xuanyangyang.rpc.core.net.NetConstants;
import io.github.xuanyangyang.rpc.core.net.NettyServer;
import io.github.xuanyangyang.rpc.core.protocol.DefaultProtocolManager;
import io.github.xuanyangyang.rpc.core.protocol.support.DefaultProtocol;
import io.github.xuanyangyang.rpc.core.reference.RPCReferenceInfoProvider;
import io.github.xuanyangyang.rpc.core.registry.Registry;
import io.github.xuanyangyang.rpc.core.registry.support.redis.RedisRegistry;

import java.util.Collections;

/**
 * 服务提供者DEMO
 *
 * @author xuanyangyang
 * @since 2020/11/1 00:43
 */
public class ProviderDemo {
    public static void main(String[] args) {
        Registry registry = new RedisRegistry();

        DefaultCodecManager codecManager = new DefaultCodecManager();
        codecManager.addCodec(new ProtostuffCodec(CodecConstants.DEFAULT_CODEC_ID));

        DefaultProtocolManager protocolManager = new DefaultProtocolManager();
        protocolManager.addProtocol(new DefaultProtocol(codecManager));

        ClientManager clientManager = new ClientManager(protocolManager);

        ServiceInstanceManager serviceInstanceManager = new ServiceInstanceManager(clientManager);

        ServiceInfoProvider serviceInfoProvider = () -> {
            ServiceInfo serviceInfo = new ServiceInfo();
            serviceInfo.setName(HiService.class.getName());
            serviceInfo.setProtocolId(NetConstants.DEFAULT_PROTOCOL_ID);
            serviceInfo.setVersion(0);
            serviceInfo.setIp("localhost");
            serviceInfo.setPort(10000);
            return Collections.singletonList(serviceInfo);
        };
        RPCReferenceInfoProvider rpcReferenceInfoProvider = Collections::emptyList;

        RPCContext rpcContext = new RPCContext(new NettyServer(protocolManager), registry, serviceInstanceManager, serviceInfoProvider, rpcReferenceInfoProvider);
        rpcContext.init();
    }
}
