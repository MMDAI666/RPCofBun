package org.Bun;

import org.Bun.entity.RpcRequest;

public interface RpcClient
{
    Object sendRequest(RpcRequest rpcRequest);
}
