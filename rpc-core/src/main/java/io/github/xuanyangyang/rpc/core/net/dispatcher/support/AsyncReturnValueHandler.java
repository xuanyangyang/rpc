package io.github.xuanyangyang.rpc.core.net.dispatcher.support;

import io.github.xuanyangyang.rpc.core.protocol.support.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;

/**
 * 异步返回值处理器
 * 处理类型为{@link CompletionStage},{@link java.util.concurrent.Future}的返回值
 *
 * @author xuanyangyang
 * @see CompletionStage
 * @see java.util.concurrent.Future
 * @since 2020/11/8 01:11
 */
public class AsyncReturnValueHandler implements ReturnValueHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncReturnValueHandler.class);

    @Override
    public boolean supports(Object returnValue) {
        Class<?> returnValueClass = returnValue.getClass();
        return CompletionStage.class.isAssignableFrom(returnValueClass) || Future.class.isAssignableFrom(returnValueClass);
    }

    @Override
    public CompletionStage<Response> handleReturnValue(Object returnValue, Supplier<Response> responseSupplier) {
        Class<?> returnValueClass = returnValue.getClass();
        if (CompletionStage.class.isAssignableFrom(returnValueClass)) {
            return handleCompletionStage((CompletionStage<?>) returnValue, responseSupplier);
        } else {
            return handleFuture((Future<?>) returnValue, responseSupplier);
        }
    }

    private CompletionStage<Response> handleCompletionStage(CompletionStage<?> completionStage, Supplier<Response> responseSupplier) {
        return completionStage.thenApply(data -> {
            Response response = responseSupplier.get();
            response.setData(data);
            return response;
        }).exceptionally(throwable -> {
            Response response = responseSupplier.get();
            response.setState(Response.STATE_SERVER_ERROR);
            response.setErrMsg(throwable.getMessage());
            LOGGER.error("获取返回值失败", throwable);
            return response;
        });
    }

    private CompletionStage<Response> handleFuture(Future<?> future, Supplier<Response> responseSupplier) {
        Response response = responseSupplier.get();
        try {
            Object data = future.get();
            response.setData(data);
        } catch (InterruptedException | ExecutionException e) {
            response.setState(Response.STATE_SERVER_ERROR);
            response.setErrMsg(e.getMessage());
            LOGGER.error("获取返回值失败", e);
        }
        return CompletableFuture.completedFuture(response);
    }
}
