package org.Bun.codeProcess;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 通用的编码拦截器
 * @author 萌萌哒AI
 * @date 2024/07/19
 */
public class CommonEncoder extends MessageToByteEncoder
{
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception
    {

    }
}
