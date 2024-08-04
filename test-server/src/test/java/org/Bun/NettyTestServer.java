package org.Bun;

import org.Bun.api.HelloServer;
import org.Bun.impl.HelloServerImpl;
import org.Bun.netty.serializer.JsonSerializer;
import org.Bun.netty.server.NettyRpcServer;
import org.Bun.provider.ServiceProviderImpl;
import org.Bun.provider.ServiceProvider;

public class NettyTestServer
{
    public static void main(String[] args)
    {
        HelloServer helloServer= new HelloServerImpl();
        NettyRpcServer server = new NettyRpcServer("127.0.0.1", 9999);
        server.setSerializer(new JsonSerializer());
        server.publishService(helloServer,HelloServer.class);
    }
}
