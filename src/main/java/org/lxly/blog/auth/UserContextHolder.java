package org.lxly.blog.auth;

public final class UserContextHolder {
    private static final ThreadLocal<UserContext> CTX = new ThreadLocal<>();

    private UserContextHolder() {}

    public static void set(UserContext context) {
        CTX.set(context);
    }

    public static UserContext get() {
        return CTX.get();
    }

    public static Long getUserId() {
        UserContext c = CTX.get();
        return c == null ? null : c.getUserId();
    }

    public static boolean isAdmin() {
        UserContext c = CTX.get();
        return c != null && "ADMIN".equalsIgnoreCase(c.getRole());
    }

    public static void clear() {
        CTX.remove();
    }
}
