package io.github.xuanyangyang.rpc.core.net;

import io.github.xuanyangyang.rpc.core.protocol.NoSuchProtocolException;
import io.github.xuanyangyang.rpc.core.protocol.Protocol;
import io.github.xuanyangyang.rpc.core.protocol.ProtocolManager;
import io.github.xuanyangyang.rpc.core.protocol.ProtocolMessageWrapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 协议编码
 *
 * @author xuanyangyang
 * @since 2020/10/4 17:15
 */
public class ProtocolEncoder extends MessageToByteEncoder<ProtocolMessageWrapper> {
    private final ProtocolManager protocolManager;

    public ProtocolEncoder(ProtocolManager protocolManager) {
        this.protocolManager = protocolManager;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ProtocolMessageWrapper protocolMessageWrapper, ByteBuf out) throws Exception {
        Protocol protocol = protocolManager.getProtocol(protocolMessageWrapper.getProtocolId());
        if (protocol == null) {
            throw new NoSuchProtocolException(protocolMessageWrapper.getProtocolId());
        }
        out.writeShort(protocolMessageWrapper.getProtocolId());
        protocol.encode(out, protocolMessageWrapper.getMessage());
    }
}
