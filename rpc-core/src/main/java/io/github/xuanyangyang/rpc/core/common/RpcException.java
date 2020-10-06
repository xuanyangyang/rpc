package io.github.xuanyangyang.rpc.core.common;

/**
 * 协议异常
 *
 * @author xuanyangyang
 * @since 2020/10/4 17:41
 */
public class RpcException extends RuntimeException {
    public RpcException(String message) {
        super(message);
    }
}
