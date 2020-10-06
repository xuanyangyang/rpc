package io.github.xuanyangyang.rpc.core.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * protostuff codec
 *
 * @author xuanyangyang
 * @since 2020/10/6 14:46
 */
public class ProtostuffCodec implements Codec {
    /**
     * id
     */
    private final Short id;

    private static final ThreadLocal<LinkedBuffer> LINKED_BUFFER_LOCAL = ThreadLocal.withInitial(LinkedBuffer::allocate);

    public ProtostuffCodec(Short id) {
        this.id = id;
    }

    @Override
    public Short getId() {
        return id;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Object decode(ByteBuf byteBuf) throws ClassNotFoundException, IOException {
        int classNameLength = byteBuf.readInt();
        byte[] classNameBytes = new byte[classNameLength];
        byteBuf.readBytes(classNameBytes);
        String className = new String(classNameBytes, StandardCharsets.UTF_8);

        LinkedBuffer linkedBuffer = LINKED_BUFFER_LOCAL.get();
        Object obj;
        try {
            Class<?> clz = Class.forName(className);
            Schema schema = RuntimeSchema.getSchema(clz);
            obj = schema.newMessage();
            ProtostuffIOUtil.mergeFrom(new ByteBufInputStream(byteBuf), obj, schema, linkedBuffer);
        } finally {
            linkedBuffer.clear();
        }
        return obj;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void encode(ByteBuf byteBuf, Object obj) throws IOException {
        Class<?> objClass = obj.getClass();
        String className = objClass.getName();
        byte[] classNameBytes = className.getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(classNameBytes.length);
        byteBuf.writeBytes(classNameBytes);

        Schema schema = RuntimeSchema.getSchema(objClass);
        LinkedBuffer linkedBuffer = LINKED_BUFFER_LOCAL.get();
        try {
            ProtostuffIOUtil.writeTo(new ByteBufOutputStream(byteBuf), obj, schema, linkedBuffer);
        } finally {
            linkedBuffer.clear();
        }
    }
}
