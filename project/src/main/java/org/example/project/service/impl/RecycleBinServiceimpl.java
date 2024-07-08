package org.example.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.project.common.database.BaseDo;
import org.example.project.dao.entity.LinkDO;
import org.example.project.dao.mapper.LinkMapper;
import org.example.project.dto.req.RecycleBinSaveReqDTO;
import org.example.project.service.RecycleBinService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


import static org.example.project.common.constant.RedisKeyConstant.GOTO_SHORT_LINK_KEY;
@RequiredArgsConstructor
@Service
public class RecycleBinServiceimpl extends ServiceImpl<LinkMapper, LinkDO> implements RecycleBinService {
    private final StringRedisTemplate stringRedisTemplate;
    /**
     * 移动至回收站
     */
    @Override
    public void saveRecycleBin(RecycleBinSaveReqDTO recycleBinSaveReqDTO) {
        LambdaUpdateWrapper<LinkDO> wrapper = Wrappers.lambdaUpdate(LinkDO.class)
                .eq(LinkDO::getFullShortUrl, recycleBinSaveReqDTO.getFullShortUrl())
                .eq(LinkDO::getGid, recycleBinSaveReqDTO.getGid())
                .eq(LinkDO::getEnableStatus, 0)
                .eq(BaseDo::getDelFlag, 0);
        LinkDO linkDO = LinkDO.builder()
                .enableStatus(1)
                .build();
        baseMapper.update(linkDO,wrapper);
        stringRedisTemplate.delete(String.format(GOTO_SHORT_LINK_KEY, recycleBinSaveReqDTO.getFullShortUrl()));
    }
}
