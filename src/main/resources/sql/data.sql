-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS my_blog_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE my_blog_db;

-- ----------------------------
-- 2. 用户表 (sys_user)
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
                            `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                            `system_name` VARCHAR(50) NOT NULL COMMENT '系统默认ID(lxly_X)，不可修改',
                            `nickname` VARCHAR(50) DEFAULT NULL COMMENT '用户昵称，可修改',
                            `email` VARCHAR(100) NOT NULL COMMENT '邮箱，登录账号',
                            `password` VARCHAR(100) NOT NULL COMMENT 'BCrypt加密后的密码',
                            `avatar` VARCHAR(255) DEFAULT 'https://s2.loli.net/2024/01/01/default_avatar.png' COMMENT '头像URL',
                            `avatar_delete_hash` VARCHAR(100) DEFAULT NULL COMMENT 'SM.MS头像删除凭证(用于更换时删除旧图)',
                            `gender` TINYINT(1) DEFAULT 0 COMMENT '性别 0:保密 1:男 2:女',
                            `role` VARCHAR(20) DEFAULT 'USER' COMMENT '角色：ADMIN-管理员, USER-普通用户',
                            `status` TINYINT(1) DEFAULT 1 COMMENT '状态 1:正常 0:禁用',
                            `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                            `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_email` (`email`),
                            UNIQUE KEY `uk_system_name` (`system_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 3. 分类表 (category) - 扩展功能：文章归档
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
                            `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
                            `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
                            `sort` INT(11) DEFAULT 0 COMMENT '排序',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章分类表';

-- ----------------------------
-- 4. 文章表 (article)
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article` (
                           `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
                           `user_id` BIGINT(20) NOT NULL COMMENT '作者ID',
                           `category_id` BIGINT(20) DEFAULT NULL COMMENT '所属分类ID',
                           `title` VARCHAR(255) NOT NULL COMMENT '文章标题',
                           `summary` VARCHAR(500) DEFAULT NULL COMMENT '文章摘要',
                           `content` LONGTEXT NOT NULL COMMENT 'Markdown原始内容',
                           `content_html` LONGTEXT DEFAULT NULL COMMENT '解析后的HTML',
                           `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图片URL',
                           `cover_delete_hash` VARCHAR(100) DEFAULT NULL COMMENT 'SM.MS封面删除凭证(用于更换时删除旧图)',
                           `view_count` INT(11) DEFAULT 0 COMMENT '浏览量',
                           `is_top` TINYINT(1) DEFAULT 0 COMMENT '是否置顶 1:是 0:否',
                           `status` TINYINT(1) DEFAULT 1 COMMENT '状态 1:已发布 0:草稿',
                           `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                           `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           PRIMARY KEY (`id`),
                           KEY `idx_user_id` (`user_id`),
                           KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';

-- ----------------------------
-- 5. 标签表 (tag)
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
                       `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
                       `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
                       PRIMARY KEY (`id`),
                       UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- ----------------------------
-- 6. 文章-标签关联表 (article_tag)
-- ----------------------------
DROP TABLE IF EXISTS `article_tag`;
CREATE TABLE `article_tag` (
                               `article_id` BIGINT(20) NOT NULL,
                               `tag_id` BIGINT(20) NOT NULL,
                               PRIMARY KEY (`article_id`, `tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章标签关联表';

-- ----------------------------
-- 7. 评论表 (comment) - 扩展功能：增加互动性
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
                           `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
                           `article_id` BIGINT(20) NOT NULL COMMENT '文章ID',
                           `user_id` BIGINT(20) NOT NULL COMMENT '评论者ID',
                           `content` TEXT NOT NULL COMMENT '评论内容',
                           `parent_id` BIGINT(20) DEFAULT 0 COMMENT '父评论ID (0为根评论)',
                           `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                           PRIMARY KEY (`id`),
                           KEY `idx_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- ----------------------------
-- 8. 验证码表 (sys_verify_code)
-- ----------------------------
DROP TABLE IF EXISTS `sys_verify_code`;
CREATE TABLE `sys_verify_code` (
                                   `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
                                   `email` VARCHAR(100) NOT NULL,
                                   `code` VARCHAR(10) NOT NULL COMMENT '验证码',
                                   `type` VARCHAR(20) NOT NULL COMMENT '类型：REGISTER, LOGIN',
                                   `is_used` TINYINT(1) DEFAULT 0 COMMENT '是否已使用',
                                   `expire_time` DATETIME NOT NULL COMMENT '过期时间',
                                   `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`id`),
                                   KEY `idx_email_code` (`email`, `code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮箱验证码表';

-- ---------------------------------------------------------
-- 9. 初始化数据
-- ---------------------------------------------------------

-- 初始化超级管理员 (密码默认为 123456 的 BCrypt 密文)
INSERT INTO `sys_user` (`system_name`, `nickname`, `email`, `password`, `role`, `gender`)
VALUES
    ('lxly_admin', '超级管理员', 'admin@example.com', '$2a$10$7696KyLfsS.u.K.W9p9uO.9sFvYkO./O.F.q.f.G.g.H.h.I.i.J.j', 'ADMIN', 1);

-- 初始化分类
INSERT INTO `category` (`name`, `sort`) VALUES ('技术教程', 1), ('生活琐事', 2), ('随笔感悟', 3);

-- 初始化标签
INSERT INTO `tag` (`name`) VALUES ('Java'), ('Spring Boot'), ('MySQL'), ('Markdown');

-- 初始化第一篇文章
INSERT INTO `article` (`user_id`, `category_id`, `title`, `summary`, `content`, `content_html`, `cover_image`)
VALUES
    (1, 1, '我的第一篇博客', '这是系统的初始化文章', '# 欢迎使用\n这是一篇基于 Markdown 的文章。', '<h1 id="欢迎使用">欢迎使用</h1><p>这是一篇基于 Markdown 的文章。</p>', 'https://s2.loli.net/2024/01/01/demo.jpg');

-- 关联文章与标签
INSERT INTO `article_tag` (`article_id`, `tag_id`) VALUES (1, 1), (1, 3);