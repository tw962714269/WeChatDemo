package com.ddwx.family.bean;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * 存储用户信息的bean
 */
public class UserInfoBean extends ErrorBean {
    //普通用户的标识，对当前开发者帐号唯一
    public String openid;
    //普通用户昵称
    public String nickname;
    //普通用户性别，1 为男性，2 为女性
    public int sex;
    //普通用户个人资料填写的省份
    public String province;
    //普通用户个人资料填写的城市
    public String city;
    //国家，如中国为 CN
    public String country;
    //用户头像，最后一个数值代表正方形头像大小（有 0、46、64、96、132 数值可选，0 代表 640*640 正方形头像），用户没有头像时该项为空
    public String headimgurl;
    //用户特权信息，json 数组，如微信沃卡用户为（chinaunicom）
    public ArrayList<String> privilege = new ArrayList<>();
    //用户统一标识。针对一个微信开放平台帐号下的应用，同一用户的 unionid 是唯一的。
    public String unionid;



    @NonNull
    @Override
    public String toString() {
        return "openid:" + openid + ",nickname:" + nickname

                + ",sex:" + sex + ",province:" + province

                + ",city:" + city + ",country:" + country

                + ",headimgurl:" + headimgurl + ",privilege.size():" + privilege.size()

                + ",unionid:" + unionid;
    }
}
