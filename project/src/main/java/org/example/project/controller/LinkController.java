package org.example.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.project.common.convention.result.Result;
import org.example.project.common.convention.result.Results;
import org.example.project.dto.req.ShortLinkCreateDTO;
import org.example.project.dto.req.ShortLinkPagereqDTO;
import org.example.project.dto.req.ShortLinkUpdateDTO;
import org.example.project.dto.res.ShortLinkCountQueryResDTO;
import org.example.project.dto.res.ShortLinkCreateResDTO;
import org.example.project.dto.res.ShortLinkPageresDTO;
import org.example.project.service.LinkService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class LinkController {
    private final LinkService linkService;

    /**
     * 短链接新增
     */
    @PostMapping("/api/short-link/admin/v1/create")
    public Result<ShortLinkCreateResDTO> create(@RequestBody ShortLinkCreateDTO shortLinkCreateDTO) {
        ShortLinkCreateResDTO shortLinkCreateResDTO = linkService.create(shortLinkCreateDTO);
        return Results.success(shortLinkCreateResDTO);
    }

    /**
     * 短链接分页
     */
    @GetMapping("/api/short-link/admin/v1/page")
    public Result<IPage<ShortLinkPageresDTO>> shortLinkPagereqDTO(ShortLinkPagereqDTO shortLinkPagereqDTO) {
        IPage<ShortLinkPageresDTO> pagelink = linkService.pagelink(shortLinkPagereqDTO);
        return Results.success(pagelink);
    }

    /**
     * 短链接分组内数量
     */
    @GetMapping("/api/short-link/v1/count")
    public Result<List<ShortLinkCountQueryResDTO>> listShortLinkCountQueryResDTO(@RequestParam("requestParam") List<String> requestParam) {
        List<ShortLinkCountQueryResDTO> shortLinkCountQueryResDTOS = linkService.listShortLinkCountQueryResDTO(requestParam);
        return Results.success(shortLinkCountQueryResDTOS);
    }

    /**
     * 短链接修改
     */
    @PostMapping("/api/short-link/admin/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateDTO shortLinkUpdateDTO) {
        linkService.updateLink(shortLinkUpdateDTO);
        return Results.success();
    }

    /**
     * 跳转短链接
     */
    @GetMapping("/{short-uri}")
    public void restoreUrl(@PathVariable("short-uri") String shortUri, ServletRequest request, ServletResponse response) throws IOException {
        linkService.restoreUrl(shortUri, request, response);
    }
}
