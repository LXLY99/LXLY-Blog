package org.lxly.blog.util;

import jakarta.servlet.http.HttpServletRequest;

public final class HttpUtil {
    private HttpUtil() {}

    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank() && !"unknown".equalsIgnoreCase(xf)) {
            // take first ip
            int comma = xf.indexOf(',');
            return comma > 0 ? xf.substring(0, comma).trim() : xf.trim();
        }
        String ip = request.getRemoteAddr();
        return ip == null ? "unknown" : ip;
    }
}
