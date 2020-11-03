package io.github.xuanyangyang.rpc.core;

import io.github.xuanyangyang.rpc.core.registry.Registry;
import io.github.xuanyangyang.rpc.core.service.RemoteServiceClientManager;
import io.github.xuanyangyang.rpc.core.service.ServiceInstanceManager;

/**
 * rpc上下文
 *
 * @author xuanyangyang
 * @since 2020/11/3 15:43
 */
public interface RPCContext {
    /**
     * @return 注册中心
     */
    Registry getRegistry();

    /**
     * @return 远程服务客户端管理
     */
    RemoteServiceClientManager getRemoteServiceClientManager();

    /**
     * @return 服务实例管理
     */
    ServiceInstanceManager getServiceInstanceManager();

    /**
     * 启动
     */
    void start();

    /**
     * 停止
     */
    void stop();

    /**
     * @return 是否运行中
     */
    boolean isRunning();
}
