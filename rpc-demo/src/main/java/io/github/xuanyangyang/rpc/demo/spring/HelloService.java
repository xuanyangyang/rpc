package io.github.xuanyangyang.rpc.demo.spring;

/**
 * hello 服务
 *
 * @author xuanyangyang
 * @since 2020/11/2 00:23
 */
public interface HelloService {
    /**
     * hello
     *
     * @param name 名称
     * @return hello结果
     */
    String hello(String name);

    /**
     * 延迟delay毫秒hello
     *
     * @param name  名称
     * @param delay 延迟时间，单位毫秒
     * @return hello结果
     */
    String delayHello(String name, long delay);
}
