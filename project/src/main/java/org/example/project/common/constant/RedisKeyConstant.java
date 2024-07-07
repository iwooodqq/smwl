package org.example.project.common.constant;

public class RedisKeyConstant {
    /**
     * 短链接跳转前缀
     */
    public static final String GOTO_SHORT_LINK_KEY="short_link_goto_%s";
    /**
     * 分布式锁名称
     */
    public static final String LOCK_GOTO_SHORT_LINK_KEY="short_link_lock_goto_%s";
    /**
     * 短链接空值跳转
     */
    public static final String GOTO_IS_NULL_SHORT_LINK_KEY="short_link_is_null_goto_%s";
}
