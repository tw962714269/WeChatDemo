package com.ddwx.family.url;

public class UrlAddress {
    //BaseUrl
    public static final String weChatBaseUrl = "https://api.weixin.qq.com/";
    //微信授权需要请求的接口，主要参数为code
    public static final String getAccessTokenUrl = "sns/oauth2/access_token";
    //重新获取微信的AccessToken，主要参数为上一个接口获取到的refreshToken
    public static final String refreshAccessTokenUrl = "sns/oauth2/refresh_token";
    //检测当前accessToken是否可用，主要参数为要检测的accessToken，返回code为0表示可用
    public static final String checkAccessTokenUrl = "sns/auth";
    //获取授权用户的个人信息
    public static final String getUserInfoUrl = "sns/userinfo";
}
