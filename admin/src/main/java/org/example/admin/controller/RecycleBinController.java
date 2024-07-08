package org.example.admin.controller;

import lombok.AllArgsConstructor;
import org.example.admin.common.convention.result.Result;
import org.example.admin.common.convention.result.Results;
import org.example.admin.dto.req.RecycleBinSaveReqDTO;
import org.example.admin.remote.LinkRemoteService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class RecycleBinController {
    /**
     * 移动至回收站
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO recycleBinSaveReqDTO) {
        LinkRemoteService.saveRecycleBin(recycleBinSaveReqDTO);
        return Results.success();
    }
}
