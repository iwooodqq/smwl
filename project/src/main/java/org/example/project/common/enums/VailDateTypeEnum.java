package org.example.project.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum VailDateTypeEnum {
    /**
     * 永久有效期
     */
    PERMANENT(0),
    /**
     * 自定义有效期
     */
    CUSTOM(1);
    @Getter
    private final int type;
}
