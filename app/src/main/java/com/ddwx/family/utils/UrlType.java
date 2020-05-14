package com.ddwx.family.utils;

/**
 * 用来告诉OkHttp当前请求的是哪个接口
 */
public enum UrlType {
    ACCRSSTOKEN,
    REFRESH_TOKEN,
    USERINFO,
    CHECK_ACCESS
}
