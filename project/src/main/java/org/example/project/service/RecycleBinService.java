package org.example.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.project.dao.entity.LinkDO;
import org.example.project.dto.req.*;
import org.example.project.dto.res.ShortLinkPageresDTO;

public interface RecycleBinService extends IService<LinkDO> {
    void saveRecycleBin(RecycleBinSaveReqDTO recycleBinSaveReqDTO);
    IPage<ShortLinkPageresDTO> pagelink(ShortLinkRecycleBinPagereqDTO shortLinkPagereqDTO);

    void recoverRecycleBin(RecycleBinRecoverReqDTO recycleBinRecoverReqDTO);

    void removeRecycleBin(RecycleBinRemoveReqDTO recycleBinRemoveReqDTO);
}
