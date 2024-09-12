package org.Bun.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.Bun.RpcServer;
import org.Bun.enums.RpcError;
import org.Bun.exception.RpcException;
import org.Bun.hook.ShutdownHook;
import org.Bun.netty.serializer.CommonSerializer;
import org.Bun.netty.serializer.JsonSerializer;
import org.Bun.netty.serializer.KryoSerializer;
import org.Bun.provider.ServiceProvider;
import org.Bun.provider.ServiceProviderImpl;
import org.Bun.register.NacosServiceRegistry;
import org.Bun.register.ServiceRegistry;
import org.Bun.utils.CommonDecoder;
import org.Bun.utils.CommonEncoder;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;


/**
 * NIO方式服务提供侧
 * @author 萌萌哒AI
 * @date 2024/07/18
 */
@Slf4j
public class NettyRpcServer implements RpcServer
{
    private final String host;
    private final int port;

    private final ServiceRegistry serviceRegistry;
    private final ServiceProvider serviceProvider;
    private CommonSerializer serializer;

    public NettyRpcServer(String host, int port) {
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
    }
    @Override
    public void setSerializer(CommonSerializer serializer)
    {
        this.serializer = serializer;
    }

    @Override
    public <T> void publishService(T service, Class<T> serviceClass)
    {
        if(serializer == null)
        {
            log.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.addServiceProvider(service, serviceClass);
        serviceRegistry.register(serviceClass.getCanonicalName(),new InetSocketAddress(host, port));
        start();
    }

    @Override
    public void start()
    {
        //bossGroup 和 workerGroup 是两个线程池, 它们默认线程数为 CPU 核心数乘以 2，
        // bossGroup 用于接收客户端传过来的请求，接收到请求后将后续操作交由 workerGroup 处理。
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup workerGroup=new NioEventLoopGroup();

        try{
            //生成了一个服务启动辅助类的实例 bootstrap，boostrap 用来为 Netty 程序的启动组装配置一些必须要组件，例如上面的创建的两个线程组
            ServerBootstrap serverBootstrap=new ServerBootstrap();
            //channel 方法用于指定服务器端监听套接字通道 NioServerSocketChannel，其内部管理了一个 Java NIO 中的ServerSocketChannel实例。
            serverBootstrap.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))//为 ServerBootstrap 设置一个日志处理器，用于记录服务器的日志信息，日志级别为 INFO。
                    .option(ChannelOption.SO_BACKLOG, 256)//设置服务器套接字的选项，SO_BACKLOG 指定了内核为此套接字排队的最大连接数。
                    .childOption(ChannelOption.SO_KEEPALIVE, true)//设置 SO_KEEPALIVE 选项，启用 TCP 的 keep-alive 机制。
                    .childOption(ChannelOption.TCP_NODELAY, true)//设置子通道的选项，TCP_NODELAY 禁用 Nagle 算法，减少延迟。
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception
                        {
                            //数据从外部传入时需要解码，而传出时需要编码
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //用于检测空闲状态。这里设置的是读空闲时间为30秒，即如果30秒内没有写操作，就会触发空闲事件。
                            pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            pipeline.addLast(new CommonEncoder(serializer));
                            pipeline.addLast(new CommonDecoder());
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(host,port).sync();//接着我们调用了 bootstrap 的 bind 方法将服务绑定到 port 端口上
            ShutdownHook.getShutdownHook().addClearAllHook();//注册这个钩子,以便在JVM关闭时注销服务
            future.channel().closeFuture().sync();//应用程序将会阻塞等待直到服务器的 Channel 关闭。
        }
        catch(Exception e){
            log.error("启动服务器时有错误发生: ", e);
        }
        finally
        {
            // 优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
