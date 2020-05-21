package com.ddwx.family.utils;

/**
 * 用来告诉OkHttp当前请求的是哪个接口
 */
public enum UrlType {
    ACCRSSTOKEN,
    REFRESH_TOKEN_TO_LOGIN,
    REFRESH_TOKEN_FOR_GET_USERINFO,
    USERINFO,
    CHECK_ACCESS_TO_LOGIN,
    CHECK_ACCESS_FOR_GET_USERINFO
}
