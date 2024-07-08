package org.example.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.project.common.database.BaseDo;
import org.example.project.dao.entity.LinkDO;
import org.example.project.dao.mapper.LinkMapper;
import org.example.project.dto.req.RecycleBinRecoverReqDTO;
import org.example.project.dto.req.RecycleBinRemoveReqDTO;
import org.example.project.dto.req.RecycleBinSaveReqDTO;
import org.example.project.dto.req.ShortLinkRecycleBinPagereqDTO;
import org.example.project.dto.res.ShortLinkPageresDTO;
import org.example.project.service.RecycleBinService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


import static org.example.project.common.constant.RedisKeyConstant.GOTO_IS_NULL_SHORT_LINK_KEY;
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

    /**
     * 分页管理
     */
    @Override
    public IPage<ShortLinkPageresDTO>pagelink(ShortLinkRecycleBinPagereqDTO shortLinkPagereqDTO) {
        LambdaQueryWrapper<LinkDO> queryWrapper = Wrappers.lambdaQuery(LinkDO.class)
                .in(LinkDO::getGid, shortLinkPagereqDTO.getGidList())
                .eq(LinkDO::getDelFlag, 0)
                .eq(LinkDO::getEnableStatus, 1)
                .orderByDesc(BaseDo::getUpdateTime);
        ShortLinkRecycleBinPagereqDTO shortLinkRecycleBinPagereqDTO = baseMapper.selectPage(shortLinkPagereqDTO, queryWrapper);
        return shortLinkRecycleBinPagereqDTO.convert(each -> {
            ShortLinkPageresDTO bean = BeanUtil.toBean(each, ShortLinkPageresDTO.class);
            bean.setDomain("http://" + bean.getDomain());
            return bean;
        });
    }

    /**
     * 短链接恢复
     */
    @Override
    public void recoverRecycleBin(RecycleBinRecoverReqDTO recycleBinRecoverReqDTO) {
        LambdaUpdateWrapper<LinkDO> wrapper = Wrappers.lambdaUpdate(LinkDO.class)
                .eq(LinkDO::getFullShortUrl, recycleBinRecoverReqDTO.getFullShortUrl())
                .eq(LinkDO::getGid, recycleBinRecoverReqDTO.getGid())
                .eq(LinkDO::getEnableStatus, 1)
                .eq(BaseDo::getDelFlag, 0);
        LinkDO linkDO = LinkDO.builder()
                .enableStatus(0)
                .build();
        baseMapper.update(linkDO,wrapper);
        stringRedisTemplate.delete(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, recycleBinRecoverReqDTO.getFullShortUrl()));
    }

    /**
     * 短链接删除
     */
    @Override
    public void removeRecycleBin(RecycleBinRemoveReqDTO recycleBinRemoveReqDTO) {
        LambdaUpdateWrapper<LinkDO> wrapper = Wrappers.lambdaUpdate(LinkDO.class)
                .eq(LinkDO::getFullShortUrl, recycleBinRemoveReqDTO.getFullShortUrl())
                .eq(LinkDO::getGid, recycleBinRemoveReqDTO.getGid())
                .eq(LinkDO::getEnableStatus, 1)
                .eq(BaseDo::getDelFlag, 0);
        baseMapper.delete(wrapper);
    }
}
