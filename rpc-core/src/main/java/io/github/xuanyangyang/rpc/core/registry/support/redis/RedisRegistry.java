package io.github.xuanyangyang.rpc.core.registry.support.redis;

import io.github.xuanyangyang.rpc.core.net.NetConstants;
import io.github.xuanyangyang.rpc.core.registry.Registry;
import io.github.xuanyangyang.rpc.core.registry.ServiceInfoListener;
import io.github.xuanyangyang.rpc.core.info.ServiceInfo;
import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.redisson.config.Config;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * redis 注册中心
 *
 * @author xuanyangyang
 * @since 2020/10/31 16:22
 */
public class RedisRegistry implements Registry {
    private RedissonClient redissonClient;
    private RTopic serverInfoTopic;
    private final List<ServiceInfoListener> listeners = new LinkedList<>();
    private final Codec serviceInfoCodec = new TypedJsonJacksonCodec(ServiceInfo.class, String.class, ServiceInfo.class);

    @Override
    public boolean addServiceInfo(ServiceInfo serviceInfo) {
        RMap<String, ServiceInfo> serviceInfoMap = redissonClient.getMap(serviceInfo.getName(), serviceInfoCodec);
        serviceInfoMap.put(serviceInfo.getId(), serviceInfo);
        serverInfoTopic.publishAsync(new AfterAddServiceInfoEvent(serviceInfo));
        return true;
    }

    @Override
    public ServiceInfo getServiceInfo(String serviceName, String serviceId) {
        RMap<String, ServiceInfo> serviceInfoMap = redissonClient.getMap(serviceName, serviceInfoCodec);
        return serviceInfoMap.get(serviceId);
    }

    @Override
    public Collection<ServiceInfo> getServiceInfos(String serviceName) {
        RMap<String, ServiceInfo> serviceInfoMap = redissonClient.getMap(serviceName, serviceInfoCodec);
        return serviceInfoMap.values();
    }

    @Override
    public ServiceInfo removeServiceInfo(String serviceName, String serviceId) {
        RMap<String, ServiceInfo> serviceInfoMap = redissonClient.getMap(serviceName, serviceInfoCodec);
        ServiceInfo serviceInfo = serviceInfoMap.remove(serviceId);
        if (serviceInfo != null) {
            serverInfoTopic.publishAsync(new AfterRemoveServiceInfoEvent(serviceInfo));
        }
        return serviceInfo;
    }

    @Override
    public void addServiceInfoListener(ServiceInfoListener listener) {
        listeners.add(listener);
    }

    @Override
    public void init() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379");
        redissonClient = Redisson.create(config);
        serverInfoTopic = redissonClient.getTopic("serverInfoTopic", new JsonJacksonCodec());
        serverInfoTopic.addListener(AfterAddServiceInfoEvent.class, (channel, msg) -> {
            for (ServiceInfoListener listener : listeners) {
                listener.afterAddService(msg.getServiceInfo());
            }
        });
        serverInfoTopic.addListener(AfterRemoveServiceInfoEvent.class, (channel, msg) -> {
            for (ServiceInfoListener listener : listeners) {
                listener.afterRemoveService(msg.getServiceInfo());
            }
        });
    }

    @Override
    public void destroy() {
        redissonClient = null;
        serverInfoTopic.removeAllListeners();
        serverInfoTopic = null;
    }

    public static void main(String[] args) {
        RedisRegistry redisRegistry = new RedisRegistry();
        redisRegistry.init();
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setId("test-1");
        serviceInfo.setName("test");
        serviceInfo.setPort(1);
        serviceInfo.setVersion(1);
        serviceInfo.setProtocolId(NetConstants.DEFAULT_PROTOCOL_ID);
        serviceInfo.setIp("localhost");
        redisRegistry.addServiceInfo(serviceInfo);

        ServiceInfo queryServiceInfo = redisRegistry.getServiceInfo("test", "test-1");
        System.out.println(queryServiceInfo);
    }
}
