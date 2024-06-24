package org.example.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.admin.common.convention.exception.ClientException;
import org.example.admin.common.enums.UserErrorCodeEnum;
import org.example.admin.dao.entity.UserDo;
import org.example.admin.dao.mapper.UserMapper;
import org.example.admin.dto.res.UserResDto;
import org.example.admin.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
@Service
public class UserServiceimpl extends ServiceImpl<UserMapper, UserDo> implements UserService {
    /**
     * 根据用户名返回用户
     * @param username
     * @return
     */
    @Override
    public UserResDto getUserByusername(String username) {
        LambdaQueryWrapper<UserDo> queryWrapper = Wrappers.lambdaQuery(UserDo.class).eq(UserDo::getUsername, username);
        UserDo userDo =baseMapper.selectOne(queryWrapper);
        UserResDto Result = new UserResDto();
        if(userDo==null){
          throw new ClientException(UserErrorCodeEnum.USER_NULL);
        }
        BeanUtils.copyProperties(userDo,Result);
        return Result;
    }
}
