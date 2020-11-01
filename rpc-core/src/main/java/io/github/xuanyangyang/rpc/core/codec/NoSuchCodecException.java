package io.github.xuanyangyang.rpc.core.codec;

import io.github.xuanyangyang.rpc.core.common.RPCException;

/**
 * 找不到codec
 *
 * @author xuanyangyang
 * @since 2020/10/4 18:27
 */
public class NoSuchCodecException extends RPCException {
    /**
     * codecId
     */
    private final short codecId;

    public NoSuchCodecException(short codecId) {
        super("找不到ID为" + codecId + "的Codec");
        this.codecId = codecId;
    }

    public short getCodecId() {
        return codecId;
    }
}
