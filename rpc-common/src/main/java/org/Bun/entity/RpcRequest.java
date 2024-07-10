package org.Bun.entity;


import lombok.Builder;
import lombok.Data;

import java.io.Serializable;


/**
 * @author bun
 * @date 2024/06/25
 * 返回类
 */
@Builder
@Data
public class RpcRequest<T> implements Serializable
{
    private String interfaceName;
    private String methodName;
    private Object[] parameters;//参数列表
    private Class<?>[] parameterTypes;//参数类型
}
