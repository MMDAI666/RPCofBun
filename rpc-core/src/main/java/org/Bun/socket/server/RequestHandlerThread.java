package org.Bun.socket.server;

import lombok.extern.slf4j.Slf4j;
import org.Bun.RequestHandler;
import org.Bun.entity.RpcRequest;
import org.Bun.entity.RpcResponse;
import org.Bun.netty.serializer.CommonSerializer;
import org.Bun.registry.ServiceRegistry;
import org.Bun.socket.ObjectReader;
import org.Bun.socket.ObjectWriter;

import java.io.*;
import java.net.Socket;

/**
 * 请求处理线程
 * 从ServiceRegistry 获取到提供服务的对象后，就会把 RpcRequest 和服务对象直接交给 RequestHandler 去处理
 * @author 萌萌哒AI
 * @date 2024/07/10
 */
@Slf4j
public class RequestHandlerThread implements Runnable
{
    private Socket socket;
    private RequestHandler requestHandler;
    private ServiceRegistry serviceRegistry;
    private CommonSerializer serializer;

    public RequestHandlerThread(Socket socket, RequestHandler requestHandler, ServiceRegistry serviceRegistry, CommonSerializer serializer)
    {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serviceRegistry = serviceRegistry;
        this.serializer = serializer;
    }

    @Override
    public void run()
    {
        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {
            RpcRequest rpcRequest = (RpcRequest) ObjectReader.readObject(inputStream);
            String interfaceName=rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object result=requestHandler.handler(rpcRequest,service);
            RpcResponse<Object> response = RpcResponse.success(result,rpcRequest.getRequestId());
            ObjectWriter.writeObject(outputStream, response, serializer);

        } catch (IOException e)
        {
            log.error("调用或发送时有错误发生：", e);
        }
    }
}
