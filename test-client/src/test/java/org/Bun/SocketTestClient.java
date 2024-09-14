package org.Bun;

import org.Bun.api.HelloServer;
import org.Bun.domain.HelloObject;
import org.Bun.loadbalancer.ConsistentHashLoadBalance;
import org.Bun.netty.serializer.JsonSerializer;
import org.Bun.socket.client.SocketRpcClient;

public class SocketTestClient {
    public static void main(String[] args) {

        RpcClient socketRpcClient=new SocketRpcClient(new ConsistentHashLoadBalance());
        RpcClientProxy proxy = new RpcClientProxy(socketRpcClient);
        HelloServer helloService = proxy.getProxy(HelloServer.class);
        HelloObject object = new HelloObject(1, "Hello,World!");
        for(int i = 0; i < 20; i ++) {
            String res = helloService.hello(object);
            System.out.println(i+":"+res);
        }
    }
}