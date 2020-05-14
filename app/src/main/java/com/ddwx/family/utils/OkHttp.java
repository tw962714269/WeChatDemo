package com.ddwx.family.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.ddwx.family.MainActivity;
import com.ddwx.family.app.MyApplication;
import com.ddwx.family.bean.ErrorBean;
import com.ddwx.family.url.UrlAddress;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class OkHttp {
    public static void getWeChatData(String url, Map<String, String> params, final Context context, final UrlType type) {
        OkHttpUtils.post().url(UrlAddress.weChatBaseUrl + url)
                .params(params)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                Log.e("onResponse", response);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    switch (type) {
                        case USERINFO:
                            if (MyApplication.errorBean.errcode == -1) {
                                InitBean.initUserInfoBean(jsonObject);
                                Log.e("getWeChatData", "userInfo======>" + MyApplication.userInfoBean.toString());
                            } else {
                                Log.e("getWeChatData", "userInfo======>errcode:" + MyApplication.errorBean.errcode + "errmsg:" + MyApplication.errorBean.errmsg);
                            }
                            break;
                        case ACCRSSTOKEN:
                            if (MyApplication.errorBean.errcode == -1) {
                                InitBean.initAccessTokenBean(jsonObject);
                                Log.e("getWeChatData", "accessToken======>" + MyApplication.accessTokenBean.toString());
                            } else {
                                Log.e("getWeChatData", "accessToken======>errcode:" + MyApplication.errorBean.errcode + "errmsg:" + MyApplication.errorBean.errmsg);
                            }
                            break;
                        case CHECK_ACCESS:
                            InitBean.initAccessTokenBean(jsonObject);
                            if (MyApplication.errorBean.errcode == 0) {
                                //AccessToken可用，获取用户信息
                                MyApplication.errorBean = new ErrorBean();
                                Map<String, String> getUserInfoParams = new HashMap<>();
                                getUserInfoParams.put("access_token", MyApplication.accessTokenBean.access_token);
                                getUserInfoParams.put("openid", MyApplication.accessTokenBean.openid);
                                OkHttp.getWeChatData(UrlAddress.getUserInfoUrl, getUserInfoParams, context, UrlType.USERINFO);
                            } else {
                                //AccessToken已过期，根据RefreshToken重新获取
                                MyApplication.errorBean = new ErrorBean();
                                Map<String, String> refreshParams = new HashMap<>();
                                refreshParams.put("appid", MainActivity.WECHAT_APP_ID);
                                refreshParams.put("grant_type", "refresh_token");
                                refreshParams.put("refresh_token", MyApplication.accessTokenBean.refresh_token);
                                OkHttp.getWeChatData(UrlAddress.refreshAccessTokenUrl, refreshParams, context, UrlType.REFRESH_TOKEN);
                            }
                            break;
                        case REFRESH_TOKEN:
                            InitBean.initAccessTokenBean(jsonObject);
                            if (MyApplication.errorBean.errcode == -1) {
                                //重新获取AccessToken成功
                                //前往获取用户信息
                                MyApplication.errorBean = new ErrorBean();
                                Map<String, String> getUserInfoParams = new HashMap<>();
                                getUserInfoParams.put("access_token", MyApplication.accessTokenBean.access_token);
                                getUserInfoParams.put("openid", MyApplication.accessTokenBean.openid);
                                OkHttp.getWeChatData(UrlAddress.getUserInfoUrl, getUserInfoParams, context, UrlType.USERINFO);
                            } else {
                                //RefreshToken失效
                                //重新拉取微信登录授权
                                IWXAPI wxapi = WXAPIFactory.createWXAPI(context, MainActivity.WECHAT_APP_ID, true);
                                if (!wxapi.isWXAppInstalled()) {
                                    Toast.makeText(context, "微信未安装", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                SendAuth.Req req = new SendAuth.Req();
                                req.scope = "snsapi_userinfo";
                                req.state = "wx_login_duzun";
                                wxapi.sendReq(req);
                            }
                            break;
                        default:
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
