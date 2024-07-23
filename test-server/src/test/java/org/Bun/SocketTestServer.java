package org.Bun;

import org.Bun.api.HelloServer;
import org.Bun.impl.HelloServerImpl;
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
        SocketRpcServer rpcServer= new SocketRpcServer(serviceRegistry);
        rpcServer.start(9000);

    }
}
