package io.github.xuanyangyang.rpc.spring;

import io.github.xuanyangyang.rpc.core.RPCContext;
import io.github.xuanyangyang.rpc.core.client.filter.RemoteServiceClientFilter;
import io.github.xuanyangyang.rpc.core.client.filter.RemoteServiceClientFilterChainFactory;
import io.github.xuanyangyang.rpc.core.codec.Codec;
import io.github.xuanyangyang.rpc.core.codec.CodecManager;
import io.github.xuanyangyang.rpc.core.protocol.Protocol;
import io.github.xuanyangyang.rpc.core.protocol.ProtocolManager;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;

import java.util.Map;

/**
 * rpc 引导
 *
 * @author xuanyangyang
 * @since 2020/11/2 00:33
 */
public class RPCBoostrap implements DisposableBean {
    private final RPCContext rpcContext;
    private final CodecManager codecManager;
    private final ProtocolManager protocolManager;
    private final RemoteServiceClientFilterChainFactory filterChainFactory;

    public RPCBoostrap(RPCContext rpcContext, CodecManager codecManager, ProtocolManager protocolManager, RemoteServiceClientFilterChainFactory filterChainFactory) {
        this.rpcContext = rpcContext;
        this.codecManager = codecManager;
        this.protocolManager = protocolManager;
        this.filterChainFactory = filterChainFactory;
    }

    @EventListener
    public void start(ApplicationStartedEvent event) {
        ConfigurableApplicationContext applicationContext = event.getApplicationContext();
        registryCodec(applicationContext);
        registryProtocol(applicationContext);
        registryFilter(applicationContext);
        rpcContext.start();
    }

    private void registryFilter(ConfigurableApplicationContext context) {
        Map<String, RemoteServiceClientFilter> filterMap = context.getBeansOfType(RemoteServiceClientFilter.class);
        filterChainFactory.addFilters(filterMap.values());
    }

    private void registryCodec(ConfigurableApplicationContext context) {
        Map<String, Codec> codecMap = context.getBeansOfType(Codec.class);
        for (Codec codec : codecMap.values()) {
            codecManager.addCodec(codec);
        }
    }

    private void registryProtocol(ConfigurableApplicationContext context) {
        Map<String, Protocol> protocolMap = context.getBeansOfType(Protocol.class);
        for (Protocol protocol : protocolMap.values()) {
            protocolManager.addProtocol(protocol);
        }
    }

    @Override
    public void destroy() throws Exception {
        rpcContext.stop();
    }
}
