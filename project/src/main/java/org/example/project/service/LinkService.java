package org.example.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.project.dao.entity.LinkDO;
import org.example.project.dto.req.ShortLinkCreateDTO;
import org.example.project.dto.req.ShortLinkPagereqDTO;
import org.example.project.dto.res.ShortLinkCountQueryResDTO;
import org.example.project.dto.res.ShortLinkCreateResDTO;
import org.example.project.dto.res.ShortLinkPageresDTO;

import java.util.List;

public interface LinkService extends IService<LinkDO> {
    ShortLinkCreateResDTO create(ShortLinkCreateDTO shortLinkCreateDTO);

    IPage<ShortLinkPageresDTO> pagelink(ShortLinkPagereqDTO shortLinkPagereqDTO);

    List<ShortLinkCountQueryResDTO> listShortLinkCountQueryResDTO(List<String> requestParam);
}
