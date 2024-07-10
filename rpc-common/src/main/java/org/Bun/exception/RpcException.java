package org.Bun.exception;

import org.Bun.enums.RpcError;

/**
 * rpc异常
 * @author bun
 * @date 2024/07/09
 */
public class RpcException extends RuntimeException
{
    public RpcException(RpcError error, String detail) {
        super(error.getMessage() + ": " + detail);
    }
    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
    public RpcException(RpcError error) {
        super(error.getMessage());
    }

}
