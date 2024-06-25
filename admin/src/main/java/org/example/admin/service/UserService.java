package org.example.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.admin.dao.entity.UserDo;
import org.example.admin.dto.req.UserLoginReqDTO;
import org.example.admin.dto.req.UserRegisterReqDTO;
import org.example.admin.dto.req.UserUpdateReqDTO;
import org.example.admin.dto.res.UserLoginResDto;
import org.example.admin.dto.res.UserResDto;

public interface UserService extends IService<UserDo> {
    UserResDto getUserByusername(String username);

    Boolean hasUsername(String username);

    void save(UserRegisterReqDTO userRegisterReqDTO);

    void updateUser(UserUpdateReqDTO userUpdateReqDTO);

    UserLoginResDto login(UserLoginReqDTO userLoginReqDTO);

    Boolean checkLogin(String token, String username);

    void logout(String token, String username);
}
