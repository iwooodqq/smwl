package org.example.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.example.admin.common.convention.exception.ClientException;
import org.example.admin.common.enums.UserErrorCodeEnum;
import org.example.admin.config.RBloomFilterConfiguration;
import org.example.admin.dao.entity.UserDo;
import org.example.admin.dao.mapper.UserMapper;
import org.example.admin.dto.res.UserResDto;
import org.example.admin.service.UserService;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
@AllArgsConstructor
@Service
public class UserServiceimpl extends ServiceImpl<UserMapper, UserDo> implements UserService {
    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
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
    /**
     * 判断用户名是否存在
     * @param username
     * @return
     */
    @Override
    public Boolean hasUsername(String username) {
        LambdaQueryWrapper<UserDo> queryWrapper = Wrappers.lambdaQuery(UserDo.class).eq(UserDo::getUsername, username);
        UserDo userDo = baseMapper.selectOne(queryWrapper);
        if(userDo==null){
            return false;
        }else {
            return true;
        }
    }
}
