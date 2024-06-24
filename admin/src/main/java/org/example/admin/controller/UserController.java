package org.example.admin.controller;

import lombok.RequiredArgsConstructor;
import org.example.admin.common.convention.exception.ClientException;
import org.example.admin.common.convention.result.Result;
import org.example.admin.common.convention.result.Results;
import org.example.admin.common.enums.UserErrorCodeEnum;
import org.example.admin.dto.res.UserResDto;
import org.example.admin.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 根据用户名查询用户信息
     * @param username
     * @return
     */
    @GetMapping("/api/short-link/admin/v1/user/{username}")
    public Result<UserResDto> getUserByusername(@PathVariable("username") String username) {
        UserResDto userResDto=userService.getUserByusername(username);
        if (userResDto==null){
           return new Result<UserResDto>().setCode(UserErrorCodeEnum.USER_NULL.code()).setMessage(UserErrorCodeEnum.USER_NULL.message());
        }
        else {
           return Results.success(userResDto);
        }
    }
}
