package org.lxly.blog.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.lxly.blog.common.BizException;
import org.lxly.blog.entity.SysUser;
import org.lxly.blog.service.SysUserService;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    private final TokenService tokenService;
    private final SysUserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            // clear old
            UserContextHolder.clear();

            String token = resolveToken(request);
            if (token != null) {
                Long userId = tokenService.getUserIdByToken(token);
                if (userId != null) {
                    SysUser user = userService.getById(userId);
                    if (user != null && (user.getStatus() == null || user.getStatus() == 1)) {
                        UserContextHolder.set(new UserContext(user.getId(), user.getSystemName(), user.getRole()));
                    }
                }
            }

            if (handler instanceof HandlerMethod hm) {
                boolean loginRequired = hm.hasMethodAnnotation(LoginRequired.class)
                        || hm.getBeanType().isAnnotationPresent(LoginRequired.class)
                        || hm.hasMethodAnnotation(AdminRequired.class)
                        || hm.getBeanType().isAnnotationPresent(AdminRequired.class);

                if (loginRequired && UserContextHolder.getUserId() == null) {
                    throw BizException.unauthorized("Not logged in");
                }
                boolean adminRequired = hm.hasMethodAnnotation(AdminRequired.class)
                        || hm.getBeanType().isAnnotationPresent(AdminRequired.class);
                if (adminRequired && !UserContextHolder.isAdmin()) {
                    throw BizException.forbidden("Admin only");
                }
            }

            return true;
        } catch (BizException e) {
            // rethrow and let global handler handle it
            throw e;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContextHolder.clear();
    }

    private String resolveToken(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null || auth.isBlank()) {
            return null;
        }
        auth = auth.trim();
        if (auth.toLowerCase().startsWith("bearer ")) {
            return auth.substring(7).trim();
        }
        // allow raw token
        return auth;
    }
}
