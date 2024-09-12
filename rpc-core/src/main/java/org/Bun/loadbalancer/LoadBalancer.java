package org.Bun.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * 负载均衡接口
 * @author 萌萌哒AI
 * @date 2024/09/13
 */
public interface LoadBalancer {

    Instance select(List<Instance> instances);

}
