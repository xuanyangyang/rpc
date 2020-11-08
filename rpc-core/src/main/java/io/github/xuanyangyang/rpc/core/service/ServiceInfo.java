package io.github.xuanyangyang.rpc.core.service;

import java.util.Objects;

/**
 * 服务信息
 *
 * @author xuanyangyang
 * @since 2020/10/6 17:22
 */
public class ServiceInfo {
    /**
     * 服务ID
     */
    private String id;
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
     * 编解码器ID
     */
    private Short codecId;
    /**
     * ip
     */
    private String ip;
    /**
     * 端口
     */
    private int port;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Short getCodecId() {
        return codecId;
    }

    public void setCodecId(Short codecId) {
        this.codecId = codecId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceInfo that = (ServiceInfo) o;
        return id.equals(that.id) &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
