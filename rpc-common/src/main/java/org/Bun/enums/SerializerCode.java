package org.Bun.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字节流中标识序列化和反序列化器
 * @author 萌萌哒AI
 * @date 2024/07/30
 */
@Getter
@AllArgsConstructor
public enum SerializerCode
{
    JSON(1);
    private final int code;
}
