package org.lxly.blog.controller;

import lombok.RequiredArgsConstructor;
import org.lxly.blog.auth.LoginRequired;
import org.lxly.blog.common.ApiResponse;
import org.lxly.blog.smms.SmmsClient;
import org.lxly.blog.smms.SmmsUploadResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {
    private final SmmsClient smmsClient;

    @LoginRequired
    @PostMapping("/image")
    public ApiResponse<SmmsUploadResult> uploadImage(@RequestParam("file") MultipartFile file) {
        return ApiResponse.ok(smmsClient.upload(file));
    }

    @LoginRequired
    @DeleteMapping("/image")
    public ApiResponse<Void> deleteImage(@RequestParam("deleteHash") String deleteHash) {
        smmsClient.deleteByHash(deleteHash);
        return ApiResponse.ok();
    }
}
