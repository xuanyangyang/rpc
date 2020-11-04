package io.github.xuanyangyang.rpc.core.codec;

import io.netty.buffer.ByteBuf;

/**
 * 编解码器
 *
 * @author xuanyangyang
 * @since 2020/10/4 18:15
 */
public interface Codec {
    /**
     * @return ID
     */
    Short getId();

    /**
     * 反序列化成对象
     *
     * @param byteBuf byteBuf
     * @return 对象
     * @throws Exception 解码异常
     */
    Object decode(ByteBuf byteBuf) throws Exception;

    /**
     * 序列化到byteBuf
     *
     * @param byteBuf byteBuf
     * @param obj     对象
     * @throws Exception 编译异常
     */
    void encode(ByteBuf byteBuf, Object obj) throws Exception;
}
