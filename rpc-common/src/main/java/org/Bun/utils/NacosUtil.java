package org.Bun.utils;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import org.Bun.enums.RpcError;
import org.Bun.exception.RpcException;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 管理Nacos连接等工具类
 * @author 萌萌哒AI
 * @date 2024/08/05
 */
@Slf4j
public class NacosUtil
{
    private static final String SERVER_ADDR = "127.0.0.1:8848";

    public static NamingService getNacosNamingService() {
        try {
            return NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e) {
            log.error("连接到Nacos时有错误发生: ", e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    public static void registerService(NamingService namingService, String serviceName, InetSocketAddress address) throws NacosException {
        namingService.registerInstance(serviceName, address.getHostName(), address.getPort());
    }

    public static List<Instance> getAllInstance(NamingService namingService, String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }

}
