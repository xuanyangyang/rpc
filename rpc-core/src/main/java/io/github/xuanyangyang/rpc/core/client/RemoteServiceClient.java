package io.github.xuanyangyang.rpc.core.client;

import io.github.xuanyangyang.rpc.core.net.Client;
import io.github.xuanyangyang.rpc.core.service.ServiceInfo;

/**
 * 远程服务客户端
 *
 * @author xuanyangyang
 * @since 2020/11/4 20:34
 */
public interface RemoteServiceClient {
     ServiceInfo getServiceInfo();

     Client getClient();
}
