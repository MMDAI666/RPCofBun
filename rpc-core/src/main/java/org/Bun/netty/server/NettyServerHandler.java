package org.Bun.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.Bun.entity.RpcResponse;

/**
 * Netty客户端侧数据处理器
 * @author 萌萌哒AI
 * @date 2024/07/19
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcResponse>
{
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception
    {

    }
}
