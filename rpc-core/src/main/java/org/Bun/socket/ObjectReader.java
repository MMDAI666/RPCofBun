package org.Bun.socket;

import lombok.extern.slf4j.Slf4j;
import org.Bun.entity.RpcRequest;
import org.Bun.entity.RpcResponse;
import org.Bun.enums.PackageType;
import org.Bun.enums.ProtocolCode;
import org.Bun.enums.RpcError;
import org.Bun.exception.RpcException;
import org.Bun.netty.serializer.CommonSerializer;

import java.io.IOException;
import java.io.InputStream;


@Slf4j
public class ObjectReader
{
    private static final int MAGIC_NUMBER = ProtocolCode.MY_PROTOCOL_CODE.getCode();
    public static Object readObject(InputStream in) throws IOException
    {
        byte[] numberBytes = new byte[4];

        in.read(numberBytes);
        int magic = bytesToInt(numberBytes);
        if (magic != MAGIC_NUMBER) {
            log.error("不识别的协议包: {}", magic);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }

        in.read(numberBytes);
        int packageCode=bytesToInt(numberBytes);
        Class<?> packageClass;
        if (packageCode == PackageType.REQUEST_PACK.getCode()) packageClass = RpcRequest.class;
        else if (packageCode == PackageType.RESPONSE_PACK.getCode()) packageClass = RpcResponse.class;
        else {
            log.error("不识别的数据包: {}", packageCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }

        in.read(numberBytes);
        int serializerCode=bytesToInt(numberBytes);
        CommonSerializer serializer=CommonSerializer.getByCode(serializerCode);
        if (serializer == null) {
            log.error("不识别的反序列化器: {}", serializerCode);
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }

        in.read(numberBytes);
        int length = bytesToInt(numberBytes);

        byte[] bytes = new byte[length];
        in.read(bytes);
        return serializer.deserialize(bytes, packageClass);
    }

    private static int bytesToInt(byte[] src)
    {
        int value;
        value = (src[0] & 0xFF)
                | ((src[1] & 0xFF)<<8)
                | ((src[2] & 0xFF)<<16)
                | ((src[3] & 0xFF)<<24);
        return value;
    }
}
