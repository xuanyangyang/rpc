package io.github.xuanyangyang.rpc.core.net;

import io.github.xuanyangyang.rpc.core.common.RPCConstants;
import io.github.xuanyangyang.rpc.core.protocol.NoSuchProtocolException;
import io.github.xuanyangyang.rpc.core.protocol.Protocol;
import io.github.xuanyangyang.rpc.core.protocol.ProtocolManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 协议解码
 *
 * @author xuanyangyang
 * @since 2020/10/4 17:22
 */
public class ProtocolDecoder extends ByteToMessageDecoder {
    private final ProtocolManager protocolManager;

    public ProtocolDecoder(ProtocolManager protocolManager) {
        this.protocolManager = protocolManager;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        do {
            if (in.readableBytes() < RPCConstants.ID_LENGTH) {
                break;
            }
            int readerIndex = in.readerIndex();
            short protocolId = in.readShort();
            Protocol protocol = protocolManager.getProtocol(protocolId);
            if (protocol == null) {
                throw new NoSuchProtocolException(protocolId);
            }
            Object msg = protocol.decode(in);
            if (msg == Protocol.DecodeResult.NEED_MORE_INPUT) {
                in.readerIndex(readerIndex);
                break;
            }
            out.add(msg);
        } while (in.isReadable());
    }
}
