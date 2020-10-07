package io.github.xuanyangyang.rpc.core.protocol.support;

/**
 * 响应
 *
 * @author xuanyangyang
 * @since 2020/10/5 16:13
 */
public class Response {
    /**
     * id
     */
    private final Long id;
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
     * ok状态
     */
    public static final byte STATE_OK = 0;
    /**
     * 客户端错误
     */
    public static final byte STATE_CLIENT_ERROR = 1;

    public Response(Long id) {
        this.id = id;
    }

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
}
