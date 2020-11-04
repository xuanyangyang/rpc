package io.github.xuanyangyang.rpc.core.net.netty;

import io.github.xuanyangyang.rpc.core.future.DefaultFuture;
import io.github.xuanyangyang.rpc.core.net.Channel;
import io.github.xuanyangyang.rpc.core.protocol.ProtocolMessage;
import io.github.xuanyangyang.rpc.core.protocol.support.Response;
import io.netty.channel.ChannelFutureListener;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * netty channel
 *
 * @author xuanyangyang
 * @since 2020/11/1 21:15
 */
public class NettyChannel implements Channel {
    /**
     * netty channel
     */
    private final io.netty.channel.Channel channel;
    /**
     * 属性map
     */
    private final Map<String, Object> attributeMap = new ConcurrentHashMap<>();

    public NettyChannel(io.netty.channel.Channel channel) {
        this.channel = channel;
    }

    @Override
    public <T> CompletableFuture<T> send(ProtocolMessage message) {
        return send(message, 0, TimeUnit.SECONDS);
    }

    @Override
    public <T> CompletableFuture<T> send(ProtocolMessage message, long timeout, TimeUnit timeUnit) {
        DefaultFuture<T> futureResult = DefaultFuture.newFuture(message.getId(), timeout, timeUnit);
        if (!isConnected()) {
            Response response = new Response(message.getId());
            response.setState(Response.STATE_CLIENT_ERROR);
            response.setData("通道已关闭");
            return futureResult;
        }
        channel.writeAndFlush(message).addListener((ChannelFutureListener) future -> {
            if (future.cause() != null) {
                futureResult.completeExceptionally(future.cause());
            }
        });
        return futureResult;
    }

    @Override
    public void close() {
        channel.close();
    }

    @Override
    public String getIp() {
        return ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
    }

    @Override
    public int getPort() {
        return ((InetSocketAddress) channel.remoteAddress()).getPort();
    }

    @Override
    public boolean isConnected() {
        return channel.isActive();
    }

    @Override
    public boolean hasAttribute(String key) {
        return attributeMap.containsKey(key);
    }

    @Override
    public Object getAttribute(String key) {
        return attributeMap.get(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributeMap.put(key, value);
    }

    @Override
    public void removeAttribute(String key) {
        attributeMap.remove(key);
    }
}
