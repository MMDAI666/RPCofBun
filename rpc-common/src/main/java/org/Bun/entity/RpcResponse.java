package org.Bun.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.Bun.enums.ResponseCode;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class RpcResponse<T> implements Serializable
{
    private Integer statusCode;
    private String message;
    private T data;

    public RpcResponse() {}

    public RpcResponse(String message)
    {
        this.message = message;
    }

    public static <T> RpcResponse<T> success(T data)
    {
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setData(data);
        return response;
    }

    public static <T> RpcResponse<T> fail(ResponseCode code)
    {
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(code.getCode());
        response.setMessage(code.getMessage());
        return response;
    }
}
