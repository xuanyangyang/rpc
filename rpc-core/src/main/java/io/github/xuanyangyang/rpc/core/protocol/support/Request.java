package io.github.xuanyangyang.rpc.core.protocol.support;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 请求
 *
 * @author xuanyangyang
 * @since 2020/10/5 16:08
 */
public class Request {
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
    private RpcInvocationInfo invocationInfo;

    public Request(Long id) {
        this.id = id;
    }

    public Request() {
        this(newId());
    }

    private static Long newId() {
        return ID_CREATE.getAndIncrement();
    }

    public Long getId() {
        return id;
    }

    public RpcInvocationInfo getInvocationInfo() {
        return invocationInfo;
    }

    public void setInvocationInfo(RpcInvocationInfo invocationInfo) {
        this.invocationInfo = invocationInfo;
    }
}
