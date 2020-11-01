package io.github.xuanyangyang.rpc.core.net.netty;

import io.github.xuanyangyang.rpc.core.net.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 * netty 工具
 *
 * @author xuanyangyang
 * @since 2020/11/1 21:37
 */
public class NettyUtils {

    public static final AttributeKey<Channel> CHANNEL = AttributeKey.valueOf("myChannel");

    public static Channel getChannel(io.netty.channel.Channel nettyChannel) {
        Attribute<Channel> attr = nettyChannel.attr(CHANNEL);
        return attr.get();
    }

    public static void setChannel(io.netty.channel.Channel nettyChannel, Channel channel) {
        Attribute<Channel> attr = nettyChannel.attr(CHANNEL);
        attr.set(channel);
    }
}
