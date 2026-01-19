package org.lxly.blog.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserContext {
    private Long userId;
    private String systemName;
    private String role;
}
