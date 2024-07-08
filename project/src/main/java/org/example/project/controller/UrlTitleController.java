package org.example.project.controller;

import lombok.AllArgsConstructor;
import org.example.project.common.convention.result.Result;
import org.example.project.common.convention.result.Results;
import org.example.project.service.UrlTitleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UrlTitleController {
    private final UrlTitleService urlTitleService;

    /**
     * 获取网页标题
     */
    @GetMapping("/api/short-link/v1/title")
    public Result<String>getTitleByUrl(@RequestParam("url") String url) {
        return Results.success(urlTitleService.getTitleByUrl(url));
    }
}
