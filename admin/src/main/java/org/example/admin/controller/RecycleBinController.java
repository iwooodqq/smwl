package org.example.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.admin.common.convention.result.Result;
import org.example.admin.common.convention.result.Results;
import org.example.admin.dto.req.RecycleBinSaveReqDTO;
import org.example.admin.remote.LinkRemoteService;
import org.example.admin.remote.dto.req.ShortLinkPagereqDTO;
import org.example.admin.remote.dto.req.ShortLinkRecycleBinPagereqDTO;
import org.example.admin.remote.dto.res.ShortLinkPageresDTO;
import org.example.admin.service.RecycleBinService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecycleBinController {
    private final RecycleBinService recycleBinService;
    /**
     * 移动至回收站
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO recycleBinSaveReqDTO) {
        LinkRemoteService.saveRecycleBin(recycleBinSaveReqDTO);
        return Results.success();
    }

    /**
     * 回收站分页
     */
    @GetMapping("/api/short-link/admin/v1/recycle-bin/page")
    public Result<IPage<ShortLinkPageresDTO>> shortLinkPagereqDTO(ShortLinkRecycleBinPagereqDTO shortLinkPagereqDTO) {
    return recycleBinService.pageRecycleBinShortLink(shortLinkPagereqDTO);
    }
}
