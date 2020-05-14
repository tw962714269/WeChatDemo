package com.ddwx.family.bean;

public class ErrorBean {
    //请求错误码  或者 检验授权凭证（access_token）是否有效 0为有效
    public int errcode = -1;
    //请求错误信息  或者  校验结果"ok"为有效
    public String errmsg;
}
