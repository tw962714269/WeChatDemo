package com.ddwx.family.utils;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.openapi.IWXAPI;

public class ConstantApi {
    //微信官方常量
    public static final String WECHAT_APP_ID = "wx7f9b4e815b5fc737";
    public static final String WECHAT_APP_SECRET = "f1067828b63fb02b1b5a147c249510c7";
    public static IWXAPI wxapi;

    /**
     * 微信分享内容
     * 1=======>分享文本
     * 2=======>分享图片
     * 3=======>分享链接
     */
    public static final int SHARE_TEXT_TO_WECHAT = 1;
    public static final int SHARE_PICTURE_TO_WECHAT = 2;
    public static final int SHARE_LINK_TO_WECHAT = 3;

    /**
     * 微信分享链接地址
     */
    public static final String linkUrl = "https://www.baidu.com/";

    /**
     * 微信分享途径
     */
    // SendMessageToWX.Req.WXSceneSession是分享到好友会话
    public static final int SHARE_TO_WECHAT_SESSION = SendMessageToWX.Req.WXSceneSession;
    // SendMessageToWX.Req.WXSceneTimeline是分享到朋友圈
    public static final int SHARE_TO_WECHAT_FRIENDS = SendMessageToWX.Req.WXSceneTimeline;

    public static int IS_WXAPP_INSTALLED = 0;

    //申请存储权限Code
    public static final int CODE = 10086;

    /**
     * 文件名
     */
    //存放AccessToken的文件名
    public static final String accessTokenPath = "accessToken.txt";
    //存放UserInfo的文件名
    public static final String userInfoPath = "userInfo.txt";

    public static String rootFilePath;


}
