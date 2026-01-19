package org.lxly.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.lxly.blog.auth.LoginRequired;
import org.lxly.blog.auth.UserContextHolder;
import org.lxly.blog.common.ApiResponse;
import org.lxly.blog.config.SiteProperties;
import org.lxly.blog.entity.Article;
import org.lxly.blog.entity.SysUser;
import org.lxly.blog.service.ArticleService;
import org.lxly.blog.service.SysUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {
    private final SysUserService userService;
    private final ArticleService articleService;
    private final SiteProperties siteProperties;

    @GetMapping("/admin-profile")
    public ApiResponse<Map<String, Object>> adminProfile() {
        SysUser admin = userService.getAdminUser();
        if (admin == null) {
            return ApiResponse.ok(Map.of(
                    "nickname", "超级管理员",
                    "avatar", "",
                    "notice", siteProperties.getNotice(),
                    "github", siteProperties.getGithub(),
                    "bilibili", siteProperties.getBilibili(),
                    "email", siteProperties.getEmail()
            ));
        }
        return ApiResponse.ok(Map.of(
                "systemName", admin.getSystemName(),
                "nickname", admin.getNickname(),
                "avatar", admin.getAvatar(),
                "notice", siteProperties.getNotice(),
                "github", siteProperties.getGithub(),
                "bilibili", siteProperties.getBilibili(),
                "email", siteProperties.getEmail() != null ? siteProperties.getEmail() : admin.getEmail()
        ));
    }

    @GetMapping("/articles")
    public ApiResponse<Page<Article>> articles(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String q
    ) {
        return ApiResponse.ok(articleService.listPublishedArticles(new Page<>(page, size), q));
    }

    @LoginRequired
    @GetMapping("/my/articles/recent")
    public ApiResponse<List<Article>> myRecent(@RequestParam(defaultValue = "10") int size) {
        Long userId = UserContextHolder.getUserId();
        return ApiResponse.ok(articleService.recentPublished(userId, size));
    }

    @LoginRequired
    @GetMapping("/my/articles")
    public ApiResponse<Page<Article>> myArticles(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String q
    ) {
        Long userId = UserContextHolder.getUserId();
        return ApiResponse.ok(articleService.listMyArticles(new Page<>(page, size), userId, status, q));
    }

    @LoginRequired
    @GetMapping("/my/archive")
    public ApiResponse<Map<String, Long>> myArchiveCounts() {
        Long userId = UserContextHolder.getUserId();
        return ApiResponse.ok(articleService.archiveCounts(userId));
    }
}
