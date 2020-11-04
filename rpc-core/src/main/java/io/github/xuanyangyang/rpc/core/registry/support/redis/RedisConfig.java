package io.github.xuanyangyang.rpc.core.registry.support.redis;

/**
 * redis配置
 *
 * @author xuanyangyang
 * @since 2020/11/4 13:00
 */
public class RedisConfig {
    /**
     * 简单的地址，当configPath与address都配置的时候，生效的是configPath
     */
    private String address = "redis://127.0.0.1:6379";
    /**
     * redis配置文件路径，当configPath与address都配置的时候，生效的是configPath
     */
    private String configPath = "";

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
