package io.github.xuanyangyang.rpc.core.net.dispatcher.support;

import io.github.xuanyangyang.rpc.core.net.Channel;
import io.github.xuanyangyang.rpc.core.protocol.support.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;

/**
 * {@link Future}返回值处理器
 *
 * @author xuanyangyang
 * @since 2020/11/8 01:27
 */
public class FutureReturnValueHandler implements ReturnValueHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(FutureReturnValueHandler.class);

    @Override
    public boolean supports(Object returnValue) {
        return Future.class.isAssignableFrom(returnValue.getClass());
    }

    @Override
    public void handleReturnValue(Channel channel, Object returnValue, Supplier<Response> responseSupplier) {
        Response response = responseSupplier.get();
        try {
            Object data = ((Future<?>) returnValue).get();
            response.setData(data);
        } catch (InterruptedException | ExecutionException e) {
            response.setState(Response.STATE_SERVER_ERROR);
            response.setErrMsg(e.getMessage());
            LOGGER.error("处理返回值失败", e);
        }
        channel.send(response);
    }
}
