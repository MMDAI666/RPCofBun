package org.Bun;

import org.Bun.api.HelloServer;
import org.Bun.domain.HelloObject;
import org.Bun.netty.serializer.JsonSerializer;
import org.Bun.socket.client.SocketRpcClient;

public class SocketTestClient {
    public static void main(String[] args) {

        RpcClient socketRpcClient=new SocketRpcClient("127.0.0.1", 9000);
        socketRpcClient.setSerializer(new JsonSerializer());
        RpcClientProxy proxy = new RpcClientProxy(socketRpcClient);
        HelloServer helloService = proxy.getProxy(HelloServer.class);
        HelloObject object = new HelloObject(1, "Hello,World!");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}