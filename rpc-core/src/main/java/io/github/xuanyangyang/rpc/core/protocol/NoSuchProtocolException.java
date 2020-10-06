package io.github.xuanyangyang.rpc.core.protocol;

import io.github.xuanyangyang.rpc.core.common.RpcException;

/**
 * 找不到协议异常
 *
 * @author xuanyangyang
 * @since 2020/10/4 17:20
 */
public class NoSuchProtocolException extends RpcException {
    /**
     * 协议ID
     */
    private final short protocolId;

    public NoSuchProtocolException(short protocolId) {
        super("找不到ID为" + protocolId + "的协议");
        this.protocolId = protocolId;
    }

    public short getProtocolId() {
        return protocolId;
    }
}
