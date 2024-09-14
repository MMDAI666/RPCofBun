package org.Bun;

import lombok.extern.slf4j.Slf4j;
import org.Bun.entity.RpcRequest;
import org.Bun.entity.RpcResponse;
import org.Bun.netty.client.NettyRpcClient;
import org.Bun.socket.client.SocketRpcClient;
import org.Bun.utils.RpcMessageChecker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
public class RpcClientProxy implements InvocationHandler
{
    private final RpcClient client;

    public RpcClientProxy(RpcClient client) {
        this.client = client;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz)
    {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    //todo 整合熔断器
    @Override
    @SuppressWarnings("unchecked")
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        log.info("调用方法: {}#{}", method.getDeclaringClass().getName(), method.getName());
        RpcRequest rpcRequest = new RpcRequest(UUID.randomUUID().toString(),method.getDeclaringClass().getName(),
                method.getName(), args, method.getParameterTypes(),false);
        RpcResponse rpcResponse = null;
        if(client instanceof NettyRpcClient)
        {
            CompletableFuture<RpcResponse> completableFuture  = (CompletableFuture<RpcResponse>) client.sendRequest(rpcRequest);
            try {
                rpcResponse = completableFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("方法调用请求发送失败", e);
                return null;
            }
        }

        if (client instanceof SocketRpcClient) {
            rpcResponse = (RpcResponse) client.sendRequest(rpcRequest);
        }

        RpcMessageChecker.check(rpcRequest, rpcResponse);
        return  rpcResponse.getData();

    }

}
