package io.github.xuanyangyang.rpc.demo.spring.provider;

import io.github.xuanyangyang.rpc.demo.spring.HelloService;
import io.github.xuanyangyang.rpc.spring.service.RPCService;

/**
 * A hello
 *
 * @author xuanyangyang
 * @since 2020/11/2 00:24
 */
@RPCService
public class AHelloService implements HelloService {
    @Override
    public String hello(String name) {
        return "hello " + name + " i am " + AHelloService.class.getName();
    }
}
