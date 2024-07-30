package org.Bun.utils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.slf4j.Slf4j;
import org.Bun.entity.RpcRequest;
import org.Bun.entity.RpcResponse;
import org.Bun.enums.PackageType;
import org.Bun.enums.RpcError;
import org.Bun.exception.RpcException;
import org.Bun.netty.serializer.CommonSerializer;

import java.util.List;

import static org.Bun.enums.ProtocolCode.MY_PROTOCOL_CODE;

/**
 * 通用的解码拦截器
 * @author 萌萌哒AI
 * @date 2024/07/19
 * +---------------+---------------+-----------------+-------------+
 * |  Magic Number |  Package Type | Serializer Type | Data Length |
 * |    4 bytes    |    4 bytes    |     4 bytes     |   4 bytes   |
 * +---------------+---------------+-----------------+-------------+
 * |                          Data Bytes                           |
 * |                   Length: ${Data Length}                      |
 * +---------------------------------------------------------------+
 * 首先是 4 字节魔数，表识一个协议包。接着是 Package Type，标明这是一个调用请求还是调用响应，
 * Serializer Type 标明了实际数据使用的序列化器，这个服务端和客户端应当使用统一标准；
 * Data Length 就是实际数据的长度，设置这个字段主要防止粘包，最后就是经过序列化后的实际数据，
 * 可能是 RpcRequest 也可能是 RpcResponse 经过序列化后的字节，取决于 Package Type。
 */
@Slf4j
public class CommonDecoder extends ReplayingDecoder
{
    private static final int MAGIC_NUMBER = MY_PROTOCOL_CODE.getCode();//协议包标识

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception
    {
        //校验协议包
        int magic = in.readInt();
        if(magic != MAGIC_NUMBER)
        {
            log.error("不识别的协议包: {}", magic);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }

        //确认数据包
        int packageCode  = in.readInt();
        Class<?> packageClass;
        if(packageCode == PackageType.REQUEST_PACK.getCode())packageClass= RpcRequest.class;
        else if(packageCode == PackageType.RESPONSE_PACK.getCode())packageClass= RpcResponse.class;
        else
        {
            log.error("不识别的数据包: {}", packageCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }

        //确认序列化器
        int serializerCode=in.readInt();
        CommonSerializer serializer=CommonSerializer.getByCode(serializerCode);
        if(serializer == null) {
            log.error("不识别的反序列化器: {}", serializerCode);
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        //解码数据
        int length=in.readInt();
        byte[] data=new byte[length];
        in.readBytes(data);
        Object obj = serializer.deserialize(data, packageClass);

        out.add(obj);
    }
}
