package io.github.xuanyangyang.rpc.core.protocol.support;

/**
 * rpc调用信息
 *
 * @author xuanyangyang
 * @since 2020/10/5 15:49
 */
public class RpcInvocationInfo {
    /**
     * 服务名
     */
    private String serviceName;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 版本
     */
    private int version;
    /**
     * 参数
     */
    private Object[] args;
    /**
     * 方法参数类型
     */
    private Class<?>[] parameterTypes;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }
}
