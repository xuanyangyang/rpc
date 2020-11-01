package io.github.xuanyangyang.rpc.spring.reference;

import io.github.xuanyangyang.rpc.core.common.RPCException;
import io.github.xuanyangyang.rpc.core.reference.RPCProxyFactory;
import io.github.xuanyangyang.rpc.core.reference.RPCReferenceInfo;
import io.github.xuanyangyang.rpc.core.reference.RPCReferenceInfoProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.util.ReflectionUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * 注解引用提供
 *
 * @author xuanyangyang
 * @since 2020/11/1 22:43
 */
public class AnnotationRPCReferenceInfoProvider implements InstantiationAwareBeanPostProcessor, RPCReferenceInfoProvider {
    private final List<RPCReferenceInfo> referenceInfoList = new LinkedList<>();

    private final RPCProxyFactory rpcProxyFactory;

    public AnnotationRPCReferenceInfoProvider(RPCProxyFactory rpcProxyFactory) {
        this.rpcProxyFactory = rpcProxyFactory;
    }

    @Override
    public Collection<RPCReferenceInfo> getProxyInfos() {
        return referenceInfoList;
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        do {
            ReflectionUtils.doWithLocalFields(targetClass, field -> {
                RPCReference rpcReference = field.getAnnotation(RPCReference.class);
                if (rpcReference == null) {
                    return;
                }
                Class<?> fieldClass = field.getType();
                if (!fieldClass.isInterface()) {
                    throw new RPCException("不支持非接口RPC引用");
                }
                RPCReferenceInfo rpcReferenceInfo = new RPCReferenceInfo();
                rpcReferenceInfo.setClz(fieldClass);
                rpcReferenceInfo.setProtocolId(rpcReference.protocolId());
                if (rpcReference.serviceName().isEmpty()) {
                    rpcReferenceInfo.setName(fieldClass.getName());
                } else {
                    rpcReferenceInfo.setName(rpcReference.serviceName());
                }
                rpcReferenceInfo.setVersion(rpcReference.version());
                referenceInfoList.add(rpcReferenceInfo);
                Object proxy = rpcProxyFactory.getOrCreateProxy(rpcReferenceInfo);
                field.setAccessible(true);
                field.set(bean, proxy);
            });
            targetClass = targetClass.getSuperclass();
        } while (targetClass != null && targetClass != Object.class);
        return pvs;
    }
}
