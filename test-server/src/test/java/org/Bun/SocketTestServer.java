package org.Bun;

import org.Bun.api.HelloServer;
import org.Bun.impl.HelloServerImpl;
import org.Bun.netty.serializer.JsonSerializer;
import org.Bun.registry.DeaultServiceRegistry;
import org.Bun.registry.ServiceRegistry;
import org.Bun.socket.server.SocketRpcServer;

public class SocketTestServer
{
    public static void main(String[] args)
    {
        ServiceRegistry serviceRegistry=new DeaultServiceRegistry();
        HelloServer helloServer = new HelloServerImpl();
        serviceRegistry.register(helloServer);
        RpcServer rpcServer= new SocketRpcServer(serviceRegistry);
        rpcServer.setSerializer(new JsonSerializer());
        rpcServer.start(9000);

    }
}
