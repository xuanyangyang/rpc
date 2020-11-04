package io.github.xuanyangyang.rpc.core.net;

import io.github.xuanyangyang.rpc.core.protocol.ProtocolMessage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 通讯通道
 *
 * @author xuanyangyang
 * @since 2020/10/7 13:55
 */
public interface Channel {
    /**
     * 发送请求
     *
     * @param message 请求
     * @param <T>     结果类型
     * @return 结果future
     */
    <T> CompletableFuture<T> send(ProtocolMessage message);

    /**
     * 发送请求
     *
     * @param message  请求
     * @param timeout  超时时间
     * @param timeUnit 时间单位
     * @param <T>      结果类型
     * @return 结果future
     */
    <T> CompletableFuture<T> send(ProtocolMessage message, long timeout, TimeUnit timeUnit);

    /**
     * 关闭
     */
    void close();

    /**
     * @return IP
     */
    String getIp();

    /**
     * @return 端口
     */
    int getPort();

    /**
     * @return 是否连接
     */
    boolean isConnected();

    /**
     * @param key key
     * @return 是否有key对应的属性
     */
    boolean hasAttribute(String key);

    /**
     * 获取key对应的属性
     *
     * @param key key
     * @return key对应的属性
     */
    Object getAttribute(String key);

    /**
     * 设置key对应的属性
     *
     * @param key   key
     * @param value value
     */
    void setAttribute(String key, Object value);

    /**
     * 移除key对应的属性
     *
     * @param key key.
     */
    void removeAttribute(String key);
}
