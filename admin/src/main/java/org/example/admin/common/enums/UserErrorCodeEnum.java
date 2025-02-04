package org.example.admin.common.enums;

import org.example.admin.common.convention.errorcode.IErrorCode;

public enum UserErrorCodeEnum implements IErrorCode {
    USER_TOKEN_FALL("A000200","用户token验证失败"),
    USER_NULL("B000200","用户记录不存在"),
    USER_EXIST("B000201","用户记录已存在"),
    USER_SAVE_ERROR("B000202","用户保存失败");
    private final String code;

    private final String message;

    UserErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
