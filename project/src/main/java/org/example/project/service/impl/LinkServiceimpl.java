package org.example.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.project.dao.entity.LinkDO;
import org.example.project.dao.mapper.LinkMapper;
import org.example.project.dto.req.ShortLinkCreateDTO;
import org.example.project.dto.res.ShortLinkCreateResDTO;
import org.example.project.service.LinkService;
import org.example.project.toolkit.HashUtil;
import org.springframework.stereotype.Service;
@Service
public class LinkServiceimpl extends ServiceImpl<LinkMapper,LinkDO> implements LinkService{
    @Override
    public ShortLinkCreateResDTO create(ShortLinkCreateDTO shortLinkCreateDTO) {
        String generateSuffix = generateSuffix(shortLinkCreateDTO);
        LinkDO linkDO = BeanUtil.toBean(shortLinkCreateDTO, LinkDO.class);
        linkDO.setFullShortUrl(shortLinkCreateDTO.getDomain()+"/"+generateSuffix);
        linkDO.setShortUri(generateSuffix);
        linkDO.setEnableStatus(0);
        baseMapper.insert(linkDO);
        return  ShortLinkCreateResDTO.builder()
                .fullShortUri(linkDO.getFullShortUrl())
                .originUri(shortLinkCreateDTO.getOriginUrl())
                .gid(shortLinkCreateDTO.getGid())
                .build();
    }
    //生成短链接方法
    private String generateSuffix(ShortLinkCreateDTO shortLinkCreateDTO){
        String originUri = shortLinkCreateDTO.getOriginUrl();
        return HashUtil.hashToBase62(originUri);
    }
}
