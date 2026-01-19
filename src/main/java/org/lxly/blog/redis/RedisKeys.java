package org.lxly.blog.redis;

public final class RedisKeys {
    private RedisKeys() {}

    public static final String AUTH_TOKEN_PREFIX = "auth:token:"; // token -> userId
    public static final String AUTH_USER_TOKEN_PREFIX = "auth:user:"; // userId -> token

    public static final String VC_PREFIX = "vc:"; // vc:{type}:{email}
    public static final String RL_PREFIX = "rl:";

    public static final String SEQ_USER_SYSTEM_NAME = "seq:user:system_name";
}
