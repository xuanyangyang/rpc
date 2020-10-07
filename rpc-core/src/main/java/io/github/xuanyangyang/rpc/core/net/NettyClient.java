package io.github.xuanyangyang.rpc.core.net;

import io.github.xuanyangyang.rpc.core.common.RpcException;
import io.github.xuanyangyang.rpc.core.protocol.ProtocolManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    private final Map<String, Object> attributeMap = new ConcurrentHashMap<>();
    private final String ip;
    private final int port;

    public NettyClient(String ip, int port, ProtocolManager protocolManager) {
        this.ip = ip;
        this.port = port;
        this.protocolManager = protocolManager;
    }


    public void send(Object message) {
        if (!isConnected()) {
            connect();
        }
        channel.writeAndFlush(message);
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
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new ProtocolDecoder(protocolManager))
                                .addLast(new ProtocolEncoder(protocolManager))
                                .addLast(new EchoHandler());
                    }
                });
        try {
            channel = bootstrap.connect(ip, port).sync().channel();
        } catch (InterruptedException e) {
            logger.warn("等待连接{}:{}被打断", ip, port);
            throw new RpcException(e);
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
