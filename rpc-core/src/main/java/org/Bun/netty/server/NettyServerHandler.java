package org.Bun.netty.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.Bun.RequestHandler;
import org.Bun.entity.RpcRequest;
import org.Bun.entity.RpcResponse;
import org.Bun.registry.DeaultServiceRegistry;
import org.Bun.registry.ServiceRegistry;

/**
 * Netty中处理RpcRequest的Handler
 * 用于接收 RpcRequest，并且执行调用，将调用结果返回封装成 RpcResponse 发送出去。
 * @author 萌萌哒AI
 * @date 2024/07/19
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest>
{
    private static RequestHandler requestHandler;
    private static ServiceRegistry serviceRegistry;

    static
    {
        requestHandler = new RequestHandler();
        serviceRegistry=new DeaultServiceRegistry();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest msg) throws Exception
    {
        try
        {
            log.info("服务器接收到请求: {}", msg);
            String interfaceName = msg.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object result = requestHandler.handler(msg, service);
            //将返回结果放入channelHandlerContext中
            ChannelFuture future = channelHandlerContext.writeAndFlush(RpcResponse.success(result));
            future.addListener(ChannelFutureListener.CLOSE);
        }
        finally
        {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("处理过程调用时有错误发生:");
        ctx.close();
    }
}
