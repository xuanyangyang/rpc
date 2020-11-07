package io.github.xuanyangyang.rpc.core.registry.support.zookeeper;

/**
 * zookeeper 配置
 *
 * @author xuanyangyang
 * @since 2020/11/6 13:30
 */
public class ZookeeperConfig {
    /**
     * 地址
     */
    private String address;
    /**
     * 根路径
     */
    private String rootPath;

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
