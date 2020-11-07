package io.github.xuanyangyang.rpc.spring.reference;

import io.github.xuanyangyang.rpc.core.common.RPCException;
import io.github.xuanyangyang.rpc.core.config.RPCConfig;
import io.github.xuanyangyang.rpc.core.reference.DefaultRPCReferenceInfo;
import io.github.xuanyangyang.rpc.core.reference.RPCReferenceManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;

/**
 * 引用扫描，处理{@link RPCReference}
 *
 * @author xuanyangyang
 * @since 2020/11/1 22:43
 */
public class RPCReferenceBeanProcessor implements InstantiationAwareBeanPostProcessor, ApplicationContextAware {
    private RPCReferenceManager referenceManager;
    private RPCConfig rpcConfig;
    private ApplicationContext applicationContext;

    public RPCReferenceBeanProcessor() {
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
                    RPCConfig rpcConfig = getRpcConfig();
                    rpcReferenceInfo.setTimeout(rpcConfig.getTimeout());
                    rpcReferenceInfo.setTimeoutTimeUnit(rpcConfig.getTimeoutTimeUnit());
                } else {
                    rpcReferenceInfo.setTimeout(rpcReference.timeout());
                    rpcReferenceInfo.setTimeoutTimeUnit(rpcReference.timeoutTimeUnit());
                }
                RPCReferenceManager referenceManager = getReferenceManager();
                referenceManager.addInfo(rpcReferenceInfo);
                Object proxy = referenceManager.getOrCreateReference(rpcReferenceInfo.getName());
                field.setAccessible(true);
                field.set(bean, proxy);
            });
            targetClass = targetClass.getSuperclass();
        } while (targetClass != null && targetClass != Object.class);
        return pvs;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public RPCReferenceManager getReferenceManager() {
        if (referenceManager == null) {
            referenceManager = applicationContext.getBean(RPCReferenceManager.class);
        }
        return referenceManager;
    }

    public RPCConfig getRpcConfig() {
        if (rpcConfig == null) {
            rpcConfig = applicationContext.getBean(RPCConfig.class);
        }
        return rpcConfig;
    }
}
