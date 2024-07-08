package org.example.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.admin.common.convention.result.Result;
import org.example.admin.remote.dto.req.ShortLinkRecycleBinPagereqDTO;
import org.example.admin.remote.dto.res.ShortLinkPageresDTO;

public interface RecycleBinService {
    Result<IPage<ShortLinkPageresDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPagereqDTO shortLinkPagereqDTO);
}
