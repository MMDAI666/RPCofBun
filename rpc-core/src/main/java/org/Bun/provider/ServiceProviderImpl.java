package org.Bun.provider;

import lombok.extern.slf4j.Slf4j;
import org.Bun.enums.RpcError;
import org.Bun.exception.RpcException;


import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 默认服务注册器
 * 一个接口只能有一个对象提供服务
 * @author bun
 * @date 2024/07/09
 */
@Slf4j
public class ServiceProviderImpl implements ServiceProvider
{
    //接口到服务的映射
    private static final Map<String,Object> serviceMap = new ConcurrentHashMap<>();//使用ConcurrentHashMap，保证线程安全
    //  创建一个Set，用于存储已注册的服务名称。
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();//使用线程安全的set

    @Override
    public synchronized <T> void addServiceProvider(T service)
    {
        String serviceName = service.getClass().getCanonicalName();
        if(registeredService.contains(serviceName))return;
        registeredService.add(serviceName);
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if(interfaces.length == 0){
            throw new RpcException(RpcError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }
        for (Class<?> anInterface : interfaces) {
            serviceMap.put(anInterface.getCanonicalName(), service);
        }
        log.info("向接口: {} 注册服务: {}", Arrays.toString(interfaces), serviceName);

    }

    @Override
    public Object getServiceProvider(String serviceName)
    {
        Object service = serviceMap.get(serviceName);
        if (service == null) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
