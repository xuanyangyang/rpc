package io.github.xuanyangyang.rpc.core.config;

import io.github.xuanyangyang.rpc.core.common.RPCConstants;

import java.util.concurrent.TimeUnit;

/**
 * rpc配置
 *
 * @author xuanyangyang
 * @since 2020/11/4 12:57
 */
public class RPCConfig {
    /**
     * 端口
     */
    private int port = RPCConstants.DEFAULT_PORT;
    /**
     * 超时时间
     */
    private long timeout;
    /**
     * 超时时间单位
     */
    private TimeUnit timeoutTimeUnit = TimeUnit.MILLISECONDS;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public TimeUnit getTimeoutTimeUnit() {
        return timeoutTimeUnit;
    }

    public void setTimeoutTimeUnit(TimeUnit timeoutTimeUnit) {
        this.timeoutTimeUnit = timeoutTimeUnit;
    }
}
