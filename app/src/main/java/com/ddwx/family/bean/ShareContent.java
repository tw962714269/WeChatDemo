package com.ddwx.family.bean;

public class ShareContent {
    //分享的类型   纯文字，图片，图文，链接
    public int type;
    public String title;
    public String content;
    public int iconId;
    public String webUrl;
    public int way;

    public void initParams() {
        type = 0;
        title = "";
        content = "";
        iconId = 0;
        webUrl = "";
        way = 0;
    }
}
