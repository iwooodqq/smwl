package org.example.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.project.dao.entity.LinkDO;
import org.example.project.dto.req.RecycleBinSaveReqDTO;

public interface RecycleBinService extends IService<LinkDO> {
    void saveRecycleBin(RecycleBinSaveReqDTO recycleBinSaveReqDTO);
}
