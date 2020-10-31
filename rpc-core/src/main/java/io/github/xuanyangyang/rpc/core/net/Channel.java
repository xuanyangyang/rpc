package io.github.xuanyangyang.rpc.core.net;

import io.github.xuanyangyang.rpc.core.protocol.ProtocolMessage;

import java.util.concurrent.CompletableFuture;

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
     */
    <T> CompletableFuture<T> send(ProtocolMessage message);

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
