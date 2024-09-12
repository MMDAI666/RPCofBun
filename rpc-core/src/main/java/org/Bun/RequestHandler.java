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
    public Object handle(RpcRequest request)
    {
        Object service = serviceProvider.getServiceProvider(request.getInterfaceName());
        return invokeTargetMethod(request, service);
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service)
    {
        Object result;
        try
        {
            Method method=service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParameterTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            log.info("服务:{} 成功调用方法:{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException e)
        {
            return RpcResponse.fail(ResponseCode.NOT_FOUND_METHOD,rpcRequest.getRequestId());
        } catch (InvocationTargetException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        return result;
    }
}
