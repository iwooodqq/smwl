package org.example.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.example.project.common.convention.result.Result;
import org.example.project.common.convention.result.Results;
import org.example.project.dto.req.ShortLinkCreateDTO;
import org.example.project.dto.req.ShortLinkPagereqDTO;
import org.example.project.dto.res.ShortLinkCreateResDTO;
import org.example.project.dto.res.ShortLinkPageresDTO;
import org.example.project.service.LinkService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LinkController {
    private final LinkService linkService;
    /**
     *短链接新增
     */
    @PostMapping("/api/short-link/admin/v1/create")
    public Result<ShortLinkCreateResDTO>create(@RequestBody ShortLinkCreateDTO shortLinkCreateDTO) {
        ShortLinkCreateResDTO shortLinkCreateResDTO= linkService.create(shortLinkCreateDTO);
        return Results.success(shortLinkCreateResDTO);
    }

    /**
     * 短链接分页
     */
    @GetMapping("/api/short-link/admin/v1/page")
    public Result<IPage<ShortLinkPageresDTO>>shortLinkPagereqDTO(ShortLinkPagereqDTO shortLinkPagereqDTO){
        IPage<ShortLinkPageresDTO> pagelink = linkService.pagelink(shortLinkPagereqDTO);
        return Results.success(pagelink);
    }
}
