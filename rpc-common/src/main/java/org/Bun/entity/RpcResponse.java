package org.Bun.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.Bun.enums.ResponseCode;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class RpcResponse<T> implements Serializable
{
    /**
     * 响应对应的请求号
     */
    private String requestId;
    private Integer statusCode;
    private String message;
    private T data;

    public RpcResponse() {}

    public RpcResponse(String message)
    {
        this.message = message;
    }

    public static <T> RpcResponse<T> success(T data,String requestId)
    {
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setRequestId(requestId);
        response.setData(data);
        return response;
    }

    public static <T> RpcResponse<T> fail(ResponseCode code, String requestId)
    {
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(code.getCode());
        response.setRequestId(requestId);
        response.setMessage(code.getMessage());
        return response;
    }
}
