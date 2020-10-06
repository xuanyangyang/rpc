package io.github.xuanyangyang.rpc.core.protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认协议管理
 *
 * @author xuanyangyang
 * @since 2020/10/6 14:36
 */
public class DefaultProtocolManager implements ProtocolManager {
    private final Map<Short, Protocol> protocolMap = new HashMap<>();

    @Override
    public Protocol getProtocol(Short id) {
        return protocolMap.get(id);
    }

    @Override
    public boolean addProtocol(Protocol protocol) {
        if (protocol == null) {
            return false;
        }
        protocolMap.put(protocol.getId(), protocol);
        return true;
    }

    @Override
    public Protocol removeProtocol(Short id) {
        return protocolMap.remove(id);
    }
}
