package io.github.xuanyangyang.rpc.core.net;

/**
 * 服务端
 *
 * @author xuanyangyang
 * @since 2020/11/1 21:51
 */
public interface Server {
    /**
     * 绑定
     *
     * @param port 端口
     */
    void bind(int port);

    /**
     * 停服
     */
    void shutdown();
}
