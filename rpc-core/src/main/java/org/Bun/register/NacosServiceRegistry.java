package org.Bun.register;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import org.Bun.enums.RpcError;
import org.Bun.exception.RpcException;
import org.Bun.utils.NacosUtil;

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
    private final NamingService namingService;

    public NacosServiceRegistry()
    {
        namingService= NacosUtil.getNacosNamingService();
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress)
    {
        try
        {
            NacosUtil.registerService(namingService, serviceName, inetSocketAddress);
        }
        catch (NacosException e)
        {
            log.error("注册服务时有错误发生:", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

}
