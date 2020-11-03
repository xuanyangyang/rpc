package io.github.xuanyangyang.rpc.demo.spring.provider;

import io.github.xuanyangyang.rpc.demo.spring.CalcService;
import io.github.xuanyangyang.rpc.spring.service.RPCService;

import java.util.concurrent.CompletableFuture;

/**
 * @author xuanyangyang
 * @since 2020/11/2 02:24
 */
@RPCService
public class ACalcService implements CalcService {
    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public int minus(int a, int b) {
        return a - b;
    }

    @Override
    public CompletableFuture<Integer> multiply(int a, int b) {
        return CompletableFuture.supplyAsync(() -> a * b);
    }
}
