package org.Bun.socket.client;

import lombok.extern.slf4j.Slf4j;
import org.Bun.RpcClient;
import org.Bun.entity.RpcRequest;
import org.Bun.entity.RpcResponse;
import org.Bun.enums.ResponseCode;
import org.Bun.enums.RpcError;
import org.Bun.exception.RpcException;
import org.Bun.netty.serializer.CommonSerializer;
import org.Bun.socket.ObjectReader;
import org.Bun.socket.ObjectWriter;
import org.Bun.utils.RpcMessageChecker;

import java.io.*;
import java.net.Socket;

/**
 * Socket方式远程方法调用的消费者（客户端）
 *
 * @author 萌萌哒AI
 * @date 2024/07/16
 */
@Slf4j
public class SocketRpcClient implements RpcClient
{
    private final String host;
    private final int port;
    private CommonSerializer serializer;

    public SocketRpcClient(String host, int port)
    {
        this.host = host;
        this.port = port;
    }
    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }


    @Override
    public Object sendRequest(RpcRequest rpcRequest)
    {
        if (serializer == null)
        {
            log.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        try (Socket socket = new Socket(host, port))
        {
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            ObjectWriter.writeObject(outputStream, rpcRequest, serializer);
            Object obj = ObjectReader.readObject(inputStream);
            RpcResponse rpcResponse = (RpcResponse) obj;

            if (rpcResponse == null || rpcResponse.getStatusCode() == null ||
                    rpcResponse.getStatusCode() != ResponseCode.SUCCESS.getCode())
            {
                log.error("服务调用失败，service：{}", rpcRequest.getInterfaceName());
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, "service:" + rpcRequest.getInterfaceName());
            }
            RpcMessageChecker.check(rpcRequest, rpcResponse);
            return rpcResponse.getData();
        } catch (IOException e)
        {
            log.error("调用时有错误发生：", e);
            throw new RpcException("服务调用失败: ", e);
        }

    }
}
