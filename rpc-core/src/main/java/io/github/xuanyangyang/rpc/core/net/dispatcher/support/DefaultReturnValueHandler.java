package io.github.xuanyangyang.rpc.core.net.dispatcher.support;

import io.github.xuanyangyang.rpc.core.protocol.support.Response;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

/**
 * 默认返回值处理器，直接返回结果
 *
 * @author xuanyangyang
 * @since 2020/11/8 01:36
 */
public class DefaultReturnValueHandler implements ReturnValueHandler {
    @Override
    public boolean supports(Object returnValue) {
        return true;
    }

    @Override
    public CompletionStage<Response> handleReturnValue(Object returnValue, Supplier<Response> responseSupplier) {
        Response response = responseSupplier.get();
        response.setState(Response.STATE_OK);
        response.setData(returnValue);
        return CompletableFuture.completedFuture(response);
    }

    @Override
    public int getOrder() {
        return MIN_ORDER;
    }
}
