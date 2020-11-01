package io.github.xuanyangyang.rpc.core.protocol.support;

import io.github.xuanyangyang.rpc.core.protocol.ProtocolMessage;

/**
 * 响应
 *
 * @author xuanyangyang
 * @since 2020/10/5 16:13
 */
public class Response implements ProtocolMessage {
    /**
     * id
     */
    private final long id;
    /**
     * 状态
     */
    private byte state;
    /**
     * 数据
     */
    private Object data;
    /**
     * 错误信息
     */
    private String errMsg;
    /**
     * 协议ID
     */
    private short protocolId;
    /**
     * ok状态
     */
    public static final byte STATE_OK = 0;
    /**
     * 客户端错误
     */
    public static final byte STATE_CLIENT_ERROR = 1;
    /**
     * 服务端错误
     */
    public static final byte STATE_SERVER_ERROR = 2;

    public Response(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public Short getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(short protocolId) {
        this.protocolId = protocolId;
    }
}
