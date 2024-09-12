package org.Bun;

import org.Bun.api.HelloServer;
import org.Bun.impl.HelloServerImpl;
import org.Bun.impl.HelloServerImpl2;
import org.Bun.netty.serializer.JsonSerializer;
import org.Bun.provider.ServiceProviderImpl;
import org.Bun.provider.ServiceProvider;
import org.Bun.socket.server.SocketRpcServer;

public class SocketTestServer
{
    public static void main(String[] args)
    {

        HelloServer helloServer = new HelloServerImpl2();
        SocketRpcServer server = new SocketRpcServer("127.0.0.1", 9998);

        server.publishService(helloServer,HelloServer.class);

    }
}
