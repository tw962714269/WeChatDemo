package com.ddwx.family.bean;

import androidx.annotation.NonNull;

public class AccessTokenBean {
    //接口调用凭证
    public String access_token = "";
    //access_token 接口调用凭证超时时间，单位（秒）(7200miao)
    public int expires_in;
    //用户刷新 access_token
    public String refresh_token = "";
    //授权用户唯一标识
    public String openid = "";
    //用户授权的作用域，使用逗号（,）分隔
    public String scope;

    @NonNull
    @Override
    public String toString() {
        return "access_token:" + access_token + ",expires_in:" + expires_in

                + ",refresh_token:" + refresh_token + ",openid:" + openid

                + ",scope:" + scope;
    }
}
