package io.github.xuanyangyang.rpc.core.reference;

import io.github.xuanyangyang.rpc.core.common.RPCException;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认引用信息管理
 *
 * @author xuanyangyang
 * @since 2020/11/3 23:23
 */
public class DefaultRPCReferenceManager implements RPCReferenceManager {
    /**
     * name -> RPCReferenceInfo
     */
    private final Map<String, RPCReferenceInfo> referenceInfoMap = new ConcurrentHashMap<>();
    /**
     * name -> rpc引用
     */
    private final Map<String, Object> referenceMap = new ConcurrentHashMap<>();
    /**
     * rpc 代理工厂
     */
    private final RPCProxyFactory rpcProxyFactory;

    public DefaultRPCReferenceManager(RPCProxyFactory rpcProxyFactory) {
        this.rpcProxyFactory = rpcProxyFactory;
    }

    @Override
    public void addInfo(RPCReferenceInfo referenceInfo) {
        RPCReferenceInfo oldReferenceInfo = referenceInfoMap.put(referenceInfo.getName(), referenceInfo);
        if (oldReferenceInfo != null && oldReferenceInfo != referenceInfo) {
            throw new RPCException("重复的引用信息," + oldReferenceInfo.getClz().getName() + "," + referenceInfo.getClass().getName());
        }
    }

    @Override
    public Collection<RPCReferenceInfo> getInfos() {
        return referenceInfoMap.values();
    }

    @Override
    public RPCReferenceInfo getInfo(String serviceName) {
        return referenceInfoMap.get(serviceName);
    }

    @Override
    public boolean hasInfo(String serviceName) {
        return referenceInfoMap.containsKey(serviceName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getOrCreateReference(String serviceName) {
        Object obj = referenceMap.get(serviceName);
        if (obj == null) {
            RPCReferenceInfo info = referenceInfoMap.get(serviceName);
            if (info == null) {
                return null;
            }
            obj = getOrCreateReference0(info);
        }
        return (T) obj;
    }

    @SuppressWarnings("unchecked")
    private <T> T getOrCreateReference0(RPCReferenceInfo referenceInfo) {
        return (T) referenceMap.computeIfAbsent(referenceInfo.getName(), key -> rpcProxyFactory.getOrCreateProxy(referenceInfo));
    }
}
