package io.github.xuanyangyang.rpc.core.net.dispatcher.support;

import io.github.xuanyangyang.rpc.core.protocol.support.Response;

import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

/**
 * 返回值处理器
 *
 * @author xuanyangyang
 * @since 2020/11/8 00:33
 */
public interface ReturnValueHandler {
    /**
     * @param returnValue 返回值
     * @return 是否支持这返回值
     */
    boolean supports(Object returnValue);

    /**
     * 处理返回值
     *
     * @param returnValue      返回值
     * @param responseSupplier response提供者
     * @return response 处理结果
     */
    CompletionStage<Response> handleReturnValue(Object returnValue, Supplier<Response> responseSupplier);

    /**
     * 越小越优先
     *
     * @return 顺序
     */
    default int getOrder() {
        return 0;
    }

    /**
     * 最大优先级
     */
    int MAX_ORDER = Integer.MIN_VALUE;
    /**
     * 最小优先级
     */
    int MIN_ORDER = Integer.MAX_VALUE;
}
