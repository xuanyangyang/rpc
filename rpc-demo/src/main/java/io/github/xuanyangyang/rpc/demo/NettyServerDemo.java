package io.github.xuanyangyang.rpc.demo;

import io.github.xuanyangyang.rpc.core.codec.CodecConstants;
import io.github.xuanyangyang.rpc.core.codec.DefaultCodecManager;
import io.github.xuanyangyang.rpc.core.codec.ProtostuffCodec;
import io.github.xuanyangyang.rpc.core.net.NettyServer;
import io.github.xuanyangyang.rpc.core.protocol.DefaultProtocolManager;
import io.github.xuanyangyang.rpc.core.protocol.support.DefaultProtocol;

/**
 * netty server demo
 *
 * @author xuanyangyang
 * @since 2020/10/6 15:22
 */
public class NettyServerDemo {
    public static void main(String[] args) {
        DefaultCodecManager codecManager = new DefaultCodecManager();
        codecManager.addCodec(new ProtostuffCodec(CodecConstants.DEFAULT_CODEC_ID));
        DefaultProtocolManager protocolManager = new DefaultProtocolManager();
        protocolManager.addProtocol(new DefaultProtocol(codecManager));
        NettyServer nettyServer = new NettyServer(protocolManager);
        nettyServer.bind(10000);
    }
}
