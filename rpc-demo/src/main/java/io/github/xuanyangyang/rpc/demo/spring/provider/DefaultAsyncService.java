package io.github.xuanyangyang.rpc.demo.spring.provider;

import io.github.xuanyangyang.rpc.demo.spring.AsyncService;
import io.github.xuanyangyang.rpc.spring.service.RPCService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 异步服务
 *
 * @author xuanyangyang
 * @since 2020/11/10 13:12
 */
@RPCService
public class DefaultAsyncService implements AsyncService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAsyncService.class);

    @Override
    public CompletableFuture<String> hi(String name) {
        return CompletableFuture.supplyAsync(() -> {
            int randomSecond = ThreadLocalRandom.current().nextInt(3);
            try {
                Thread.sleep(randomSecond * 1000);
            } catch (InterruptedException e) {
                LOGGER.error("随机休眠被打断", e);
            }
            return "hi " + name + ",i am " + DefaultAsyncService.class.getName();
        });
    }
}
