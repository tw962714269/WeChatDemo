package com.ddwx.family.utils;

import com.ddwx.family.app.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class InitBean {
    public static void initAccessTokenBean(JSONObject jsonObject) {
        try {
            if (jsonObject.has("access_token")) {
                MyApplication.accessTokenBean.access_token = jsonObject.getString("access_token");
                MyApplication.accessTokenBean.expires_in = jsonObject.getInt("expires_in");
                MyApplication.accessTokenBean.refresh_token = jsonObject.getString("refresh_token");
                MyApplication.accessTokenBean.openid = jsonObject.getString("openid");
                MyApplication.accessTokenBean.scope = jsonObject.getString("scope");
            } else if (jsonObject.has("errcode")) {
                MyApplication.errorBean.errcode = jsonObject.getInt("errcode");
                MyApplication.errorBean.errmsg = jsonObject.getString("errmsg");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static void initUserInfoBean(JSONObject jsonObject) {
        try {
            if (jsonObject.has("openid")) {
                MyApplication.userInfoBean.openid = jsonObject.getString("openid");
                MyApplication.userInfoBean.nickname = jsonObject.getString("nickname");
                MyApplication.userInfoBean.sex = jsonObject.getInt("sex");
                MyApplication.userInfoBean.province = jsonObject.getString("province");
                MyApplication.userInfoBean.city = jsonObject.getString("city");
                MyApplication.userInfoBean.country = jsonObject.getString("country");
                MyApplication.userInfoBean.headimgurl = jsonObject.getString("headimgurl");
                JSONArray privilege = jsonObject.getJSONArray("privilege");
                for (int i = 0; i < privilege.length(); i++) {
                    MyApplication.userInfoBean.privilege.add((String) privilege.get(i));
                }
                MyApplication.userInfoBean.unionid = jsonObject.getString("unionid");
            } else if (jsonObject.has("errcode")) {
                MyApplication.errorBean.errcode = jsonObject.getInt("errcode");
                MyApplication.errorBean.errmsg = jsonObject.getString("errmsg");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
