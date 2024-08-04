package org.Bun;

import org.Bun.netty.serializer.CommonSerializer;

public interface RpcServer
{
    void setSerializer(CommonSerializer serializer);

    void start(int port);
}
