package org.Bun.netty.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.Bun.entity.RpcResponse;

/**
 * Netty客户端侧处理器
 *
 * @author 萌萌哒AI
 * @date 2024/07/30
 */
@Slf4j
public class NettyClientrHandler extends SimpleChannelInboundHandler<RpcResponse>
{
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse msg) throws Exception
    {
        try
        {
            log.info(String.format("客户端接收到消息: %s", msg));
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
            channelHandlerContext.attr(key).set(msg);
            channelHandlerContext.channel().close();
        }
        finally
        {
            ReferenceCountUtil.release(msg);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("过程调用时有错误发生:");
        ctx.close();
    }
}
