package com.ddwx.family;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ddwx.family.app.MyApplication;
import com.ddwx.family.url.UrlAddress;
import com.ddwx.family.utils.OkHttp;
import com.ddwx.family.utils.UrlType;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String WECHAT_APP_ID = "wx7f9b4e815b5fc737";
    public static final String WECHAT_APP_SECRET = "f1067828b63fb02b1b5a147c249510c7";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.text);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> checkParams = new HashMap<>();
                checkParams.put("access_token", MyApplication.accessTokenBean.access_token);
                checkParams.put("openid", MyApplication.accessTokenBean.openid);
                OkHttp.getWeChatData(UrlAddress.checkAccessTokenUrl, checkParams, MainActivity.this, UrlType.CHECK_ACCESS);
            }
        });
    }
}
