package org.Bun;

import org.Bun.entity.RpcRequest;
import org.Bun.netty.serializer.CommonSerializer;

public interface RpcClient
{
    void setSerializer(CommonSerializer serializer);

    Object sendRequest(RpcRequest rpcRequest);

}
