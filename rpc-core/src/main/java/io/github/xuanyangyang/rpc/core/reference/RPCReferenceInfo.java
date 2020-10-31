package io.github.xuanyangyang.rpc.core.reference;

/**
 * rpc引用信息
 *
 * @author xuanyangyang
 * @since 2020/10/7 16:48
 */
public class RPCReferenceInfo {
    /**
     * 服务名
     */
    private String name;
    /**
     * 协议id
     */
    private Short protocolId;
    /**
     * 服务版本
     */
    private int version;
    /**
     * 引用的class
     */
    private Class<?> clz;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Short getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(Short protocolId) {
        this.protocolId = protocolId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Class<?> getClz() {
        return clz;
    }

    public void setClz(Class<?> clz) {
        this.clz = clz;
    }
}
