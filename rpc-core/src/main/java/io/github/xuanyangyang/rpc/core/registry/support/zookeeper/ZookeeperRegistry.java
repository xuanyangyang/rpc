package io.github.xuanyangyang.rpc.core.registry.support.zookeeper;

import io.github.xuanyangyang.rpc.core.codec.Codec;
import io.github.xuanyangyang.rpc.core.common.RPCException;
import io.github.xuanyangyang.rpc.core.registry.Registry;
import io.github.xuanyangyang.rpc.core.registry.ServiceInfoListener;
import io.github.xuanyangyang.rpc.core.service.ServiceInfo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * zookeeper 注册中心
 *
 * @author xuanyangyang
 * @since 2020/11/6 12:51
 */
public class ZookeeperRegistry implements Registry {
    /**
     * 配置
     */
    private final ZookeeperConfig config;
    /**
     * 编解码器
     */
    private final Codec codec;
    /**
     * 客户端
     */
    private CuratorFramework client;
    /**
     * 监听
     */
    private CuratorCache cache;
    /**
     * 路径分隔符
     */
    public static final String PATH_SEPARATOR = "/";
    /**
     * 对象大小
     */
    public static final int OBJ_SIZE = 4;
    /**
     * 监听列表
     */
    private final List<ServiceInfoListener> listeners = new CopyOnWriteArrayList<>();

    public ZookeeperRegistry(ZookeeperConfig config, Codec codec) {
        this.config = config;
        this.codec = codec;
    }

    @Override
    public boolean addServiceInfo(ServiceInfo serviceInfo) {
        try {
            client.create()
                    .creatingParentContainersIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(getPath(serviceInfo), encode(serviceInfo));
        } catch (Exception e) {
            throw new RPCException("添加服务失败", e);
        }
        return true;
    }

    private String getPath(ServiceInfo serviceInfo) {
        return getPath(serviceInfo.getName(), serviceInfo.getId());
    }

    private String getPath(String serviceName, String serviceId) {
        return config.getRootPath() + PATH_SEPARATOR + serviceName + PATH_SEPARATOR + serviceId;
    }

    private String getPath(String serviceName) {
        return config.getRootPath() + PATH_SEPARATOR + serviceName;
    }

    @Override
    public ServiceInfo getServiceInfo(String serviceName, String serviceId) {
        String path = getPath(serviceName, serviceId);
        ServiceInfo serviceInfo;
        try {
            byte[] dataBytes = client.getData().forPath(path);
            serviceInfo = decode(dataBytes);
        } catch (KeeperException.NoNodeException e) {
            serviceInfo = null;
        } catch (Exception e) {
            throw new RPCException("获取" + serviceName + ":" + serviceId + "信息失败", e);
        }
        return serviceInfo;
    }

    @Override
    public Collection<ServiceInfo> getServiceInfos(String serviceName) {
        Collection<ServiceInfo> infos = new LinkedList<>();
        try {
            List<String> ids = client.getChildren().forPath(getPath(serviceName));
            for (String id : ids) {
                ServiceInfo serviceInfo = getServiceInfo(serviceName, id);
                if (serviceInfo == null) {
                    continue;
                }
                infos.add(serviceInfo);
            }
        } catch (KeeperException.NoNodeException e) {
            // 忽略异常
        } catch (Exception e) {
            throw new RPCException("获取" + serviceName + "服务信息失败", e);
        }
        return infos;
    }

    @Override
    public ServiceInfo removeServiceInfo(String serviceName, String serviceId) {
        ServiceInfo serviceInfo = getServiceInfo(serviceName, serviceId);
        if (serviceInfo == null) {
            return null;
        }
        try {
            client.delete().guaranteed().forPath(getPath(serviceName, serviceId));
        } catch (KeeperException.NoNodeException e) {
            // 忽略异常
        } catch (Exception e) {
            throw new RPCException("删除" + serviceName + ":" + serviceId + "信息失败", e);
        }
        return serviceInfo;
    }

    @Override
    public void addServiceInfoListener(ServiceInfoListener listener) {
        listeners.add(listener);
    }

    @Override
    public void init() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.newClient(config.getAddress(), retryPolicy);
        client.start();
        initListener();
    }

    private void initListener() {
        cache = CuratorCache.build(client, config.getRootPath());
        CuratorCacheListener listener = CuratorCacheListener.builder()
                .forCreates(node -> {
                    byte[] data = node.getData();
                    if (ArrayUtils.isEmpty(data)) {
                        return;
                    }
                    ServiceInfo serviceInfo = decode(data);
                    for (ServiceInfoListener serviceInfoListener : listeners) {
                        serviceInfoListener.afterAddService(serviceInfo);
                    }
                })
                .forDeletes(node -> {
                    byte[] data = node.getData();
                    if (ArrayUtils.isEmpty(data)) {
                        return;
                    }
                    ServiceInfo serviceInfo = decode(data);
                    for (ServiceInfoListener serviceInfoListener : listeners) {
                        serviceInfoListener.afterRemoveService(serviceInfo);
                    }
                })
                .build();
        cache.listenable().addListener(listener);
        cache.start();
    }

    @Override
    public void destroy() {
        cache.close();
        client.close();
    }

    private byte[] encode(Object obj) {
        ByteBuf buffer = Unpooled.buffer();
        buffer.writerIndex(buffer.writerIndex() + OBJ_SIZE);
        byte[] bytes;
        try {
            codec.encode(buffer, obj);
            buffer.setInt(0, buffer.writerIndex() - OBJ_SIZE);
            bytes = buffer.array();
        } catch (Exception e) {
            throw new RPCException("编码失败", e);
        } finally {
            ReferenceCountUtil.release(buffer);
        }
        return bytes;
    }

    @SuppressWarnings("unchecked")
    private <T> T decode(byte[] bytes) {
        ByteBuf buffer = Unpooled.wrappedBuffer(bytes);
        int size = buffer.readInt();
        buffer.writerIndex(size + OBJ_SIZE);
        Object res;
        try {
            res = codec.decode(buffer);
        } catch (Exception e) {
            throw new RPCException("解码异常", e);
        } finally {
            ReferenceCountUtil.release(buffer);
        }
        return (T) res;
    }
}
