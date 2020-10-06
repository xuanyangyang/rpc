package io.github.xuanyangyang.rpc.core.protocol.support;

import io.github.xuanyangyang.rpc.core.protocol.ProtocolMessageWrapper;

/**
 * 默认协议消息
 *
 * @author xuanyangyang
 * @since 2020/10/6 15:40
 */
public class DefaultProtocolMessageWrapper implements ProtocolMessageWrapper {
    /**
     * 协议Id
     */
    private final Short protocolId;
    /**
     * 消息
     */
    private final Object message;

    public DefaultProtocolMessageWrapper(Short protocolId, Object message) {
        this.protocolId = protocolId;
        this.message = message;
    }

    public static DefaultProtocolMessageWrapper createProtocolMessage(Short protocolId, Object message) {
        return new DefaultProtocolMessageWrapper(protocolId, message);
    }

    @Override
    public Short getProtocolId() {
        return protocolId;
    }

    @Override
    public Object getMessage() {
        return message;
    }
}
