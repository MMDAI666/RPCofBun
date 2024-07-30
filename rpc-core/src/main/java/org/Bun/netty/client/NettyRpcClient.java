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
import org.Bun.netty.serializer.JsonSerializer;
import org.Bun.utils.CommonDecoder;
import org.Bun.utils.CommonEncoder;

@Slf4j
public class NettyRpcClient implements RpcClient
{
    private String host;
    private int port;
    private static final Bootstrap bootstrap;

    public NettyRpcClient(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    //在静态代码块中就直接配置好了 Netty 客户端，等待发送数据时启动，channel 将 RpcRequest 对象写出，并且等待服务端返回的结果。
    //注意这里的发送是非阻塞的，所以发送后会立刻返回，而无法得到结果。这里通过 AttributeKey 的方式阻塞获得返回结果：
    static {
        bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception
                    {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new CommonDecoder())
                                .addLast(new CommonEncoder(new JsonSerializer()))
                                .addLast(new NettyClientrHandler());
                    }
                });

    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest)
    {
        try
        {
            ChannelFuture future=bootstrap.connect(host, port).sync();
            log.info("客户端连接到服务器 {}:{}", host, port);
            Channel channel = future.channel();
            if (channel!=null)
            {
                channel.writeAndFlush(rpcRequest).addListener(future1 ->
                {
                    if (future1.isSuccess())log.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                    else log.error("发送消息时有错误发生: ", future1.cause());
                });
                channel.closeFuture().sync();
                //通过这种方式获得全局可见的返回结果，在获得返回结果 RpcResponse 后，将这个对象以 key 为 rpcResponse 放入 ChannelHandlerContext 中，
                // 这里就可以立刻获得结果并返回，我们会在 NettyClientHandler 中看到放入的过程。
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                RpcResponse rpcResponse = channel.attr(key).get();
                return rpcResponse.getData();
            }
        }
        catch (Exception e)
        {
            log.error("发送消息时有错误发生: ", e);
        }
        return null;
    }
}
