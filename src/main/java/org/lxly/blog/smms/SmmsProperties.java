package org.lxly.blog.smms;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "smms")
public class SmmsProperties {
    private String token;
}
