package org.example.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.example.project.common.convention.result.Result;
import org.example.project.common.convention.result.Results;
import org.example.project.dto.req.RecycleBinSaveReqDTO;
import org.example.project.dto.req.ShortLinkPagereqDTO;
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
    public Result<IPage<ShortLinkPageresDTO>> shortLinkPagereqDTO(ShortLinkPagereqDTO shortLinkPagereqDTO) {
        IPage<ShortLinkPageresDTO> pagelink = recycleBinService.pagelink(shortLinkPagereqDTO);
        return Results.success(pagelink);
    }
}
