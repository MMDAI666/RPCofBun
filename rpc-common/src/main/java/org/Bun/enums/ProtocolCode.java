package org.Bun.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 协议包标识
 * @author 萌萌哒AI
 * @date 2024/07/30
 */
@Getter
@AllArgsConstructor
public enum ProtocolCode
{
    MY_PROTOCOL_CODE(0xCAFEBABE);
    private final int code;
}
