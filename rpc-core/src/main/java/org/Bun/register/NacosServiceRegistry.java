package org.Bun.register;

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
 *  Nacos服务注册中心
 * @author 萌萌哒AI
 * @date 2024/08/04
 */
@Slf4j
public class NacosServiceRegistry implements ServiceRegistry
{
    private static final String SERVER_ADDR = "127.0.0.1:8848";
    private static final NamingService namingService;
    static {
        try {
            namingService = NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e) {
            log.error("连接到Nacos时有错误发生: ", e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress)
    {
        try
        {
            namingService.registerInstance(serviceName,inetSocketAddress.getHostName(),inetSocketAddress.getPort());
        }
        catch (NacosException e)
        {
            log.error("注册服务时有错误发生:", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

    @Override
    public InetSocketAddress lookupService(String serviceName)
    {
        try
        {
            List<Instance> instances = namingService.getAllInstances(serviceName);
            if(instances.isEmpty())throw new RuntimeException();
            return new InetSocketAddress(instances.get(0).getIp(),instances.get(0).getPort());
        } catch (RuntimeException | NacosException e)
        {
            log.error("获取服务时有错误发生:", e);
            throw new RuntimeException(e);
        }
    }
}
