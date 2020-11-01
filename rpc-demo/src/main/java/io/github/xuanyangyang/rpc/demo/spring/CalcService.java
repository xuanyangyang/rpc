package io.github.xuanyangyang.rpc.demo.spring;

/**
 * 计算服务
 *
 * @author xuanyangyang
 * @since 2020/11/2 02:23
 */
public interface CalcService {
    /**
     * a+b
     *
     * @param a a
     * @param b b
     * @return a+b结果
     */
    int add(int a, int b);

    /**
     * a-b
     *
     * @param a a
     * @param b b
     * @return a-b结果
     */
    int minus(int a, int b);
}
