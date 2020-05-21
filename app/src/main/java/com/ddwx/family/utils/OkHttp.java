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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

import static com.ddwx.family.utils.ConstantApi.WECHAT_APP_ID;
import static com.ddwx.family.utils.ConstantApi.wxapi;

public class OkHttp {
    public static void getWeChatData(String url, Map<String, String> params, final Context context, final UrlType type) {
        OkHttpUtils.get().url(UrlAddress.weChatBaseUrl + url)
                .params(params)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(final String response, int id) {
                Log.e("onResponse", response);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    switch (type) {
                        /**
                         * 微信授权接口
                         */
                        case ACCRSSTOKEN:
                            InitBean.initAccessTokenBean(jsonObject);
                            if (MyApplication.errorBean.errcode == -1) {
                                Toast.makeText(context, "微信授权登陆成功.", Toast.LENGTH_SHORT).show();
                                Log.e("getWeChatData", "accessToken======>" + MyApplication.accessTokenBean.toString());
                                writeFile("accessToken", response);
                            } else {
                                Toast.makeText(context, "微信授权登陆失败.", Toast.LENGTH_SHORT).show();
                                Log.e("getWeChatData", "accessToken======>errcode:" + MyApplication.errorBean.errcode + "errmsg:" + MyApplication.errorBean.errmsg);
                            }
                            break;

                        /**
                         * 检测AccessToken是否可用
                         * 不可用就去调用登陆
                         */
                        case CHECK_ACCESS_TO_LOGIN:
                            InitBean.initAccessTokenBean(jsonObject);
                            if (MyApplication.errorBean.errcode == 0) {
                                //AccessToken可用
                                Toast.makeText(context, "已授权，可获取用户信息.", Toast.LENGTH_SHORT).show();
                            } else {
                                //AccessToken已过期，根据RefreshToken重新获取
                                requestDataByType(context, UrlType.REFRESH_TOKEN_TO_LOGIN);
                            }
                            break;

                        /**
                         * 检测AccessToken是否可用
                         * 可用就去获取用户信息
                         */
                        case CHECK_ACCESS_FOR_GET_USERINFO:
                            InitBean.initAccessTokenBean(jsonObject);
                            if (MyApplication.errorBean.errcode == 0) {
                                //AccessToken可用，获取用户信息
                                requestDataByType(context, UrlType.USERINFO);
                            } else {
                                //AccessToken已过期，根据RefreshToken重新获取
                                requestDataByType(context, UrlType.REFRESH_TOKEN_FOR_GET_USERINFO);
                            }
                            break;

                        /**
                         * 获取用户信息的接口
                         */
                        case USERINFO:
                            InitBean.initUserInfoBean(jsonObject);
                            if (MyApplication.errorBean.errcode == -1) {
                                Toast.makeText(context, "获取用户信息成功。", Toast.LENGTH_SHORT).show();
                                Log.e("getWeChatData", "userInfo======>" + MyApplication.userInfoBean.toString());
                                writeFile("userInfo", response);
                            } else {
                                Toast.makeText(context, "获取用户信息失败.", Toast.LENGTH_SHORT).show();
                                Log.e("getWeChatData", "userInfo======>errcode:" + MyApplication.errorBean.errcode + "errmsg:" + MyApplication.errorBean.errmsg);
                            }
                            break;

                        case REFRESH_TOKEN_TO_LOGIN:
                            InitBean.initAccessTokenBean(jsonObject);
                            if (MyApplication.errorBean.errcode == -1) {
                                //重新获取AccessToken成功
                                Toast.makeText(context, "已授权，可获取用户信息。", Toast.LENGTH_SHORT).show();
                                writeFile("accessToken", response);
                            } else {
                                //RefreshToken失效
                                if (CommonUtil.checkVX(context)) {
                                    SendAuth.Req req = new SendAuth.Req();
                                    req.scope = "snsapi_userinfo";
                                    wxapi.sendReq(req);
                                } else
                                    Toast.makeText(context, "微信未安装", Toast.LENGTH_SHORT).show();
                            }
                            break;

                        case REFRESH_TOKEN_FOR_GET_USERINFO:
                            InitBean.initAccessTokenBean(jsonObject);
                            if (MyApplication.errorBean.errcode == -1) {
                                //重新获取AccessToken成功
                                requestDataByType(context, UrlType.USERINFO);
                                writeFile("accessToken", response);
                            } else
                                //RefreshToken失效
                                Toast.makeText(context, "微信授权已过期，请重新登陆", Toast.LENGTH_SHORT).show();
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void requestDataByType(Context context, UrlType type) {
        MyApplication.errorBean = new ErrorBean();
        Map<String, String> params = new HashMap<>();

        switch (type) {
            case REFRESH_TOKEN_TO_LOGIN:
            case REFRESH_TOKEN_FOR_GET_USERINFO:
                params.put("appid", WECHAT_APP_ID);
                params.put("grant_type", "refresh_token");
                params.put("refresh_token", MyApplication.accessTokenBean.refresh_token);
                OkHttp.getWeChatData(UrlAddress.refreshAccessTokenUrl, params, context, type);
                break;
            case USERINFO:
                params.put("access_token", MyApplication.accessTokenBean.access_token);
                params.put("openid", MyApplication.accessTokenBean.openid);
                OkHttp.getWeChatData(UrlAddress.getUserInfoUrl, params, context, type);
                break;
        }
    }

    private static void writeFile(final String fileName, final String response) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileUtil.writeFileData(fileName + ".txt", response);
                    FileUtil.writtenFileData(fileName + "2.txt", response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
