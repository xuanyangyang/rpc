package io.github.xuanyangyang.rpc.core.protocol;

/**
 * 协议消息
 *
 * @author xuanyangyang
 * @since 2020/10/31 15:58
 */
public interface ProtocolMessage {
    /**
     * @return 消息ID
     */
    Long getId();

    /**
     * @return 协议ID
     */
    Short getProtocolId();

    /**
     * @return 编解码器ID
     */
    Short getCodecId();
}
