package org.example.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import org.example.admin.common.biz.user.UserContext;
import org.example.admin.common.convention.exception.ClientException;
import org.example.admin.common.convention.result.Result;
import org.example.admin.dao.entity.GroupDo;
import org.example.admin.dao.mapper.GroupMapper;
import org.example.admin.database.BaseDo;
import org.example.admin.remote.LinkRemoteService;
import org.example.admin.remote.dto.req.ShortLinkRecycleBinPagereqDTO;
import org.example.admin.remote.dto.res.ShortLinkPageresDTO;
import org.example.admin.service.RecycleBinService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RecycleBinServiceimpl implements RecycleBinService {
    private final GroupMapper groupMapper;
    @Override
    public Result<IPage<ShortLinkPageresDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPagereqDTO shortLinkPagereqDTO) {
        LambdaQueryWrapper<GroupDo> wrapper = Wrappers.lambdaQuery(GroupDo.class)
                .eq(GroupDo::getUsername, UserContext.getUsername())
                .eq(BaseDo::getDelFlag, 0);
        List<GroupDo> groupDos = groupMapper.selectList(wrapper);
        if (CollUtil.isEmpty(groupDos)){
            throw new ClientException("无用户分组信息");
        }
        shortLinkPagereqDTO.setGidList(groupDos.stream().map(GroupDo::getGid).toList());
        return LinkRemoteService.pageRecycleBinShortLink(shortLinkPagereqDTO);
    }
}
