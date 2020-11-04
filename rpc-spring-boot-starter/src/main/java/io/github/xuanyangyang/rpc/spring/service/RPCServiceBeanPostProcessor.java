package io.github.xuanyangyang.rpc.spring.service;

import io.github.xuanyangyang.rpc.core.common.RPCException;
import io.github.xuanyangyang.rpc.core.net.NetUtils;
import io.github.xuanyangyang.rpc.core.service.LocalServiceInstance;
import io.github.xuanyangyang.rpc.core.service.ServiceInfo;
import io.github.xuanyangyang.rpc.core.service.ServiceInstance;
import io.github.xuanyangyang.rpc.core.service.ServiceInstanceManager;
import io.github.xuanyangyang.rpc.spring.config.SpringRPCProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * rpc service 扫描
 *
 * @author xuanyangyang
 * @since 2020/11/2 00:06
 */
public class RPCServiceBeanPostProcessor implements BeanPostProcessor {
    private final SpringRPCProperties rpcConfig;
    private final ServiceInstanceManager serviceInstanceManager;

    public RPCServiceBeanPostProcessor(SpringRPCProperties rpcConfig, ServiceInstanceManager serviceInstanceManager) {
        this.serviceInstanceManager = serviceInstanceManager;
        this.rpcConfig = rpcConfig;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        RPCService rpcService = beanClass.getAnnotation(RPCService.class);
        if (rpcService != null) {
            String ip;
            try {
                InetAddress localAddress = NetUtils.getLocalAddress();
                ip = localAddress.getHostAddress();
            } catch (UnknownHostException e) {
                throw new RPCException("无法获取本地IP");
            }
            if (rpcService.name().isEmpty()) {
                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> interfaceClass : interfaces) {
                    ServiceInfo serviceInfo = createServiceInfo(interfaceClass.getName(), ip, rpcConfig.getPort(), rpcService.protocolId(), rpcService.version());
                    registryService(serviceInfo, bean);
                }
            } else {
                ServiceInfo serviceInfo = createServiceInfo(rpcService.name(), ip, rpcConfig.getPort(), rpcService.protocolId(), rpcService.version());
                registryService(serviceInfo, bean);
            }
        }
        return bean;
    }

    private ServiceInfo createServiceInfo(String serviceName, String ip, int port, short protocolId, int version) {
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setName(serviceName);
        serviceInfo.setIp(ip);
        serviceInfo.setPort(port);
        serviceInfo.setProtocolId(protocolId);
        serviceInfo.setVersion(version);
        serviceInfo.setId(serviceInfo.getIp() + ":" + serviceInfo.getPort());
        return serviceInfo;
    }

    private void registryService(ServiceInfo serviceInfo, Object obj) {
        ServiceInstance newServiceInstance = new LocalServiceInstance(serviceInfo, obj);
        ServiceInstance oldInstance = serviceInstanceManager.getInstance(newServiceInstance.getServiceName());
        if (oldInstance != null) {
            throw new RPCException("同时存在" + newServiceInstance.getServiceName() + "两个服务" +
                    Arrays.toString(new String[]{oldInstance.getRealInstance().getClass().getName(), newServiceInstance.getRealInstance().getClass().getName()})
                    + "，无法进行选择，请指定@RPCService的name属性");
        }
        serviceInstanceManager.addInstance(newServiceInstance);
    }
}
