package org.Bun;

import org.Bun.entity.RpcRequest;
import org.Bun.netty.serializer.CommonSerializer;

public interface RpcClient
{
    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    Object sendRequest(RpcRequest rpcRequest);

}
