package io.github.xuanyangyang.rpc.core.protocol.support;

import io.github.xuanyangyang.rpc.core.protocol.ProtocolMessage;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 请求
 *
 * @author xuanyangyang
 * @since 2020/10/5 16:08
 */
public class Request implements ProtocolMessage {
    /**
     * id
     */
    private final Long id;
    /**
     * id 创建
     */
    private static final AtomicLong ID_CREATE = new AtomicLong();
    /**
     * rpc调用信息
     */
    private RPCInvocationInfo invocationInfo;
    /**
     * 协议ID
     */
    private short protocolId;

    public Request(Long id) {
        this.id = id;
    }

    public Request() {
        this(newId());
    }

    private static Long newId() {
        return ID_CREATE.getAndIncrement();
    }

    @Override
    public Long getId() {
        return id;
    }

    public RPCInvocationInfo getInvocationInfo() {
        return invocationInfo;
    }

    public void setInvocationInfo(RPCInvocationInfo invocationInfo) {
        this.invocationInfo = invocationInfo;
    }

    @Override
    public Short getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(short protocolId) {
        this.protocolId = protocolId;
    }
}
