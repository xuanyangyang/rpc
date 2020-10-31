package io.github.xuanyangyang.rpc.core.net;

import io.github.xuanyangyang.rpc.core.protocol.ProtocolManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端管理
 *
 * @author xuanyangyang
 * @since 2020/10/7 14:52
 */
public class ClientManager {
    private final Map<String, Client> clientMap = new ConcurrentHashMap<>();
    private final ProtocolManager protocolManager;

    public ClientManager(ProtocolManager protocolManager) {
        this.protocolManager = protocolManager;
    }

    public Client getClient(String id) {
        return clientMap.get(id);
    }

    public Client getClient(String ip, int port) {
        return getClient(Client.createId(ip, port));
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
        return clientMap.computeIfAbsent(Client.createId(ip, port), key -> new NettyClient(ip, port, protocolManager));
    }
}
