package org.Bun.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Package Type，标明这是一个调用请求还是调用响应
 * @author 萌萌哒AI
 * @date 2024/07/30
 */
@Getter
@AllArgsConstructor
public enum PackageType
{

    REQUEST_PACK(0),
    RESPONSE_PACK(1);

    private final int code;
}
