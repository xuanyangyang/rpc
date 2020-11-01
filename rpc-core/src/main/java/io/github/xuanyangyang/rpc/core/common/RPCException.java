package io.github.xuanyangyang.rpc.core.common;

/**
 * 协议异常
 *
 * @author xuanyangyang
 * @since 2020/10/4 17:41
 */
public class RPCException extends RuntimeException {
    public RPCException(String message) {
        super(message);
    }

    public RPCException(Throwable cause) {
        super(cause);
    }

    public RPCException(String message, Throwable cause) {
        super(message, cause);
    }
}
