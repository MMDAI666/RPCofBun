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

        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloServer helloService = rpcClientProxy.getProxy(HelloServer.class);
        HelloObject object = new HelloObject(12, "This is a message");
        for(int i = 0; i < 20; i ++) {
            String res = helloService.hello(object);
            System.out.println(i+":"+res);
        }
    }
}
