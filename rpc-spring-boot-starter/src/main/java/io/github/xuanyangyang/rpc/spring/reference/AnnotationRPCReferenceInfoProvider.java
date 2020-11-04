package io.github.xuanyangyang.rpc.spring.reference;

import io.github.xuanyangyang.rpc.core.common.RPCException;
import io.github.xuanyangyang.rpc.core.config.RPCConfig;
import io.github.xuanyangyang.rpc.core.reference.DefaultRPCReferenceInfo;
import io.github.xuanyangyang.rpc.core.reference.RPCReferenceManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.util.ReflectionUtils;

/**
 * 注解引用提供
 *
 * @author xuanyangyang
 * @since 2020/11/1 22:43
 */
public class AnnotationRPCReferenceInfoProvider implements InstantiationAwareBeanPostProcessor {
    private final RPCReferenceManager referenceManager;
    private final RPCConfig rpcConfig;

    public AnnotationRPCReferenceInfoProvider(RPCReferenceManager referenceManager, RPCConfig rpcConfig) {
        this.referenceManager = referenceManager;
        this.rpcConfig = rpcConfig;
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
                DefaultRPCReferenceInfo rpcReferenceInfo = new DefaultRPCReferenceInfo();
                rpcReferenceInfo.setClz(fieldClass);
                rpcReferenceInfo.setProtocolId(rpcReference.protocolId());
                if (rpcReference.serviceName().isEmpty()) {
                    rpcReferenceInfo.setName(fieldClass.getName());
                } else {
                    rpcReferenceInfo.setName(rpcReference.serviceName());
                }
                rpcReferenceInfo.setVersion(rpcReference.version());
                if (rpcReference.timeout() == 0) {
                    rpcReferenceInfo.setTimeout(rpcConfig.getTimeout());
                    rpcReferenceInfo.setTimeoutTimeUnit(rpcConfig.getTimeoutTimeUnit());
                } else {
                    rpcReferenceInfo.setTimeout(rpcReference.timeout());
                    rpcReferenceInfo.setTimeoutTimeUnit(rpcReference.timeoutTimeUnit());
                }
                referenceManager.addInfo(rpcReferenceInfo);
                Object proxy = referenceManager.getOrCreateReference(rpcReferenceInfo.getName());
                field.setAccessible(true);
                field.set(bean, proxy);
            });
            targetClass = targetClass.getSuperclass();
        } while (targetClass != null && targetClass != Object.class);
        return pvs;
    }
}
