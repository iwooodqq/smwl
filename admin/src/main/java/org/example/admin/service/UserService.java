package org.example.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.admin.dao.entity.UserDo;
import org.example.admin.dto.req.UserRegisterReqDTO;
import org.example.admin.dto.res.UserResDto;

public interface UserService extends IService<UserDo> {
    UserResDto getUserByusername(String username);

    Boolean hasUsername(String username);

    void save(UserRegisterReqDTO userRegisterReqDTO);
}
