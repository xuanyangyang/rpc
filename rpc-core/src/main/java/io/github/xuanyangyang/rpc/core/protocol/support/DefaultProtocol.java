package io.github.xuanyangyang.rpc.core.protocol.support;

import io.github.xuanyangyang.rpc.core.codec.Codec;
import io.github.xuanyangyang.rpc.core.codec.CodecConstants;
import io.github.xuanyangyang.rpc.core.codec.CodecManager;
import io.github.xuanyangyang.rpc.core.codec.NoSuchCodecException;
import io.github.xuanyangyang.rpc.core.common.RpcException;
import io.github.xuanyangyang.rpc.core.net.NetConstants;
import io.github.xuanyangyang.rpc.core.protocol.Protocol;
import io.netty.buffer.ByteBuf;

import java.util.Arrays;

/**
 * 默认协议
 *
 * @author xuanyangyang
 * @since 2020/10/4 17:32
 */
public class DefaultProtocol implements Protocol {
    /**
     * 魔术字
     */
    private static final byte[] MAGIC = new byte[]{10, 02};
    /**
     * 魔术字长度
     */
    private static final int MAGIC_LENGTH = 2;
    /**
     * 协议头长度
     */
    private static final short HEADER_LENGTH = 2;
    /**
     * 协议体长度
     */
    private static final int BODY_LENGTH = 4;
    /**
     * 协议版本
     */
    private static final int protocolVersion = 1;
    /**
     * codec管理
     */
    private final CodecManager codecManager;
    /**
     * 当前codecID
     */
    private Short codecId = CodecConstants.DEFAULT_CODEC_ID;
    /**
     * 请求类型
     */
    private static final byte TYPE_REQUEST = 1;
    /**
     * 响应类型
     */
    private static final byte TYPE_RESPONSE = 2;

    public DefaultProtocol(CodecManager codecManager) {
        this.codecManager = codecManager;
    }

    @Override
    public Short getId() {
        return NetConstants.DEFAULT_PROTOCOL_ID;
    }

    @Override
    public Object decode(ByteBuf buffer) throws Exception {
        if (buffer.readableBytes() < MAGIC_LENGTH) {
            return DecodeResult.NEED_MORE_INPUT;
        }
        byte[] actualMagic = new byte[MAGIC_LENGTH];
        buffer.readBytes(actualMagic);
        if (MAGIC[0] != actualMagic[0] || MAGIC[1] != actualMagic[1]) {
            throw new RpcException("魔术字对不上！！！期待的魔术字：" + Arrays.toString(MAGIC) + "，实际收到的魔术字：" + Arrays.toString(actualMagic));
        }
        if (buffer.readableBytes() < BODY_LENGTH) {
            return DecodeResult.NEED_MORE_INPUT;
        }
        // 协议剩余长度
        int length = buffer.readInt();
        if (buffer.readableBytes() < length) {
            return DecodeResult.NEED_MORE_INPUT;
        }
        // 只传递当前需要的buffer
        ByteBuf curByteBuf = buffer.readSlice(length);
        short headerLength = curByteBuf.readShort();
        Header header = decodeHeader(curByteBuf.readSlice(headerLength));
        return decodeBody(curByteBuf, header);
    }

    private Object decodeBody(ByteBuf buffer, Header header) throws Exception {
        byte type = header.getType();
        Object msg;
        if (TYPE_REQUEST == type) {
            msg = decodeRequest(buffer, header);
        } else {
            msg = decodeResponse(buffer, header);
        }
        return msg;
    }

    private Response decodeResponse(ByteBuf buffer, Header header) throws Exception {
        Response response = new Response(header.getMsgId());
        response.setState(buffer.readByte());
        Codec codec = codecManager.getCodec(header.getCodecId());
        Object data = codec.decode(buffer);
        response.setData(data);
        return response;
    }

    private Request decodeRequest(ByteBuf buffer, Header header) throws Exception {
        Request request = new Request(header.getMsgId());
        RpcInvocationInfo invocationInfo = (RpcInvocationInfo) decodeObj(buffer, header.getCodecId());
        request.setInvocationInfo(invocationInfo);
        return request;
    }

