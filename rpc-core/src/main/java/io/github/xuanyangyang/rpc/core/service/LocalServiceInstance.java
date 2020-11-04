package io.github.xuanyangyang.rpc.core.service;

import io.github.xuanyangyang.rpc.core.common.RPCException;
import io.github.xuanyangyang.rpc.core.protocol.support.RPCInvocationInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 本地服务实例
 *
 * @author xuanyangyang
 * @since 2020/11/1 17:18
 */
public class LocalServiceInstance implements ServiceInstance {
    /**
     * 服务信息
     */
    private final ServiceInfo serviceInfo;
    /**
     * 实际的服务
     */
    private final Object service;


    public LocalServiceInstance(ServiceInfo serviceInfo, Object service) {
        this.service = service;
        this.serviceInfo = serviceInfo;
    }

    @Override
    public Object invoke(RPCInvocationInfo invocationInfo) {
        try {
            Method method = service.getClass().getMethod(invocationInfo.getMethodName(), invocationInfo.getParameterTypes());
            return method.invoke(service, invocationInfo.getArgs());
        } catch (NoSuchMethodException e) {
            String message = "在" + invocationInfo.getServiceName() + "服务里找不到" + invocationInfo.getMethodName() + "方法";
            throw new RPCException(message, e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RPCException("调用异常", e);
        }
    }

    @Override
    public ServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    @Override
    public Object getRealInstance() {
        return service;
    }
}
