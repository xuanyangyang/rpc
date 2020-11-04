package io.github.xuanyangyang.rpc.core.net.dispatcher;

import io.github.xuanyangyang.rpc.core.net.Channel;

/**
 * 消息分发
 *
 * @author xuanyangyang
 * @since 2020/11/1 20:52
 */
public interface MessageDispatcher {
    /**
     * 消息分发
     *
     * @param channel 通道
     * @param message 消息
     */
    void dispatch(Channel channel, Object message);
}
