package org.Bun.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

/**
 * 随机
 * @author 萌萌哒AI
 * @date 2024/09/13
 */
public class RandomLoadBalancer implements LoadBalancer
{
    @Override
    public Instance select(List<Instance> instances)
    {
        return instances.get(new Random().nextInt(instances.size()));
    }
}
