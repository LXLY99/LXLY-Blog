package org.lxly.blog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "site")
public class SiteProperties {
    /** public notice/announcement */
    private String notice;
    private String github;
    private String bilibili;
    private String email;
}
