package io.github.xuanyangyang.rpc.core.net;

import io.github.xuanyangyang.rpc.core.common.RPCException;
import io.github.xuanyangyang.rpc.core.protocol.ProtocolManager;
import io.github.xuanyangyang.rpc.core.service.ServiceInstanceManager;
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
    private final ServiceInstanceManager serviceInstanceManager;
    private final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    public NettyServer(ProtocolManager protocolManager, ServiceInstanceManager serviceInstanceManager) {
        this.protocolManager = protocolManager;
        this.serviceInstanceManager = serviceInstanceManager;
    }

    public void bind(int port) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        DispatcherHandler dispatcherHandler = new DispatcherHandler(serviceInstanceManager);
        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new ProtocolDecoder(protocolManager))
                                .addLast(new ProtocolEncoder(protocolManager))
                                .addLast(dispatcherHandler);
                    }
                });
        try {
            serverBootstrap.bind(port).sync();
        } catch (InterruptedException e) {
            logger.warn("等待绑定端口被打断");
            throw new RPCException(e);
        }
        logger.info("服务启动成功，端口：{}", port);
    }
}
