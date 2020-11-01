package io.github.xuanyangyang.rpc.spring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * rpc 配置
 *
 * @author xuanyangyang
 * @since 2020/11/2 00:16
 */
@ConfigurationProperties(prefix = "xyy.rpc")
public class RPCConfig {
    /**
     * 绑定端口
     */
    private int port = 10000;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
