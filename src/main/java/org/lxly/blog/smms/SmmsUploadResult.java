package org.lxly.blog.smms;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SmmsUploadResult {
    private String url;
    private String deleteHash;
}
