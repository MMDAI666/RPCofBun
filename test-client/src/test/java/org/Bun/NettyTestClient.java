package org.Bun;

import org.Bun.api.HelloServer;
import org.Bun.domain.HelloObject;
import org.Bun.netty.client.NettyRpcClient;
import org.Bun.netty.serializer.JsonSerializer;

public class NettyTestClient
{
    public static void main(String[] args)
    {
        RpcClient client=new NettyRpcClient("127.0.0.1",9999);
        client.setSerializer(new JsonSerializer());
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloServer helloServer=proxy.getProxy(HelloServer.class);
        HelloObject helloObject=new HelloObject(12,"This is a Netty message");
        String hello = helloServer.hello(helloObject);
        System.out.println(hello);
    }
}
