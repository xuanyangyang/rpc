package io.github.xuanyangyang.rpc.core.protocol;

/**
 * 协议消息包装
 *
 * @author xuanyangyang
 * @since 2020/10/4 17:18
 */
public interface ProtocolMessageWrapper {
    /**
     * @return 协议ID
     */
    Short getProtocolId();

    /**
     * @return 消息
     */
    Object getMessage();
}
