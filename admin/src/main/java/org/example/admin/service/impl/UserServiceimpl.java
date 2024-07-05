package org.example.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.example.admin.common.convention.exception.ClientException;
import org.example.admin.common.enums.UserErrorCodeEnum;
import org.example.admin.dao.entity.UserDo;
import org.example.admin.dao.mapper.UserMapper;
import org.example.admin.dto.req.UserLoginReqDTO;
import org.example.admin.dto.req.UserRegisterReqDTO;
import org.example.admin.dto.req.UserUpdateReqDTO;
import org.example.admin.dto.res.UserLoginResDto;
import org.example.admin.dto.res.UserResDto;
import org.example.admin.service.GroupService;
import org.example.admin.service.UserService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.example.admin.common.constant.RedisCacheConstant.LOCK_USER_SAVE_KEY;
import static org.example.admin.common.enums.UserErrorCodeEnum.*;

@AllArgsConstructor
@Service
public class UserServiceimpl extends ServiceImpl<UserMapper, UserDo> implements UserService {
    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;
    private final GroupService groupService;
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
                try {
                    int insert = baseMapper.insert(userDo);
                    if(insert<1){
                        throw new ClientException(USER_SAVE_ERROR);
                    }
                }
                catch (Exception e){
                    throw new ClientException(USER_EXIST);
                }
                userRegisterCachePenetrationBloomFilter.add(userRegisterReqDTO.getUsername());
                groupService.savegroup(userRegisterReqDTO.getUsername(),"默认分组");
                return;
            }
            throw new ClientException(USER_EXIST);
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 修改用户
     */
    @Override
    public void updateUser(UserUpdateReqDTO userUpdateReqDTO) {
        LambdaUpdateWrapper<UserDo> wrapper = Wrappers.lambdaUpdate(UserDo.class).eq(UserDo::getUsername, userUpdateReqDTO.getUsername());
        baseMapper.update(BeanUtil.toBean(userUpdateReqDTO,UserDo.class),wrapper);
    }

    /**
     * 用户登录
     */
    @Override
    public UserLoginResDto login(UserLoginReqDTO userLoginReqDTO) {
        LambdaQueryWrapper<UserDo> wrapper = Wrappers.lambdaQuery(UserDo.class)
                .eq(UserDo::getUsername, userLoginReqDTO.getUsername())
                .eq(UserDo::getPassword, userLoginReqDTO.getPassword())
                .eq(UserDo::getDelFlag, 0);
        UserDo userDo = baseMapper.selectOne(wrapper);
        if (userDo==null){
            throw new ClientException(USER_NULL);
        }
        Boolean hasKey = stringRedisTemplate.hasKey("login_" + userLoginReqDTO.getUsername());
        if (Boolean.TRUE.equals(hasKey)){
            throw new ClientException("用户已登录");
        }
        String token = UUID.randomUUID().toString();
        stringRedisTemplate.opsForHash().put("login_"+userLoginReqDTO.getUsername(),token,JSON.toJSONString(userDo));
        stringRedisTemplate.expire("login_"+userLoginReqDTO.getUsername(),30,TimeUnit.MINUTES);
        return new UserLoginResDto(token);
    }
    /**
     * 检查用户登录
     */
    @Override
    public Boolean checkLogin(String token, String username) {
        return stringRedisTemplate.opsForHash().hasKey("login_"+username,token);
    }
    /**
     * 退出登录
     */
    @Override
    public void logout(String token, String username) {
        if (checkLogin(token,username)){
            stringRedisTemplate.delete("login_"+username);
            return;
        }
        throw new ClientException("用户未登录或Token已存在");
    }

}
