package io.github.xuanyangyang.rpc.spring.config;

import io.github.xuanyangyang.rpc.core.common.RPCConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

/**
 * rpc 配置
 *
 * @author xuanyangyang
 * @since 2020/11/2 00:16
 */
@ConfigurationProperties(prefix = "rpc")
public class SpringRPCProperties {
    /**
     * 绑定端口
     */
    private int port = RPCConstants.DEFAULT_PORT;
    /**
     * 超时时间
     */
    private long timeout = 0;
    /**
     * 超时时间单位
     */
    private TimeUnit timeoutTimeUnit = TimeUnit.MILLISECONDS;
    /**
     * 注册中心配置
     */
    private SpringRegistryProperties registry = new SpringRegistryProperties();

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

    public SpringRegistryProperties getRegistry() {
        return registry;
    }

    public void setRegistry(SpringRegistryProperties registry) {
        this.registry = registry;
    }

    /**
     * 注册中心配置
     */
    public static class SpringRegistryProperties {
        /**
         * redis配置
         */
        private SpringRedisProperties redis = new SpringRedisProperties();
        /**
         * zookeeper配置
         */
        private SpringZookeeperProperties zookeeper = new SpringZookeeperProperties();

        public SpringRedisProperties getRedis() {
            return redis;
        }

        public void setRedis(SpringRedisProperties redis) {
            this.redis = redis;
        }

        public SpringZookeeperProperties getZookeeper() {
            return zookeeper;
        }

        public void setZookeeper(SpringZookeeperProperties zookeeper) {
            this.zookeeper = zookeeper;
        }
    }

    /**
     * redis配置
     */
    public static class SpringRedisProperties {
        /**
         * 是否启用
         */
        public boolean enable = false;
        /**
         * 简单的地址，当configPath与address都配置的时候，生效的是configPath
         */
        private String address = "redis://127.0.0.1:6379";
        /**
         * redis配置文件路径，当configPath与address都配置的时候，生效的是configPath
         */
        private String configPath = "";

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getConfigPath() {
            return configPath;
        }

        public void setConfigPath(String configPath) {
            this.configPath = configPath;
        }
    }

    /**
     * zookeeper配置
     */
    public static class SpringZookeeperProperties {
        /**
         * 是否启用
         */
        public boolean enable = false;
        /**
         * 地址
         */
        private String address = "localhost:2181";
        /**
         * 根目录
         */
        private String rootPath = "/service";

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getRootPath() {
            return rootPath;
        }

        public void setRootPath(String rootPath) {
            this.rootPath = rootPath;
        }
    }
}
