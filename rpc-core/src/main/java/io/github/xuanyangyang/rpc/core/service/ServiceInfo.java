package io.github.xuanyangyang.rpc.core.service;

/**
 * 服务信息
 *
 * @author xuanyangyang
 * @since 2020/10/6 17:22
 */
public class ServiceInfo {
    /**
     * 服务名
     */
    private String name;
    /**
     * 服务版本
     */
    private int version;
    /**
     * 协议ID
     */
    private Short protocolId;
    /**
     * ip
     */
    private String ip;
    /**
     * 端口
     */
    private int port;
    /**
     * 服务key
     */
    private String serviceKey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Short getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(Short protocolId) {
        this.protocolId = protocolId;
    }

    public String getServiceKey() {
        return serviceKey;
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }
}
