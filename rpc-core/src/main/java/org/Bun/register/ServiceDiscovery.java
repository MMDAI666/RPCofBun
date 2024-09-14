package org.Bun.register;

import org.Bun.entity.RpcRequest;

import java.net.InetSocketAddress;

/**
 * 服务发现接口
 * @author 萌萌哒AI
 * @date 2024/08/05
 */
public interface ServiceDiscovery
{
    /**
     * 根据请求获取服务实体
     *
     * @param rpcRequest 请求
     * @return 服务实体
     */
    InetSocketAddress lookupService(RpcRequest rpcRequest);
}
