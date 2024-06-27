package org.example.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.example.admin.common.biz.user.UserContext;
import org.example.admin.dao.entity.GroupDo;
import org.example.admin.dao.entity.UserDo;
import org.example.admin.dao.mapper.GroupMapper;
import org.example.admin.dto.req.GroupSortReqDTO;
import org.example.admin.dto.req.GroupUpdateReqDTO;
import org.example.admin.dto.res.GroupResDto;
import org.example.admin.service.GroupService;
import org.example.admin.util.RandomStringGenerator;
import org.springframework.stereotype.Service;

import java.util.List;

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
                .username(UserContext.getUsername())
                .gid(random)
                .sortOrder(0)
                .build();
        baseMapper.insert(groupDo);
    }
    /**
     * 查询分组排序
     */
    @Override
    public List<GroupResDto> sort() {
        LambdaQueryWrapper<GroupDo> wrapper = Wrappers.lambdaQuery(GroupDo.class).
                eq(GroupDo::getUsername, UserContext.getUsername())
                .eq(GroupDo::getDelFlag, 0)
                .orderByDesc(GroupDo::getSortOrder, GroupDo::getUpdateTime);
        List<GroupDo> groupDos = baseMapper.selectList(wrapper);
        List<GroupResDto> resDtoList = BeanUtil.copyToList(groupDos, GroupResDto.class);
        return resDtoList;
    }

    /**
     * 修改分组名称
     */
    @Override
    public void updategroup(GroupUpdateReqDTO groupUpdateReqDTO) {
        LambdaUpdateWrapper<GroupDo> wrapper = Wrappers.lambdaUpdate(GroupDo.class)
                .eq(GroupDo::getUsername, UserContext.getUsername())
                .eq(GroupDo::getGid, groupUpdateReqDTO.getGid())
                .eq(GroupDo::getDelFlag, 0);
        GroupDo groupDo = new GroupDo();
        groupDo.setName(groupUpdateReqDTO.getName());
        baseMapper.update(groupDo,wrapper);
    }

    @Override
    public void deletegroup(String gid) {
        LambdaUpdateWrapper<GroupDo> wrapper = Wrappers.lambdaUpdate(GroupDo.class)
                .eq(GroupDo::getUsername, UserContext.getUsername())
                .eq(GroupDo::getGid,gid)
                .eq(GroupDo::getDelFlag, 0);
        GroupDo groupDo = new GroupDo();
        groupDo.setDelFlag(1);
        baseMapper.update(groupDo,wrapper);
    }

    /**
     *设置分组排序
     */
    @Override
    public void sortgroup(List<GroupSortReqDTO> groupSortReqDTOList) {
        for (GroupSortReqDTO groupSortReqDTO : groupSortReqDTOList) {
            GroupDo groupDo = GroupDo.builder()
                    .sortOrder(groupSortReqDTO.getSortOrder())
                    .gid(groupSortReqDTO.getGid())
                    .build();
            LambdaUpdateWrapper<GroupDo> wrapper = Wrappers.lambdaUpdate(GroupDo.class)
                    .eq(GroupDo::getGid, groupDo.getGid())
                    .eq(GroupDo::getDelFlag, 0)
                    .eq(GroupDo::getUsername,UserContext.getUsername());
            baseMapper.update(groupDo,wrapper);
        }
    }

    /**
     * 删除分组
     */


    private Boolean hasgid(String random){
        LambdaQueryWrapper<GroupDo> wrapper = Wrappers.lambdaQuery(GroupDo.class)
                .eq(GroupDo::getId, random)
                .eq(GroupDo::getUsername, UserContext.getUsername());
        GroupDo one = baseMapper.selectOne(wrapper);
        return one==null;
    }
}
