package com.hmetao.code_dictionary.constants;

public class BaseConstants {

    // 盐
    public static final String SALT_PASSWORD = "HMETAO";

    // 登录session key
    public static final String LOGIN_USERINFO_SESSION_KEY = "Login_Session_UserInfo";

    // ali oss 前缀
    public static final String ALI_OSS_TOOL_UPLOAD_PREFIX = "tool/";

    // 七牛云 oss 前缀
    public static final String QINIU_OSS_UPLOAD_PREFIX = "avatar/";
    public static final String QINIU_OSS_MARKDOWN_IMAGE_UPLOAD_PREFIX = "markdown/images";

    // 用户角色 id
    public static final Long BASE_ROLE_USER = 2L;

    // 系统默认用户 id
    public static final Long BASE_ADMIN_USER = 1L;

    // 通用分组
    public static final String BASE_GROUP = "通用分组";

    // 基本权限标识
    public static final String BASE_PERMS = "base";
}
