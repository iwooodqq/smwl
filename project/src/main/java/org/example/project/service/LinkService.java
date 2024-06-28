package org.example.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.project.dao.entity.LinkDO;
import org.example.project.dto.req.ShortLinkCreateDTO;
import org.example.project.dto.res.ShortLinkCreateResDTO;

public interface LinkService extends IService<LinkDO> {
    ShortLinkCreateResDTO create(ShortLinkCreateDTO shortLinkCreateDTO);
}
