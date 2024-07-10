package org.Bun;

import org.Bun.api.HelloServer;
import org.Bun.impl.HelloServerImpl;
import org.Bun.registry.DeaultServiceRegistry;
import org.Bun.registry.ServiceRegistry;
import org.Bun.server.RpcServer;

public class TestServer
{
    public static void main(String[] args)
    {
        ServiceRegistry serviceRegistry=new DeaultServiceRegistry();
        HelloServer helloServer = new HelloServerImpl();
        serviceRegistry.register(helloServer);
        RpcServer rpcServer= new RpcServer(serviceRegistry);
        rpcServer.start(helloServer,9000);

    }
}
