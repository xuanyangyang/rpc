package io.github.xuanyangyang.rpc.demo;

/**
 * @author xuanyangyang
 * @since 2020/11/1 00:49
 */
public class DefaultHiService implements HiService {
    @Override
    public String sayHi() {
        return "hi i am " + DefaultHiService.class.getName();
    }
}
