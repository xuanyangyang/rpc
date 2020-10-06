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

/**
 * netty客户端
 *
 * @author xuanyangyang
 * @since 2020/10/6 15:09
 */
public class NettyClient {
    private final ProtocolManager protocolManager;

    private final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private Channel channel;

    public NettyClient(ProtocolManager protocolManager) {
        this.protocolManager = protocolManager;
    }

    public void connect(String ip, int port) {
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

    public void send(Object message) {
        channel.writeAndFlush(message);
    }
}
