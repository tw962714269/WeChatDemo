package com.ddwx.family.utils;

import android.content.Context;
import android.widget.Toast;

import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import static com.ddwx.family.utils.ConstantApi.wxapi;

public class CommonUtil {

    /**
     * 检查是否安装微信
     *
     * @param context
     * @return
     */
    public static boolean checkVX(Context context) {
        if (wxapi == null)
            wxapi = WXAPIFactory.createWXAPI(context, ConstantApi.WECHAT_APP_ID);
        if (wxapi.isWXAppInstalled()) {
            return true;
        }
        Toast.makeText(context, "微信未安装", Toast.LENGTH_SHORT).show();
        return false;
    }
}
