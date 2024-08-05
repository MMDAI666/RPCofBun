package org.Bun.socket.server;

import lombok.extern.slf4j.Slf4j;
import org.Bun.RequestHandler;
import org.Bun.RpcServer;
import org.Bun.enums.RpcError;
import org.Bun.exception.RpcException;
import org.Bun.hook.ShutdownHook;
import org.Bun.netty.serializer.CommonSerializer;
import org.Bun.provider.ServiceProvider;
import org.Bun.provider.ServiceProviderImpl;
import org.Bun.register.NacosServiceRegistry;
import org.Bun.register.ServiceRegistry;
import org.Bun.utils.ThreadPoolFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Socket方式远程方法调用的提供者（服务端）
 * @author 萌萌哒AI
 * @date 2024/07/10
 */
@Slf4j
public class SocketRpcServer implements RpcServer
{

    private RequestHandler requestHandler = new RequestHandler();
    private final ExecutorService threadPool;;
    private final String host;
    private final int port;
    private CommonSerializer serializer;
    private final ServiceRegistry serviceRegistry;
    private final ServiceProvider serviceProvider;

    public SocketRpcServer(String host, int port) {
        this.host = host;
        this.port = port;
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
        this.serviceRegistry = new NacosServiceRegistry();
        this.serviceProvider = new ServiceProviderImpl();
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public <T> void publishService(T service, Class<T> serviceClass)
    {
        if(serializer == null) {
            log.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.addServiceProvider(service,serviceClass);
        serviceRegistry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
        start();
    }
    @Override
    public void start()
    {
        try (ServerSocket serverSocket = new ServerSocket())
        {
            serverSocket.bind(new InetSocketAddress(host, port));
            log.info("服务器正在启动...");
            ShutdownHook.getShutdownHook().addClearAllHook();//注册钩子
            Socket socket;
            while ((socket = serverSocket.accept()) != null)
            {
                log.info("消费者连接: {}:{}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new RequestHandlerThread(socket, requestHandler, serviceProvider,serializer));
            }
        } catch (IOException e)
        {
            log.error("服务器启动时有错误发生:", e);
        }
    }

}
