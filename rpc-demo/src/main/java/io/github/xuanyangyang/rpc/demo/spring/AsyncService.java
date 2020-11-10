package io.github.xuanyangyang.rpc.demo.spring;

import java.util.concurrent.CompletableFuture;

/**
 * 异步服务
 *
 * @author xuanyangyang
 * @since 2020/11/10 13:05
 */
public interface AsyncService {
    /**
     * 异步hi
     *
     * @param name 名称
     * @return hi 结果
     */
    CompletableFuture<String> hi(String name);
}
