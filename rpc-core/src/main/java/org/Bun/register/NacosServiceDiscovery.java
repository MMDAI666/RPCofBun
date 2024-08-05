package org.Bun.register;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import org.Bun.utils.NacosUtil;

import java.net.InetSocketAddress;
import java.util.List;
@Slf4j
public class NacosServiceDiscovery implements ServiceDiscovery
{
    private final NamingService namingService;
    public NacosServiceDiscovery()
    {
        this.namingService = NacosUtil.getNacosNamingService();
    }
    @Override
    public InetSocketAddress lookupService(String serviceName)
    {
        try
        {
            List<Instance> instances =  NacosUtil.getAllInstance(serviceName);
            if(instances.isEmpty())throw new RuntimeException();
            return new InetSocketAddress(instances.get(0).getIp(),instances.get(0).getPort());
        } catch (RuntimeException | NacosException e)
        {
            log.error("获取服务时有错误发生:", e);
            throw new RuntimeException(e);
        }
    }
}
