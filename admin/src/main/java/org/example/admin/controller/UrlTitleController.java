package org.example.admin.controller;

import lombok.AllArgsConstructor;
import org.example.admin.common.convention.result.Result;
import org.example.admin.remote.LinkRemoteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UrlTitleController {
    /**
     * 获取网页标题
     */
    @GetMapping("/api/short-link/admin/v1/title")
    public Result<String> getTitleByUrl(@RequestParam("url") String url) {
        return LinkRemoteService.getTitleByUrl(url);
    }
}
