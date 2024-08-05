package org.Bun.register;

import java.net.InetSocketAddress;

/**
 * 服务发现接口
 * @author 萌萌哒AI
 * @date 2024/08/05
 */
public interface ServiceDiscovery
{
    /**
     * 根据服务名称获取服务实体
     * @param serviceName 服务名称
     * @return 服务实体
     */
    InetSocketAddress lookupService(String serviceName);
}
