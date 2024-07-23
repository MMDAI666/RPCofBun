package org.Bun.socket.server;

import lombok.extern.slf4j.Slf4j;
import org.Bun.RequestHandler;
import org.Bun.RpcServer;
import org.Bun.registry.ServiceRegistry;

import java.io.IOException;
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
    private final ExecutorService threadPool;
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 50;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;

    private RequestHandler requestHandler = new RequestHandler();
    private final ServiceRegistry serviceRegistry;

    public SocketRpcServer(ServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);

        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                workingQueue, Executors.defaultThreadFactory());

    }

    @Override
    public void start(int port)
    {
        try (ServerSocket serverSocket = new ServerSocket(port))
        {
            log.info("服务器正在启动...");
            Socket socket;
            while ((socket = serverSocket.accept()) != null)
            {
                log.info("消费者连接: {}:{}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new RequestHandlerThread(socket, requestHandler, serviceRegistry));
            }
        } catch (IOException e)
        {
            log.error("服务器启动时有错误发生:", e);
        }
    }

}
