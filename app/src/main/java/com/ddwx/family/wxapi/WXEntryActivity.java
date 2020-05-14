package com.ddwx.family.wxapi;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ddwx.family.MainActivity;
import com.ddwx.family.app.MyApplication;
import com.ddwx.family.bean.AccessTokenBean;
import com.ddwx.family.bean.ErrorBean;
import com.ddwx.family.url.UrlAddress;
import com.ddwx.family.utils.OkHttp;
import com.ddwx.family.utils.UrlType;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    private static final String TAG = "WXEntryActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WXAPIFactory.createWXAPI(this, MainActivity.WECHAT_APP_ID).handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.e(TAG, "onReq");
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(final BaseResp baseResp) {
        Log.e(TAG, "onReq" + baseResp.errCode);
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //得到code
                        MyApplication.errorBean = new ErrorBean();
                        String code = ((SendAuth.Resp) baseResp).code;
                        Log.d(TAG, code);
                        Map<String, String> params = new HashMap<>(50);
                        params.put("appid", MainActivity.WECHAT_APP_ID);
                        params.put("secret", MainActivity.WECHAT_APP_SECRET);
                        params.put("code", code);
                        params.put("grant_type", "authorization_code");
                        OkHttp.getWeChatData(UrlAddress.getAccessTokenUrl, params, AccessTokenBean.class,WXEntryActivity.this, UrlType.ACCRSSTOKEN);
                        finish();
                    }
                });
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED://用户拒绝授权
                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //取消
                break;
        }
    }
}
