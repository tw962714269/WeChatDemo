package com.ddwx.family.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ddwx.family.bean.ShareContent;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;

import static com.ddwx.family.utils.ConstantApi.SHARE_LINK_TO_WECHAT;
import static com.ddwx.family.utils.ConstantApi.SHARE_PICTURE_TO_WECHAT;
import static com.ddwx.family.utils.ConstantApi.SHARE_TEXT_TO_WECHAT;

public class ShareContentToVX {

    private static WXMediaMessage shareTextToWX(String msgContent) {
        WXTextObject textObject = new WXTextObject();
        textObject.text = msgContent;
        WXMediaMessage wxMediaMessage = new WXMediaMessage();
        wxMediaMessage.mediaObject = textObject;
        wxMediaMessage.description = msgContent;
        return wxMediaMessage;
    }

    private static WXMediaMessage sharePictureToWX(Context context, int iconId) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), iconId);
        WXImageObject imageObject = new WXImageObject(bitmap);
        WXMediaMessage wxMediaMessage = new WXMediaMessage();
        wxMediaMessage.mediaObject = imageObject;
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
        bitmap.recycle();
        wxMediaMessage.setThumbImage(scaledBitmap);
        return wxMediaMessage;
    }

    private static WXMediaMessage shareLinkToWX(Context context, ShareContent shareContent) {
        // 初始化一个WXWebpageObject对象
        WXWebpageObject webpageObject = new WXWebpageObject();
        // 填写网页的url
        webpageObject.webpageUrl = shareContent.webUrl;
        // 用WXWebpageObject对象初始化一个WXMediaMessage对象
        WXMediaMessage wxMediaMessage = new WXMediaMessage(webpageObject);
//        // 填写网页标题、描述、位图
        wxMediaMessage.title = shareContent.title;
        wxMediaMessage.description = shareContent.content;
        // 如果没有位图，可以传null，会显示默认的图片
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), shareContent.iconId);
        wxMediaMessage.setThumbImage(bitmap);
        return wxMediaMessage;
    }

    public static WXMediaMessage setVXMsg(Context context, ShareContent shareContent) {
        switch (shareContent.type) {
            case SHARE_TEXT_TO_WECHAT:
                return shareTextToWX(shareContent.content);
            case SHARE_PICTURE_TO_WECHAT:
                return sharePictureToWX(context,shareContent.iconId);
            case SHARE_LINK_TO_WECHAT:
                return shareLinkToWX(context,shareContent);
            default:
                return null;
        }
    }

}
