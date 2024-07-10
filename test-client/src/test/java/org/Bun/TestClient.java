package org.Bun;

import org.Bun.client.RpcClientProxy;
import org.Bun.api.HelloServer;
import org.Bun.domain.HelloObject;

public class TestClient {
    public static void main(String[] args) {
        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1", 9000);
        HelloServer helloService = proxy.getProxy(HelloServer.class);
        HelloObject object = new HelloObject(1, "Hello,World!");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}