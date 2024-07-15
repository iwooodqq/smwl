package org.example.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.example.admin.common.convention.result.Result;
import org.example.admin.common.convention.result.Results;
import org.example.admin.remote.LinkRemoteService;
import org.example.admin.remote.dto.req.ShortLinkBatchCreateReqDTO;
import org.example.admin.remote.dto.req.ShortLinkCreateDTO;
import org.example.admin.remote.dto.req.ShortLinkPagereqDTO;
import org.example.admin.remote.dto.req.ShortLinkUpdateDTO;
import org.example.admin.remote.dto.res.ShortLinkBaseInfoRespDTO;
import org.example.admin.remote.dto.res.ShortLinkBatchCreateRespDTO;
import org.example.admin.remote.dto.res.ShortLinkCreateResDTO;
import org.example.admin.remote.dto.res.ShortLinkPageresDTO;
import org.example.admin.toolkit.EasyExcelWebUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LinkController {
    /**
     * 短链接分页
     */
    @GetMapping("/api/short-link/admin/v1/page")
    public Result<IPage<ShortLinkPageresDTO>> shortLinkPagereqDTO(ShortLinkPagereqDTO shortLinkPagereqDTO) {
        return Results.success(LinkRemoteService.shortLinkPagereqDTO(shortLinkPagereqDTO).getData());
    }
    /**
     *短链接新增
     */
    @PostMapping("/api/short-link/admin/v1/create")
    public Result<ShortLinkCreateResDTO>create(@RequestBody ShortLinkCreateDTO shortLinkCreateDTO) {
        return Results.success(LinkRemoteService.create(shortLinkCreateDTO).getData());
    }
    /**
     *短链接修改
     */
    @PostMapping("/api/short-link/admin/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateDTO shortLinkUpdateDTO){
        LinkRemoteService.updateLink(shortLinkUpdateDTO);
        return Results.success();
    }
    /**
     * 批量创建短链接
     */
    @SneakyThrows
    @PostMapping("/api/short-link/admin/v1/create/batch")
    public void batchCreateShortLink(@RequestBody ShortLinkBatchCreateReqDTO requestParam, HttpServletResponse response) {
        Result<ShortLinkBatchCreateRespDTO> shortLinkBatchCreateRespDTOResult = LinkRemoteService.batchCreateShortLink(requestParam);
        if (shortLinkBatchCreateRespDTOResult.isSuccess()) {
            List<ShortLinkBaseInfoRespDTO> baseLinkInfos = shortLinkBatchCreateRespDTOResult.getData().getBaseLinkInfos();
            EasyExcelWebUtil.write(response, "批量创建短链接-SaaS短链接系统", ShortLinkBaseInfoRespDTO.class, baseLinkInfos);
        }
    }

}
