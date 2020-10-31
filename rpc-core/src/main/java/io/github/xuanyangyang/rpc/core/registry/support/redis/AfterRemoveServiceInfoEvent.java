package io.github.xuanyangyang.rpc.core.registry.support.redis;

import io.github.xuanyangyang.rpc.core.info.ServiceInfo;

/**
 * 删除服务之后事件
 *
 * @author xuanyangyang
 * @since 2020/10/31 17:04
 */
public class AfterRemoveServiceInfoEvent {
    private ServiceInfo serviceInfo;

    public AfterRemoveServiceInfoEvent() {
    }

    public AfterRemoveServiceInfoEvent(ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    public ServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    public void setServiceInfo(ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }
}
