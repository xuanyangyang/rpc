package io.github.xuanyangyang.rpc.core.future;

import io.github.xuanyangyang.rpc.core.common.RPCException;
import io.github.xuanyangyang.rpc.core.protocol.support.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认future
 *
 * @author xuanyangyang
 * @since 2020/10/7 15:50
 */
public class DefaultFuture<T> extends CompletableFuture<T> {
    private final Long id;
    private static final Map<Long, DefaultFuture> FUTURES = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(DefaultFuture.class);

    public DefaultFuture(Long id) {
        this.id = id;
        FUTURES.put(id, this);
    }

    public static <T> DefaultFuture<T> newFuture(Long id) {
        return new DefaultFuture<>(id);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        Response response = new Response(id);
        response.setState(Response.STATE_CLIENT_ERROR);
        response.setData("请求被取消");
        doReceived(response);
        return true;
    }

    @SuppressWarnings("unchecked")
    private void doReceived(Response response) {
        if (response == null) {
            throw new IllegalStateException("response 不能为null");
        }
        if (response.getState() == Response.STATE_OK) {
            this.complete((T) response.getData());
        } else {
            this.completeExceptionally(new RPCException(response.getErrMsg()));
        }
    }

    @SuppressWarnings("rawtypes")
    public static void received(Response response) {
        DefaultFuture future = FUTURES.remove(response.getId());
        if (future == null) {
            logger.warn("找不到响应对应的请求，响应到达时间{}，响应状态{}", LocalDateTime.now(), response.getState());
            return;
        }
        future.doReceived(response);
    }
}
