package io.github.xuanyangyang.rpc.core.reference;

import java.util.concurrent.TimeUnit;

/**
 * rpc引用信息
 *
 * @author xuanyangyang
 * @since 2020/10/7 16:48
 */
public class DefaultRPCReferenceInfo implements RPCReferenceInfo {
    /**
     * 服务名
     */
    private String name;
    /**
     * 协议id
     */
    private Short protocolId;
    /**
     * 服务版本
     */
    private int version;
    /**
     * 引用的接口class
     */
    private Class<?> clz;
    /**
     * 超时时间
     */
    private long timeout;
    /**
     * 超时时间单位
     */
    private TimeUnit timeoutTimeUnit;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Short getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(Short protocolId) {
        this.protocolId = protocolId;
    }

    @Override
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public Class<?> getClz() {
        return clz;
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public TimeUnit getTimeoutTimeUnit() {
        return timeoutTimeUnit;
    }

    public void setClz(Class<?> clz) {
        this.clz = clz;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setTimeoutTimeUnit(TimeUnit timeoutTimeUnit) {
        this.timeoutTimeUnit = timeoutTimeUnit;
    }
}
