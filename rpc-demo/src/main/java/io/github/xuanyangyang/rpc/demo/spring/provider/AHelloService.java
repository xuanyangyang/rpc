package io.github.xuanyangyang.rpc.demo.spring.provider;

import io.github.xuanyangyang.rpc.core.config.RPCConfig;
import io.github.xuanyangyang.rpc.core.net.NetUtils;
import io.github.xuanyangyang.rpc.demo.spring.HelloService;
import io.github.xuanyangyang.rpc.spring.service.RPCService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A hello
 *
 * @author xuanyangyang
 * @since 2020/11/2 00:24
 */
@RPCService
public class AHelloService implements HelloService, InitializingBean {
    @Autowired
    private RPCConfig rpcConfig;
    private String ip;

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

    @Override
    public void afterPropertiesSet() throws Exception {
        ip = NetUtils.getLocalAddress().getHostAddress();
    }
}
