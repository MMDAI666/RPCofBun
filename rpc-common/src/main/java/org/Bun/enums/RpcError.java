package org.Bun.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * rpc错误信息
 *
 * @author bun
 * @date 2024/07/09
 */
@Getter
@AllArgsConstructor
public enum RpcError
{

    CLIENT_CONNECT_SERVER_FAILURE("客户端连接服务端失败"),

    FAILED_TO_CONNECT_TO_SERVICE_REGISTRY("连接注册中心失败"),
    REGISTER_SERVICE_FAILED("注册服务失败"),

    SERVICE_INVOCATION_FAILURE("服务调用出现失败"),
    SERVICE_CAN_NOT_BE_NULL("注册的服务不得为空"),
    SERVICE_NOT_FOUND("找不到对应的服务"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("注册的服务未实现接口"),

    UNKNOWN_PROTOCOL("不识别的协议包"),
    UNKNOWN_SERIALIZER("不识别的(反)序列化器"),
    UNKNOWN_PACKAGE_TYPE("不识别的数据包类型"),

    RESPONSE_NOT_MATCH("响应与请求号不匹配"),
    SERIALIZER_NOT_FOUND("找不到序列化器");

    private final String message;

}
