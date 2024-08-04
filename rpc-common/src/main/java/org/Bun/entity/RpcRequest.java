package org.Bun.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @author bun
 * @date 2024/06/25
 * 返回类
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest<T> implements Serializable
{
    /**
     * 请求号
     */
    private String requestId;
    private String interfaceName;
    private String methodName;
    private Object[] parameters;//参数列表
    private Class<?>[] parameterTypes;//参数类型
}
