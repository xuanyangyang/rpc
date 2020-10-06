package io.github.xuanyangyang.rpc.core.net;

import io.github.xuanyangyang.rpc.core.protocol.ProtocolManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * netty 服务端
 *
 * @author xuanyangyang
 * @since 2020/10/4 17:00
 */
public class NettyServer {
    private final ProtocolManager protocolManager;

    private final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    public NettyServer(ProtocolManager protocolManager) {
        this.protocolManager = protocolManager;
    }

    public void bind(int port) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new ProtocolDecoder(protocolManager))
                                .addLast(new ProtocolEncoder(protocolManager))
                                .addLast(new EchoHandler());
                    }
                });
        try {
            serverBootstrap.bind(port).sync();
        } catch (InterruptedException e) {
            logger.warn("等待绑定端口被打断", e);
        }
    }
}
