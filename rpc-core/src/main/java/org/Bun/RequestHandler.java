package org.Bun;

import lombok.extern.slf4j.Slf4j;
import org.Bun.entity.RpcRequest;
import org.Bun.entity.RpcResponse;
import org.Bun.enums.ResponseCode;
import org.Bun.provider.ServiceProvider;
import org.Bun.provider.ServiceProviderImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 接受请求和服务,进行反射调用
 * @author 萌萌哒AI
 * @date 2024/07/10
 */
@Slf4j
public class RequestHandler
{
    private static final ServiceProvider serviceProvider;
    static {
        serviceProvider = new ServiceProviderImpl();
    }
    public Object handler(RpcRequest request)
    {
        Object result = null;
        Object service = serviceProvider.getServiceProvider(request.getInterfaceName());
        try
        {
            result=invokeTargetMethod(request,service);
            log.info("服务:{}调用方法:{}",request.getInterfaceName(),request.getMethodName());
        } catch (InvocationTargetException | IllegalAccessException e)
        {
            log.error("调用或发送时有错误发生",e);
        }
        return result;
    }
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) throws IllegalAccessException, InvocationTargetException
    {
        Method methods;
        try
        {
            methods=service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParameterTypes());
        } catch (NoSuchMethodException e)
        {
            return RpcResponse.fail(ResponseCode.NOT_FOUND_METHOD,rpcRequest.getRequestId());
        }
        return methods.invoke(service,rpcRequest.getParameters());
    }
}
