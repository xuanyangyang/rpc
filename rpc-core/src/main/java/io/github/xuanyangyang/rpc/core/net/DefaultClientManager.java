package io.github.xuanyangyang.rpc.core.net;

import io.github.xuanyangyang.rpc.core.net.dispatcher.MessageDispatcher;
import io.github.xuanyangyang.rpc.core.net.netty.NettyClient;
import io.github.xuanyangyang.rpc.core.protocol.ProtocolManager;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 客户端管理
 *
 * @author xuanyangyang
 * @since 2020/10/7 14:52
 */
public class DefaultClientManager implements ClientManager {
    private final Map<String, Client> clientMap = new ConcurrentHashMap<>();
    private final ProtocolManager protocolManager;
    private final ScheduledExecutorService scheduledExecutorService;
    private final MessageDispatcher messageDispatcher;

    public DefaultClientManager(ProtocolManager protocolManager, MessageDispatcher messageDispatcher) {
        this(protocolManager, messageDispatcher, Executors.newSingleThreadScheduledExecutor(new DefaultThreadFactory("客户端检查线程")));
    }

    public DefaultClientManager(ProtocolManager protocolManager, MessageDispatcher messageDispatcher, ScheduledExecutorService scheduledExecutorService) {
        this.protocolManager = protocolManager;
        this.scheduledExecutorService = scheduledExecutorService;
        this.messageDispatcher = messageDispatcher;
    }

    public Client getClient(String id) {
        return clientMap.get(id);
    }

    public void addClient(Client client) {
        Client oldClient = clientMap.put(client.getId(), client);
        if (oldClient != null) {
            oldClient.close();
        }
    }

    public void removeClient(String id) {
        Client client = clientMap.remove(id);
        if (client != null) {
            client.close();
        }
    }

    public void init() {
        scheduledExecutorService.scheduleAtFixedRate(this::checkClient, 0, 10, TimeUnit.SECONDS);
    }

    public void destroy() {
        scheduledExecutorService.shutdown();
    }

    private void checkClient() {
        for (Client client : clientMap.values()) {
            if (client.isConnected()) {
                continue;
            }
            client.reconnect();
        }
    }

    public Client getOrCreateClient(String ip, int port) {
        return clientMap.computeIfAbsent(Client.createId(ip, port), key -> new NettyClient(ip, port, protocolManager, messageDispatcher));
    }
}
