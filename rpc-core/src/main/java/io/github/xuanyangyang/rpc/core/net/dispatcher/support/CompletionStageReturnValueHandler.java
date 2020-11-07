package io.github.xuanyangyang.rpc.core.net.dispatcher.support;

import io.github.xuanyangyang.rpc.core.net.Channel;
import io.github.xuanyangyang.rpc.core.protocol.support.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

/**
 * {@link CompletionStage}返回值处理器
 *
 * @author xuanyangyang
 * @since 2020/11/8 01:11
 */
public class CompletionStageReturnValueHandler implements ReturnValueHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompletionStageReturnValueHandler.class);

    @Override
    public boolean supports(Object returnValue) {
        return CompletionStage.class.isAssignableFrom(returnValue.getClass());
    }

    @Override
    public void handleReturnValue(Channel channel, Object returnValue, Supplier<Response> responseSupplier) {
        CompletionStage<?> completionStage = (CompletionStage<?>) returnValue;
        completionStage.whenComplete((data, throwable) -> {
            if (throwable != null) {
                Response response = responseSupplier.get();
                response.setState(Response.STATE_SERVER_ERROR);
                response.setErrMsg(throwable.getMessage());
                channel.send(response);
                LOGGER.error("处理返回值失败", throwable);
            }
        }).thenAccept(data -> {
            Response response = responseSupplier.get();
            response.setData(data);
            channel.send(response);
        });
    }
}
