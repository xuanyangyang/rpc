package io.github.xuanyangyang.rpc.core.codec;

import io.netty.buffer.ByteBuf;

/**
 * 编解码器
 *
 * @author xuanyangyang
 * @since 2020/10/4 18:15
 */
public interface Codec {
    Short getId();

    Object decode(ByteBuf byteBuf);

    void encode(ByteBuf byteBuf, Object msg);
}
