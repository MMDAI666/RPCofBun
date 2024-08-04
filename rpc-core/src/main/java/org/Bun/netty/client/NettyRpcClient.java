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
import org.Bun.netty.serializer.CommonSerializer;
import org.Bun.netty.serializer.JsonSerializer;
import org.Bun.netty.serializer.KryoSerializer;
import org.Bun.register.NacosServiceRegistry;
import org.Bun.register.ServiceRegistry;
import org.Bun.utils.CommonDecoder;
import org.Bun.utils.CommonEncoder;
import org.Bun.utils.RpcMessageChecker;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class NettyRpcClient implements RpcClient
{
    private final ServiceRegistry serviceRegistry;

    private CommonSerializer serializer;
    private static final Bootstrap bootstrap;
    private static final EventLoopGroup group;//这个引用必须保存,否则EventLoopGroup线程无法关闭,client无法结束

    public NettyRpcClient()
    {
        serviceRegistry=new NacosServiceRegistry();
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    //在静态代码块中就直接配置好了 Netty 客户端，等待发送数据时启动，channel 将 RpcRequest 对象写出，并且等待服务端返回的结果。
    //注意这里的发送是非阻塞的，所以发送后会立刻返回，而无法得到结果。这里通过 AttributeKey 的方式阻塞获得返回结果：
    static {
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);

    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest)
    {
        if(serializer == null) {
            log.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        //AtomicReference 提供了一种线程安全的方式来更新和读取引用类型的变量，避免了使用传统的同步机制（如 synchronized 块）所带来的复杂性和性能开销。
        //Netty 的 ChannelFuture 和 AttributeKey 机制都是非阻塞的，这意味着在等待响应的过程中，可能会有多个线程同时访问和修改结果变量。
        // 使用 AtomicReference 可以确保这些操作是线程安全的，并且结果的更新是可见的。
        AtomicReference<Object> result = new AtomicReference<>(null);
        try
        {
            InetSocketAddress inetSocketAddress = serviceRegistry.lookupService(rpcRequest.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            if(channel.isActive())
            {
                channel.writeAndFlush(rpcRequest).addListener(future1 ->
                {
                    if (future1.isSuccess()) log.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                    else log.error("发送消息时有错误发生: ", future1.cause());
                });
                channel.closeFuture().sync();
                //通过这种方式获得全局可见的返回结果，在获得返回结果 RpcResponse 后，将这个对象以 key 为 rpcResponse 放入 ChannelHandlerContext 中，
                // 这里就可以立刻获得结果并返回，我们会在 NettyClientHandler 中看到放入的过程。
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse" + rpcRequest.getRequestId());
                RpcResponse rpcResponse = channel.attr(key).get();
                RpcMessageChecker.check(rpcRequest, rpcResponse);
                result.set(rpcResponse.getData());
            }
            else System.exit(0);
        }
        catch (Exception e)
        {
            log.error("发送消息时有错误发生: ", e);
        }
        finally
        {
            group.shutdownGracefully();
            ChannelProvider.getGroup().shutdownGracefully();//理同group
        }
        return result.get();
    }
}
