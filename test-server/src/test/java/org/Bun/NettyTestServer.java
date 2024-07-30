package org.Bun;

import org.Bun.api.HelloServer;
import org.Bun.impl.HelloServerImpl;
import org.Bun.netty.server.NettyRpcServer;
import org.Bun.registry.DeaultServiceRegistry;
import org.Bun.registry.ServiceRegistry;

public class NettyTestServer
{
    public static void main(String[] args)
    {
        HelloServer helloServer= new HelloServerImpl();
        ServiceRegistry serviceRegistry=new DeaultServiceRegistry();
        serviceRegistry.register(helloServer);
        RpcServer server=new NettyRpcServer();
        server.start(9999);
    }
}
