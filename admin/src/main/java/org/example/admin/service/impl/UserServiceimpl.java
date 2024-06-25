package org.example.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.example.admin.common.convention.exception.ClientException;
import org.example.admin.common.enums.UserErrorCodeEnum;
import org.example.admin.dao.entity.UserDo;
import org.example.admin.dao.mapper.UserMapper;
import org.example.admin.dto.req.UserRegisterReqDTO;
import org.example.admin.dto.res.UserResDto;
import org.example.admin.service.UserService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import static org.example.admin.common.constant.RedisCacheConstant.LOCK_USER_SAVE_KEY;
import static org.example.admin.common.enums.UserErrorCodeEnum.USER_EXIST;
import static org.example.admin.common.enums.UserErrorCodeEnum.USER_SAVE_ERROR;

@AllArgsConstructor
@Service
public class UserServiceimpl extends ServiceImpl<UserMapper, UserDo> implements UserService {
    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    /**
     * 根据用户名返回用户
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
     */
    @Override
    public Boolean hasUsername(String username) {
       return !userRegisterCachePenetrationBloomFilter.contains(username);
    }
    /**
     * 用户注册
     */
    @Override
    public void save(UserRegisterReqDTO userRegisterReqDTO) {
        if (!hasUsername(userRegisterReqDTO.getUsername())){
            throw new ClientException(USER_EXIST);
        }
        UserDo userDo = new UserDo();
        BeanUtils.copyProperties(userRegisterReqDTO,userDo);
        //上分布式锁
        RLock lock = redissonClient.getLock(LOCK_USER_SAVE_KEY + userRegisterReqDTO.getUsername());
        try {
            if (lock.tryLock()){
                int insert = baseMapper.insert(userDo);
                if(insert<1){
                    throw new ClientException(USER_SAVE_ERROR);
                }
                userRegisterCachePenetrationBloomFilter.add(userRegisterReqDTO.getUsername());
                return;
            }
            throw new ClientException(USER_EXIST);
        }
        finally {
            lock.unlock();
        }
    }
}
