package io.github.xuanyangyang.rpc.core;

import io.github.xuanyangyang.rpc.core.net.Server;
import io.github.xuanyangyang.rpc.core.reference.RPCReferenceInfo;
import io.github.xuanyangyang.rpc.core.reference.RPCReferenceInfoProvider;
import io.github.xuanyangyang.rpc.core.registry.Registry;
import io.github.xuanyangyang.rpc.core.registry.ServiceInfoListener;
import io.github.xuanyangyang.rpc.core.service.RemoteServiceClientManager;
import io.github.xuanyangyang.rpc.core.service.ServiceInfo;
import io.github.xuanyangyang.rpc.core.service.ServiceInfoProvider;

import java.util.Collection;

/**
 * rpc上下文
 *
 * @author xuanyangyang
 * @since 2020/10/31 23:00
 */
public class RPCContext {
    /**
     * 服务
     */
    private final Server server;
    /**
     * 注册中心
     */
    private final Registry registry;
    /**
     * 服务实例管理
     */
    private final RemoteServiceClientManager remoteServiceClientManager;
    /**
     * 服务信息提供者
     */
    private final ServiceInfoProvider serviceInfoProvider;
    /**
     * rpc引用信息提供者
     */
    private final RPCReferenceInfoProvider RPCReferenceInfoProvider;

    public RPCContext(Server server, Registry registry, RemoteServiceClientManager remoteServiceClientManager,
                      ServiceInfoProvider serviceInfoProvider,
                      RPCReferenceInfoProvider RPCReferenceInfoProvider) {
        this.server = server;
        this.registry = registry;
        this.remoteServiceClientManager = remoteServiceClientManager;
        this.serviceInfoProvider = serviceInfoProvider;
        this.RPCReferenceInfoProvider = RPCReferenceInfoProvider;
    }

    public void init() {
        registry.init();
        uploadServiceInfo();
        initServiceInfos();
        monitorServiceInfoChange();
    }

    private void uploadServiceInfo() {
        Collection<ServiceInfo> serviceInfos = serviceInfoProvider.getServiceInfos();
        if (serviceInfos.isEmpty()) {
            return;
        }
        startRPCServer();
        for (ServiceInfo serviceInfo : serviceInfos) {
            registry.addServiceInfo(serviceInfo);
        }
    }

    private void startRPCServer() {
        server.bind(10000);
    }

    private void monitorServiceInfoChange() {
        registry.addServiceInfoListener(new ServiceInfoListener() {
            @Override
            public void afterRemoveService(ServiceInfo serviceInfo) {
                remoteServiceClientManager.removeInstance(serviceInfo.getName(), serviceInfo.getId());
            }

            @Override
            public void afterAddService(ServiceInfo serviceInfo) {
                remoteServiceClientManager.addInstance(serviceInfo);
            }
        });
    }

    private void initServiceInfos() {
        remoteServiceClientManager.init();
        Collection<RPCReferenceInfo> referenceInfos = RPCReferenceInfoProvider.getProxyInfos();
        for (RPCReferenceInfo rpcReferenceInfo : referenceInfos) {
            Collection<ServiceInfo> serviceInfos = registry.getServiceInfos(rpcReferenceInfo.getName());
            for (ServiceInfo serviceInfo : serviceInfos) {
                remoteServiceClientManager.addInstance(serviceInfo);
            }
        }
    }

    public void destroy() {
        registry.destroy();
        remoteServiceClientManager.destroy();
    }
}
