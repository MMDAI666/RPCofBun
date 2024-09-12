package org.Bun.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.Bun.RpcClient;
import org.Bun.entity.RpcRequest;
import org.Bun.entity.RpcResponse;
import org.Bun.enums.RpcError;
import org.Bun.exception.RpcException;
import org.Bun.loadbalancer.LoadBalancer;
import org.Bun.loadbalancer.RandomLoadBalancer;
import org.Bun.netty.serializer.CommonSerializer;
import org.Bun.netty.serializer.JsonSerializer;
import org.Bun.netty.serializer.KryoSerializer;
import org.Bun.register.NacosServiceDiscovery;
import org.Bun.register.NacosServiceRegistry;
import org.Bun.register.ServiceDiscovery;
import org.Bun.register.ServiceRegistry;
import org.Bun.utils.*;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class NettyRpcClient implements RpcClient
{
    private final ServiceDiscovery serviceDiscovery;

    private CommonSerializer serializer;
    private static final Bootstrap bootstrap;
    private static final EventLoopGroup group;//这个引用必须保存,否则EventLoopGroup线程无法关闭,client无法结束

    private final UnprocessedRequests unprocessedRequests;

    public NettyRpcClient()
    {
        this(DEFAULT_SERIALIZER, new RandomLoadBalancer());
    }
    public NettyRpcClient(LoadBalancer loadBalancer) {
        this(DEFAULT_SERIALIZER, loadBalancer);
    }

    public NettyRpcClient(Integer serializer)
    {
        this(serializer, new RandomLoadBalancer());
    }

    public NettyRpcClient(Integer serializer, LoadBalancer loadBalancer) {
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.serializer = CommonSerializer.getByCode(serializer);
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    //在静态代码块中就直接配置好了 Netty 客户端，等待发送数据时启动，channel 将 RpcRequest 对象写出，并且等待服务端返回的结果。
    //注意这里的发送是非阻塞的，所以发送后会立刻返回，而无法得到结果。这里通过 AttributeKey 的方式阻塞获得返回结果：
    static
    {
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class);

    }

    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest)
    {
        if (serializer == null)
        {
            log.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        //AtomicReference 提供了一种线程安全的方式来更新和读取引用类型的变量，避免了使用传统的同步机制（如 synchronized 块）所带来的复杂性和性能开销。
        //Netty 的 ChannelFuture 和 AttributeKey 机制都是非阻塞的，这意味着在等待响应的过程中，可能会有多个线程同时访问和修改结果变量。
        // 使用 AtomicReference 可以确保这些操作是线程安全的，并且结果的更新是可见的。
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        try
        {
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            if (!channel.isActive())
            {
                group.shutdownGracefully();
                return null;
            }

            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future1 ->
            {
                if (future1.isSuccess()) log.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                else
                {
                    future1.channel().close();
                    resultFuture.completeExceptionally(future1.cause());
                    log.error("发送消息时有错误发生: ", future1.cause());
                }
            } );
        } catch (Exception e)
        {
            log.error("发送消息时有错误发生: ", e);
            Thread.currentThread().interrupt();
        }
        return resultFuture;
    }
}
