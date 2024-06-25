package org.example.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.example.admin.common.convention.result.Result;
import org.example.admin.common.convention.result.Results;
import org.example.admin.dto.req.UserLoginReqDTO;
import org.example.admin.dto.req.UserRegisterReqDTO;
import org.example.admin.dto.req.UserUpdateReqDTO;
import org.example.admin.dto.res.UserLoginResDto;
import org.example.admin.dto.res.UserResDto;
import org.example.admin.dto.res.UserResTrueDto;
import org.example.admin.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 根据用户名查询用户信息
     */
    @GetMapping("/api/short-link/admin/v1/user/{username}")
    public Result<UserResDto> getUserByusername(@PathVariable("username") String username) {
        UserResDto userResDto=userService.getUserByusername(username);
           return Results.success(userResDto);
        }
    /**
     * 根据用户名查询用户真实信息
     */
    @GetMapping("/api/short-link/admin/v1/true/user/{username}")
    public Result<UserResTrueDto> getTrueUserByusername(@PathVariable("username") String username) {
        UserResDto userResDto=userService.getUserByusername(username);
        UserResTrueDto userResTrueDto = BeanUtil.toBean(userResDto, UserResTrueDto.class);
        return Results.success(userResTrueDto);
    }

    /**
     * 判断用户名是否存在
     */
    @GetMapping("/api/short-link/admin/v1/user/has-username/")
    public Result<Boolean> getUserHasUsername(@RequestParam String username) {
        return Results.success(userService.hasUsername(username));
    }
    /**
     * 用户注册
     */
    @PostMapping("/api/short-link/admin/v1/user")
    public Result<Void> saveUser(@RequestBody UserRegisterReqDTO userRegisterReqDTO) {
        userService.save(userRegisterReqDTO);
        return Results.success();
    }
    /**
     * 修改用户
     */
    @PutMapping("/api/short-link/admin/v1/user")
    public Result<Void> updateUser(@RequestBody UserUpdateReqDTO userUpdateReqDTO) {
        userService.updateUser(userUpdateReqDTO);
        return Results.success();
    }
    /**
     * 用户登录
     */
    @PostMapping("/api/short-link/admin/v1/user/login")
    public Result<UserLoginResDto> login(@RequestBody UserLoginReqDTO userLoginReqDTO) {
        UserLoginResDto userLoginResDto= userService.login(userLoginReqDTO);
        return Results.success(userLoginResDto);
    }
    /**
     * 检查是否登录
     */
    @GetMapping("/api/short-link/admin/v1/user/check-login")
    public Boolean checkLogin(@RequestParam("token") String token,@RequestParam("username") String username) {
        return userService.checkLogin(token,username);
    }
}
