package io.github.xuanyangyang.rpc.core.protocol;

import io.netty.buffer.ByteBuf;

/**
 * 协议
 *
 * @author xuanyangyang
 * @since 2020/10/4 17:12
 */
public interface Protocol {
    /**
     * @return 协议ID
     */
    Short getId();

    /**
     * 解码
     *
     * @param buffer 缓冲
     * @return 解码结果
     */
    Object decode(ByteBuf buffer);

    /**
     * 编码
     *
     * @param buffer  缓冲
     * @param message 消息
     */
    void encode(ByteBuf buffer, Object message);

    /**
     * 解码结果
     */
    enum DecodeResult {
        /**
         * 需要更多的输入
         */
        NEED_MORE_INPUT
    }
}
