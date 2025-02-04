package org.example.admin.dto.res;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.example.admin.common.serialize.PhoneDesensitizationSerializer;

import java.util.Date;

@Data
public class UserResDto {
    /**
     * id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;
    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    @JsonSerialize(using = PhoneDesensitizationSerializer.class)
    private String phone;

    /**
     * 邮箱
     */
    private String mail;

}
