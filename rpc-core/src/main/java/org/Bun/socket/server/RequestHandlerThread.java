package org.Bun.socket.server;

import org.Bun.RequestHandler;
import org.Bun.entity.RpcRequest;
import org.Bun.entity.RpcResponse;
import org.Bun.registry.ServiceRegistry;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 请求处理线程
 * 从ServiceRegistry 获取到提供服务的对象后，就会把 RpcRequest 和服务对象直接交给 RequestHandler 去处理
 * @author 萌萌哒AI
 * @date 2024/07/10
 */
public class RequestHandlerThread implements Runnable
{
    private Socket socket;
    private RequestHandler requestHandler;
    private ServiceRegistry serviceRegistry;

    public RequestHandlerThread(Socket socket, RequestHandler requestHandler, ServiceRegistry serviceRegistry)
    {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void run()
    {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream()))
        {
            RpcRequest rpcRequest=(RpcRequest) in.readObject();
            String interfaceName=rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object reslut=requestHandler.handler(rpcRequest,service);
            out.writeObject(RpcResponse.success(reslut));
            out.flush();

        } catch (IOException | ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }
}
