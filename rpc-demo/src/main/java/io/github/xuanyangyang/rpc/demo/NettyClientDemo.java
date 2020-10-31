package io.github.xuanyangyang.rpc.demo;

import io.github.xuanyangyang.rpc.core.codec.CodecConstants;
import io.github.xuanyangyang.rpc.core.codec.DefaultCodecManager;
import io.github.xuanyangyang.rpc.core.codec.ProtostuffCodec;
import io.github.xuanyangyang.rpc.core.net.NetConstants;
import io.github.xuanyangyang.rpc.core.net.NettyClient;
import io.github.xuanyangyang.rpc.core.protocol.DefaultProtocolManager;
import io.github.xuanyangyang.rpc.core.protocol.support.DefaultProtocol;
import io.github.xuanyangyang.rpc.core.protocol.support.Request;
import io.github.xuanyangyang.rpc.core.protocol.support.RpcInvocationInfo;

/**
 * @author xuanyangyang
 * @since 2020/10/6 15:24
 */
public class NettyClientDemo {
    public static void main(String[] args) throws InterruptedException {
        DefaultCodecManager codecManager = new DefaultCodecManager();
        codecManager.addCodec(new ProtostuffCodec(CodecConstants.DEFAULT_CODEC_ID));
        DefaultProtocolManager protocolManager = new DefaultProtocolManager();
        protocolManager.addProtocol(new DefaultProtocol(codecManager));
        NettyClient nettyClient = new NettyClient("localhost", 10000, protocolManager);
        nettyClient.connect();
        Request request = new Request();
        RpcInvocationInfo invocationInfo = new RpcInvocationInfo();
        invocationInfo.setMethodName("hello");
        invocationInfo.setServiceName("Hello");
        invocationInfo.setVersion(1);
        invocationInfo.setArgs(new Object[]{1, 2, 3});
        request.setInvocationInfo(invocationInfo);
        request.setProtocolId(NetConstants.DEFAULT_PROTOCOL_ID);
        nettyClient.send(request);
    }
}
