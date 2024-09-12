package org.Bun.netty.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.Bun.entity.RpcRequest;
import org.Bun.entity.RpcResponse;
import org.Bun.netty.serializer.CommonSerializer;
import java.net.InetSocketAddress;

/**
 * Netty客户端侧处理器
 *
 * @author 萌萌哒AI
 * @date 2024/07/30
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse>
{
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse msg) throws Exception
    {
        try
        {
            log.info(String.format("客户端接收到消息: %s", msg));
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse" + msg.getRequestId());
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

    //空闲事件
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception
    {
        if (evt instanceof IdleStateEvent)
        {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                log.info("发送心跳包 [{}]", ctx.channel().remoteAddress());
                Channel channel = ChannelProvider.get((InetSocketAddress)ctx.channel().remoteAddress(),
                        CommonSerializer.getByCode(CommonSerializer.DEFAULT_SERIALIZER));
                RpcRequest request = new RpcRequest();
                request.setHeartBeat(true);
                channel.writeAndFlush(request).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        }
        else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
