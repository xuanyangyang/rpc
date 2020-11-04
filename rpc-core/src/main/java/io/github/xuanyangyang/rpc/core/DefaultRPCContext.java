package io.github.xuanyangyang.rpc.core;

import io.github.xuanyangyang.rpc.core.config.RPCConfig;
import io.github.xuanyangyang.rpc.core.net.Server;
import io.github.xuanyangyang.rpc.core.reference.RPCReferenceInfo;
import io.github.xuanyangyang.rpc.core.reference.RPCReferenceManager;
import io.github.xuanyangyang.rpc.core.registry.Registry;
import io.github.xuanyangyang.rpc.core.registry.ServiceInfoListener;
import io.github.xuanyangyang.rpc.core.service.RemoteServiceClientManager;
import io.github.xuanyangyang.rpc.core.service.ServiceInfo;
import io.github.xuanyangyang.rpc.core.service.ServiceInstanceManager;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * rpc上下文
 *
 * @author xuanyangyang
 * @since 2020/10/31 23:00
 */
public class DefaultRPCContext implements RPCContext {
    /**
     * 服务
     */
    private final Server server;
    /**
     * 注册中心
     */
    private final Registry registry;
    /**
     * 服务信息提供者
     */
    private final ServiceInstanceManager serviceInstanceManager;
    /**
     * 服务实例管理
     */
    private final RemoteServiceClientManager remoteServiceClientManager;
    /**
     * rpc引用信息管理
     */
    private final RPCReferenceManager referenceManager;
    /**
     * 配置
     */
    private final RPCConfig config;
    /**
     * 运行中
     */
    private final AtomicBoolean running = new AtomicBoolean();

    public DefaultRPCContext(Server server, Registry registry, ServiceInstanceManager serviceInstanceManager,
                             RemoteServiceClientManager remoteServiceClientManager, RPCReferenceManager referenceManager, RPCConfig config) {
        this.server = server;
        this.registry = registry;
        this.serviceInstanceManager = serviceInstanceManager;
        this.remoteServiceClientManager = remoteServiceClientManager;
        this.referenceManager = referenceManager;
        this.config = config;
    }

    private void start0() {
        registry.init();
        uploadServiceInfo();
        initServiceInfos();
        monitorServiceInfoChange();
    }

    private void uploadServiceInfo() {
        Collection<ServiceInfo> serviceInfos = serviceInstanceManager.getServiceInfos();
        if (serviceInfos.isEmpty()) {
            return;
        }
        startRPCServer();
        for (ServiceInfo serviceInfo : serviceInfos) {
            registry.addServiceInfo(serviceInfo);
        }
    }

    private void startRPCServer() {
        server.bind(config.getPort());
    }

    private void monitorServiceInfoChange() {
        registry.addServiceInfoListener(new ServiceInfoListener() {
            @Override
            public void afterRemoveService(ServiceInfo serviceInfo) {
                if (referenceManager.hasInfo(serviceInfo.getName())) {
                    remoteServiceClientManager.removeClient(serviceInfo.getName(), serviceInfo.getId());
                }
            }

            @Override
            public void afterAddService(ServiceInfo serviceInfo) {
                if (referenceManager.hasInfo(serviceInfo.getName())) {
                    remoteServiceClientManager.addClient(serviceInfo);
                }
            }
        });
    }

    private void initServiceInfos() {
        remoteServiceClientManager.init();
        Collection<RPCReferenceInfo> referenceInfos = referenceManager.getInfos();
        for (RPCReferenceInfo rpcReferenceInfo : referenceInfos) {
            Collection<ServiceInfo> serviceInfos = registry.getServiceInfos(rpcReferenceInfo.getName());
            for (ServiceInfo serviceInfo : serviceInfos) {
                remoteServiceClientManager.addClient(serviceInfo);
            }
        }
    }

    private void stop0() {
        server.shutdown();
        for (ServiceInfo serviceInfo : serviceInstanceManager.getServiceInfos()) {
            registry.removeServiceInfo(serviceInfo.getName(), serviceInfo.getId());
        }
        registry.destroy();
        remoteServiceClientManager.destroy();
    }

    @Override
    public Registry getRegistry() {
        return registry;
    }

    @Override
    public RemoteServiceClientManager getRemoteServiceClientManager() {
        return remoteServiceClientManager;
    }

    @Override
    public ServiceInstanceManager getServiceInstanceManager() {
        return serviceInstanceManager;
    }

    @Override
    public void start() {
        if (running.compareAndSet(false, true)) {
            start0();
        }
    }

    @Override
    public void stop() {
        if (running.compareAndSet(true, false)) {
            stop0();
        }
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }
}
