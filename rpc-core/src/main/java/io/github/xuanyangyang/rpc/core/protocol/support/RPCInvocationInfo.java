package io.github.xuanyangyang.rpc.core.protocol.support;

/**
 * rpc调用信息
 *
 * @author xuanyangyang
 * @since 2020/11/4 22:35
 */
public interface RPCInvocationInfo {
    /**
     * @return 服务名
     */
    String getServiceName();

    /**
     * @return 方法名
     */
    String getMethodName();

    /**
     * @return 参数
     */
    Object[] getArgs();

    /**
     * @return 版本
     */
    int getVersion();

    /**
     * @return 方法参数类型
     */
    Class<?>[] getParameterTypes();
}
