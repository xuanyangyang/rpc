package io.github.xuanyangyang.rpc.core.net;

/**
 * 客户端
 *
 * @author xuanyangyang
 * @since 2020/10/7 14:16
 */
public interface Client extends Channel {
    /**
     * 断开连接
     */
    void disconnect();

    /**
     * 连接
     */
    void connect();

    /**
     * 重连
     */
    void reconnect();

    /**
     * @return id
     */
    default String getId() {
        return createId(getIp(), getPort());
    }

    static String createId(String ip, int port) {
        return ip + ":" + port;
    }
}
