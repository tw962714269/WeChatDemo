package com.ddwx.family.app;

import android.app.Application;

import com.ddwx.family.bean.AccessTokenBean;
import com.ddwx.family.bean.ErrorBean;
import com.ddwx.family.bean.UserInfoBean;

public class MyApplication extends Application {
    public static AccessTokenBean accessTokenBean = new AccessTokenBean();
    public static ErrorBean errorBean = new ErrorBean();
    public static UserInfoBean userInfoBean = new UserInfoBean();
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
