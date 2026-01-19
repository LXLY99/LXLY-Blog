package org.lxly.blog.smms;

import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lxly.blog.common.BizException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmmsClient {
    private final SmmsProperties properties;

    // SM.MS API endpoints (v2)
    private static final String UPLOAD_URL = "https://smms.app/api/v2/upload";
    private static final String DELETE_URL = "https://smms.app/api/v2/delete";
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif"
    );

    public SmmsUploadResult upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw BizException.badRequest("File is empty");
        }
        if (file.getSize() <= 0) {
            throw BizException.badRequest("File size is invalid");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw BizException.badRequest("Unsupported file type");
        }
        try {
            byte[] bytes = file.getBytes();
            Map<String, Object> form = new HashMap<>();
            // hutool will treat byte[] as file content
            form.put("smfile", bytes);
            try (HttpResponse resp = HttpRequest.post(UPLOAD_URL)
                    .header("Authorization", properties.getToken())
                    .form(form)
                    .execute()) {
                String body = resp.body();
                SmmsApiResponse api = JSONUtil.toBean(body, SmmsApiResponse.class);
                if (api == null || !Boolean.TRUE.equals(api.getSuccess())) {
                    String msg = api != null ? api.getMessage() : "SMMS upload failed";
                    throw new BizException(502, msg);
                }
                SmmsApiResponse.SmmsData d = api.getData();
                return new SmmsUploadResult(d.getUrl(), d.getDelete());
            }
        } catch (IOException e) {
            throw new BizException(500, "File read failed");
        } catch (Exception e) {
            if (e instanceof BizException be) {
                throw be;
            }
            throw new BizException(502, "SMMS upload failed");
        }
    }

    public void deleteByHash(String deleteHash) {
        if (deleteHash == null || deleteHash.isBlank()) {
            return;
        }
        try (HttpResponse resp = HttpRequest.get(DELETE_URL + "/" + deleteHash)
                .header("Authorization", properties.getToken())
                .execute()) {
            // Even if deletion fails, we only log.
            String body = resp.body();
            SmmsApiResponse api = JSONUtil.toBean(body, SmmsApiResponse.class);
            if (api == null || !Boolean.TRUE.equals(api.getSuccess())) {
                log.warn("SMMS delete failed: {}", body);
            }
        } catch (Exception ex) {
            log.warn("SMMS delete exception: {}", ex.getMessage());
        }
    }

    @lombok.Data
    private static class SmmsApiResponse {
        private Boolean success;
        private String message;
        private SmmsData data;

        @lombok.Data
        public static class SmmsData {
            private String url;
            private String delete;
        }
    }
}
