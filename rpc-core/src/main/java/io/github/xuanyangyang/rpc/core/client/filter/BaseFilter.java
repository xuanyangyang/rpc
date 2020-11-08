package io.github.xuanyangyang.rpc.core.client.filter;

import io.github.xuanyangyang.rpc.core.client.RemoteServiceClient;
import io.github.xuanyangyang.rpc.core.codec.CodecManager;
import io.github.xuanyangyang.rpc.core.protocol.ProtocolManager;
import io.github.xuanyangyang.rpc.core.protocol.support.RPCInvocationInfo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 基础过滤
 *
 * @author xuanyangyang
 * @since 2020/11/4 22:23
 */
public class BaseFilter implements RemoteServiceClientFilter {
    /**
     * 协议管理器
     */
    private final ProtocolManager protocolManager;
    /**
     * 编解码器管理器
     */
    private final CodecManager codecManager;

    public BaseFilter(ProtocolManager protocolManager, CodecManager codecManager) {
        this.protocolManager = protocolManager;
        this.codecManager = codecManager;
    }

    @Override
    public List<RemoteServiceClient> filter(List<RemoteServiceClient> clients, RPCInvocationInfo invocationInfo) {
        return clients.stream()
                .filter(remoteServiceClient -> remoteServiceClient.getServiceInfo().getVersion() >= invocationInfo.getVersion())
                .filter(remoteServiceClient -> protocolManager.hasProtocol(remoteServiceClient.getServiceInfo().getProtocolId()))
                .filter(remoteServiceClient -> codecManager.hasCodec(remoteServiceClient.getServiceInfo().getCodecId()))
                .filter(remoteServiceClient -> remoteServiceClient.getClient().isConnected())
                .collect(Collectors.toList());
    }
}
