package org.Bun;

import org.Bun.api.HelloServer;
import org.Bun.domain.HelloObject;
import org.Bun.netty.client.NettyRpcClient;
import org.Bun.netty.serializer.JsonSerializer;

public class NettyTestClient
{
    public static void main(String[] args)
    {
        RpcClient client = new NettyRpcClient();
        client.setSerializer(new JsonSerializer());
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloServer helloService = rpcClientProxy.getProxy(HelloServer.class);
        HelloObject object = new HelloObject(12, "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
