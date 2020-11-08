package io.github.xuanyangyang.rpc.demo.spring.provider;

import io.github.xuanyangyang.rpc.core.config.RPCConfig;
import io.github.xuanyangyang.rpc.core.net.NetUtils;
import io.github.xuanyangyang.rpc.demo.spring.HelloService;
import io.github.xuanyangyang.rpc.spring.service.RPCService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.UnknownHostException;

/**
 * A hello
 *
 * @author xuanyangyang
 * @since 2020/11/2 00:24
 */
@RPCService
public class AHelloService implements HelloService {
    @Autowired
    private RPCConfig rpcConfig;
    private String ip;
    private static final Logger LOGGER = LoggerFactory.getLogger(AHelloService.class);

    public AHelloService() {
        try {
            ip = NetUtils.getLocalAddress().getHostAddress();
        } catch (UnknownHostException e) {
            LOGGER.error("找不到IP", e);
            ip = null;
        }
    }

    @Override
    public String hello(String name) {
        return "hello " + name + " i am " + AHelloService.class.getName() + ", ip: " + ip + "，port:" + rpcConfig.getPort();
    }

    @Override
    public String delayHello(String name, long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello " + name + " i am " + AHelloService.class.getName() + ", ip: " + ip + "，port:" + rpcConfig.getPort();
    }
}
