package io.github.xuanyangyang.rpc.core.net.netty;

import io.github.xuanyangyang.rpc.core.common.RPCException;
import io.github.xuanyangyang.rpc.core.net.*;
import io.github.xuanyangyang.rpc.core.net.dispatcher.MessageDispatcher;
import io.github.xuanyangyang.rpc.core.protocol.ProtocolManager;
import io.github.xuanyangyang.rpc.core.protocol.ProtocolMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * netty客户端
 *
 * @author xuanyangyang
 * @since 2020/10/6 15:09
 */
public class NettyClient implements Client {
    private final ProtocolManager protocolManager;
    private final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private Channel channel;
    private final String ip;
    private final int port;
    /**
     * 消息分发器
     */
    private final MessageDispatcher messageDispatcher;

    public NettyClient(String ip, int port, ProtocolManager protocolManager, MessageDispatcher messageDispatcher) {
        this.ip = ip;
        this.port = port;
        this.protocolManager = protocolManager;
        this.messageDispatcher = messageDispatcher;
    }


    @Override
    public <T> CompletableFuture<T> send(ProtocolMessage message) {
        return channel.send(message);
    }

    @Override
    public <T> CompletableFuture<T> send(ProtocolMessage message, long timeout, TimeUnit timeUnit) {
        return channel.send(message, timeout, timeUnit);
    }

    @Override
    public void close() {
        channel.close();
    }

    @Override
    public String getIp() {
        return ip;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public boolean isConnected() {
        return channel != null && channel.isConnected();
    }

    @Override
    public boolean hasAttribute(String key) {
        return channel.hasAttribute(key);
    }

    @Override
    public Object getAttribute(String key) {
        return channel.getAttribute(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        channel.setAttribute(key, value);
    }

    @Override
    public void removeAttribute(String key) {
        channel.removeAttribute(key);
    }

    @Override
    public synchronized void disconnect() {
        channel.close();
    }

    @Override
    public synchronized void connect() {
        if (isConnected()) {
            return;
        }
        connect(ip, port);
    }

    private synchronized void connect(String ip, int port) {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        DispatcherHandler dispatcherHandler = new DispatcherHandler(messageDispatcher);
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(io.netty.channel.Channel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new ProtocolDecoder(protocolManager))
                                .addLast(new ProtocolEncoder(protocolManager))
                                .addLast(dispatcherHandler);
                    }
                });
        try {
            io.netty.channel.Channel nettyChannel = bootstrap.connect(ip, port).sync().channel();
            while (channel == null) {
                // 等待获取channel
                channel = NettyUtils.getChannel(nettyChannel);
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            logger.warn("等待连接{}:{}被打断", ip, port);
            throw new RPCException(e);
        }
        logger.info("连接{}:{}成功", ip, port);
    }

    @Override
    public synchronized void reconnect() {
        if (isConnected()) {
            disconnect();
        }
        connect();
    }
}
