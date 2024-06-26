package org.example.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.example.admin.dao.entity.GroupDo;
import org.example.admin.dao.mapper.GroupMapper;
import org.example.admin.service.GroupService;
import org.example.admin.util.RandomStringGenerator;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class GroupServiceimpl extends ServiceImpl<GroupMapper, GroupDo>implements GroupService {

    /**
     * 新增分组
     */
    @Override
    public void savegroup(String group) {
        String random;
        do {
            random = RandomStringGenerator.generateRandom();
        } while (!hasgid(random));
        GroupDo groupDo = GroupDo.builder()
                .name(group)
                .gid(random)
                .build();
        baseMapper.insert(groupDo);
    }
    private Boolean hasgid(String random){
        LambdaQueryWrapper<GroupDo> wrapper = Wrappers.lambdaQuery(GroupDo.class)
                .eq(GroupDo::getId, random)
                //TODO设置用户名
                .eq(GroupDo::getUsername, null);
        //检查分组是否存在
        GroupDo one = baseMapper.selectOne(wrapper);
        return one==null;
    }
}
