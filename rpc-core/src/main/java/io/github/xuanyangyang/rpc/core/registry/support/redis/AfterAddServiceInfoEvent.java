package io.github.xuanyangyang.rpc.core.registry.support.redis;

import io.github.xuanyangyang.rpc.core.info.ServiceInfo;

/**
 * 添加服务之后事件
 *
 * @author xuanyangyang
 * @since 2020/10/31 17:03
 */
public class AfterAddServiceInfoEvent {
    private ServiceInfo serviceInfo;

    public AfterAddServiceInfoEvent() {
    }

    AfterAddServiceInfoEvent(ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    public ServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    public void setServiceInfo(ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }
}
