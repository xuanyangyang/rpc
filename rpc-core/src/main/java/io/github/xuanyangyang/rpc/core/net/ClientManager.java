package io.github.xuanyangyang.rpc.core.net;

/**
 * 客户端管理
 *
 * @author xuanyangyang
 * @since 2020/11/3 13:00
 */
public interface ClientManager {
    /**
     * 通过ID获取客户端
     *
     * @param id 客户端id
     * @return 客户端
     */
    Client getClient(String id);

    /**
     * 通过ip port获取客户端
     *
     * @param ip   ip
     * @param port port
     * @return 客户端
     */
    default Client getClient(String ip, int port) {
        return getClient(Client.createId(ip, port));
    }

    /**
     * 添加客户端
     *
     * @param client 客户端
     */
    void addClient(Client client);

    /**
     * 通过ID移除客户端
     *
     * @param id 客户端id
     */
    void removeClient(String id);

    /**
     * 初始化
     */
    void init();

    /**
     * 销毁
     */
    void destroy();

    /**
     * 获取不到就创建客户端
     *
     * @param ip   ip
     * @param port 端口
     * @return 客户端
     */
    Client getOrCreateClient(String ip, int port);
}
