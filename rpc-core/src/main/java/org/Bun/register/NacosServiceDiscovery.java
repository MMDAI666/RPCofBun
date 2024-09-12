package org.Bun.register;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import org.Bun.loadbalancer.LoadBalancer;
import org.Bun.loadbalancer.RandomLoadBalancer;
import org.Bun.utils.NacosUtil;

import java.net.InetSocketAddress;
import java.util.List;
@Slf4j
public class NacosServiceDiscovery implements ServiceDiscovery
{
    private  LoadBalancer loadBalancer;


    public NacosServiceDiscovery(LoadBalancer loadBalancer) {
        if(loadBalancer == null) this.loadBalancer = new RandomLoadBalancer();
        else this.loadBalancer = loadBalancer;
    }

    public void setLoadBalancer(LoadBalancer loadBalancer)
    {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public InetSocketAddress lookupService(String serviceName)
    {
        try
        {
            List<Instance> instances =  NacosUtil.getAllInstance(serviceName);
            if(instances.isEmpty())throw new RuntimeException();
            Instance  instance = loadBalancer.select(instances);
            return new InetSocketAddress(instance.getIp(),instance.getPort());
        } catch (RuntimeException | NacosException e)
        {
            log.error("获取服务时有错误发生:", e);
            throw new RuntimeException(e);
        }
    }
}
