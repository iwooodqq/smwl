package org.example.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.example.project.common.convention.result.Result;
import org.example.project.common.convention.result.Results;
import org.example.project.dto.req.RecycleBinRecoverReqDTO;
import org.example.project.dto.req.RecycleBinRemoveReqDTO;
import org.example.project.dto.req.RecycleBinSaveReqDTO;
import org.example.project.dto.req.ShortLinkRecycleBinPagereqDTO;
import org.example.project.dto.res.ShortLinkPageresDTO;
import org.example.project.service.RecycleBinService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class RecycleBinController {
    private final RecycleBinService recycleBinService;

    /**
     * 移动至回收站
     */
    @PostMapping("/api/short-link/v1/recycle-bin/save")
    public Result<Void>saveRecycleBin(@RequestBody RecycleBinSaveReqDTO recycleBinSaveReqDTO) {
        recycleBinService.saveRecycleBin(recycleBinSaveReqDTO);
        return Results.success();
    }
    /**
     * 短链接分页
     */
    @GetMapping("/api/short-link/v1/recycle-bin/page")
    public Result<IPage<ShortLinkPageresDTO>> shortLinkPagereqDTO(ShortLinkRecycleBinPagereqDTO shortLinkPagereqDTO) {
        IPage<ShortLinkPageresDTO> pagelink = recycleBinService.pagelink(shortLinkPagereqDTO);
        return Results.success(pagelink);
    }
    /**
     * 短链接恢复
     */
    @PostMapping("/api/short-link/v1/recycle-bin/recover")
    public Result<Void> recoverRecycleBin(@RequestBody RecycleBinRecoverReqDTO recycleBinRecoverReqDTO){
        recycleBinService.recoverRecycleBin(recycleBinRecoverReqDTO);
        return Results.success();
    }
    /**
     * 删除短链接
     */
    @PostMapping("/api/short-link/v1/recycle-bin/remove")
    public Result<Void> removeRecycleBin(@RequestBody RecycleBinRemoveReqDTO recycleBinRemoveReqDTO){
        recycleBinService.removeRecycleBin(recycleBinRemoveReqDTO);
        return Results.success();
    }
}