    private void encodeHeader(ByteBuf buffer, long id, byte type) {
        int headerWriterIndex = buffer.writerIndex();
        buffer.writerIndex(buffer.writerIndex() + HEADER_LENGTH);
        buffer.writeInt(protocolVersion);
        buffer.writeShort(codecId);
        buffer.writeByte(type);
        buffer.writeLong(id);
        int headerLength = buffer.writerIndex() - headerWriterIndex - HEADER_LENGTH;
        buffer.setShort(headerWriterIndex, headerLength);
    }

    private Header decodeHeader(ByteBuf buffer) {
        int protocolVersion = buffer.readInt();
        if (DefaultProtocol.protocolVersion < protocolVersion) {
            throw new RpcException("当前协议版本过低，不能解析该信息。当前协议版本:" + DefaultProtocol.protocolVersion + ",收到的信息协议版本:" + protocolVersion);
        }
        short codecId = buffer.readShort();
        Codec codec = codecManager.getCodec(codecId);
        if (codec == null) {
            throw new NoSuchCodecException(codecId);
        }
        byte type = buffer.readByte();
        long id = buffer.readLong();
        if (TYPE_REQUEST != type && TYPE_RESPONSE != type) {
            throw new RpcException("无法解析的协议类型:" + type);
        }
        Header header = new Header();
        header.setProtocolVersion(protocolVersion);
        header.setCodecId(codecId);
        header.setType(type);
        header.setMsgId(id);
        return header;
    }

    @Override
    public void encode(ByteBuf buffer, Object message) throws Exception {
        buffer.writeBytes(MAGIC);
        int bodyWriterIndex = buffer.writerIndex();
        buffer.writerIndex(bodyWriterIndex + BODY_LENGTH);
        if (message instanceof Request) {
            Request request = (Request) message;
            encodeHeader(buffer, request.getId(), TYPE_REQUEST);
            encodeRequest(buffer, request);
        } else if (message instanceof Response) {
            Response response = (Response) message;
            encodeHeader(buffer, response.getId(), TYPE_RESPONSE);
            encodeResponse(buffer, response);
        } else {
            throw new RpcException("目前不支持除了Request，Response之外的消息类型，收到的消息类型：" + message.getClass());
        }
        int length = buffer.writerIndex() - bodyWriterIndex - BODY_LENGTH;
        buffer.setInt(bodyWriterIndex, length);
    }

    private void encodeResponse(ByteBuf buffer, Response response) throws Exception {
        buffer.writeByte(response.getState());
        encodeObj(buffer, response.getData());
    }

    public void encodeRequest(ByteBuf buffer, Request request) throws Exception {
        encodeObj(buffer, request.getInvocationInfo());
    }

    private void encodeObj(ByteBuf buffer, Object obj) throws Exception {
        Codec codec = codecManager.getCodec(codecId);
        if (codec == null) {
            throw new NoSuchCodecException(codecId);
        }
        codec.encode(buffer, obj);
    }

    private Object decodeObj(ByteBuf buffer, Short codecId) throws Exception {
        Codec codec = codecManager.getCodec(codecId);
        if (codec == null) {
            throw new NoSuchCodecException(codecId);
        }
        return codec.decode(buffer);
    }

    static class Header {
        /**
         * 协议版本
         */
        private int protocolVersion;
        /**
         * codec id
         */
        private Short codecId;
        /**
         * 消息ID
         */
        private Long msgId;
        /**
         * 类型
         */
        private byte type;

        public int getProtocolVersion() {
            return protocolVersion;
        }

        public void setProtocolVersion(int protocolVersion) {
            this.protocolVersion = protocolVersion;
        }

        public Short getCodecId() {
            return codecId;
        }

        public void setCodecId(Short codecId) {
            this.codecId = codecId;
        }

        public Long getMsgId() {
            return msgId;
        }

        public void setMsgId(Long msgId) {
            this.msgId = msgId;
        }

        public byte getType() {
            return type;
        }

        public void setType(byte type) {
            this.type = type;
        }
    }
}
