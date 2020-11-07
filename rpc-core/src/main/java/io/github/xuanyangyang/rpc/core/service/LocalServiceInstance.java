package io.github.xuanyangyang.rpc.core.service;

import io.github.xuanyangyang.rpc.core.common.RPCException;
import io.github.xuanyangyang.rpc.core.protocol.support.RPCInvocationInfo;
import org.apache.commons.lang.ArrayUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    /**
     * method key -> method
     */
    private final Map<String, Method> methodMap = new ConcurrentHashMap<>();

    public LocalServiceInstance(ServiceInfo serviceInfo, Object service) {
        this.service = service;
        this.serviceInfo = serviceInfo;
    }

    @Override
    public Object invoke(RPCInvocationInfo invocationInfo) {
        String methodKey = getMethodKey(invocationInfo);
        Method method = methodMap.computeIfAbsent(methodKey, key -> findMethod(invocationInfo));
        try {
            return method.invoke(service, invocationInfo.getArgs());
        } catch (IllegalAccessException |
                InvocationTargetException e) {
            throw new RPCException("调用异常", e);
        }
    }

    private Method findMethod(RPCInvocationInfo invocationInfo) {
        try {
            return service.getClass().getMethod(invocationInfo.getMethodName(), invocationInfo.getParameterTypes());
        } catch (NoSuchMethodException e) {
            String message = "在" + invocationInfo.getServiceName() + "服务里找不到" + "参数为" +
                    Arrays.toString(invocationInfo.getParameterTypes()) + "的" + invocationInfo.getMethodName() + "方法";
            throw new RPCException(message, e);
        }
    }

    private String getMethodKey(RPCInvocationInfo invocationInfo) {
        return getMethodKey(invocationInfo.getMethodName(), invocationInfo.getParameterTypes());
    }

    private String getMethodKey(String methodName, Class<?>[] parameterTypes) {
        StringBuilder builder = new StringBuilder();
        builder.append(methodName);
        builder.append("(");
        if (!ArrayUtils.isEmpty(parameterTypes)) {
            int lastIndex = parameterTypes.length - 1;
            for (int i = 0; ; i++) {
                builder.append(parameterTypes[i].getName());
                if (i == lastIndex) {
                    break;
                } else {
                    builder.append(",");
                }
            }
        }
        builder.append(")");
        return builder.toString();
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
