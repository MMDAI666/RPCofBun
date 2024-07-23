package org.Bun.codeProcess;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * 通用的解码拦截器
 * @author 萌萌哒AI
 * @date 2024/07/19
 */
public class CommonDecoder extends ReplayingDecoder
{
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception
    {

    }
}
