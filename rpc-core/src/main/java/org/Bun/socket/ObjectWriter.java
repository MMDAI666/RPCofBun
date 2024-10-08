package org.Bun.socket;

import org.Bun.entity.RpcRequest;
import org.Bun.enums.PackageType;
import org.Bun.enums.ProtocolCode;
import org.Bun.netty.serializer.CommonSerializer;

import java.io.IOException;
import java.io.OutputStream;

public class ObjectWriter
{
    private static final int MAGIC_NUMBER = ProtocolCode.MY_PROTOCOL_CODE.getCode();
    public static void writeObject(OutputStream outputStream, Object object, CommonSerializer serializer) throws IOException
    {
        outputStream.write(intToBytes(MAGIC_NUMBER));

        if (object instanceof RpcRequest) {
            outputStream.write(intToBytes(PackageType.REQUEST_PACK.getCode()));
        } else {
            outputStream.write(intToBytes(PackageType.RESPONSE_PACK.getCode()));
        }

        outputStream.write(intToBytes(serializer.getCode()));
        byte[] bytes = serializer.serialize(object);
        outputStream.write(intToBytes(bytes.length));
        outputStream.write(bytes);
        outputStream.flush();
    }

    private static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value>>24) & 0xFF);
        src[1] = (byte) ((value>>16)& 0xFF);
        src[2] = (byte) ((value>>8)&0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }
}
