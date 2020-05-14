package com.ddwx.family;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ddwx.family.app.MyApplication;
import com.ddwx.family.url.UrlAddress;
import com.ddwx.family.utils.FileUtil;
import com.ddwx.family.utils.InitBean;
import com.ddwx.family.utils.OkHttp;
import com.ddwx.family.utils.UrlType;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 在app文件夹下有说明.txt文件，内部是这次的整体流程
 * 基本功能已经实现
 * 所有的信息以Log日志的形式输出
 */

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    private final int CODE = 10086;
    public static final String WECHAT_APP_ID = "wx7f9b4e815b5fc737";
    public static final String WECHAT_APP_SECRET = "f1067828b63fb02b1b5a147c249510c7";

    private List<String> permissionsList = new ArrayList<>();
    private String rootFilePath;
    private String accessTokenPath = "accessToken.txt";
    private String userInfoPath = "userInfo.txt";


    private String[] permissionsArray = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootFilePath = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath() + "/";
        toCheckPermissions(this, permissionsArray);
        if (permissionsList.size() > 0) {
            Log.e(TAG, "onCreate: APP需要获取您的XXX权限，以保证您的正常使用");
            //去申请权限
            toRequestPermissions(this, CODE);
        }

        TextView textView = findViewById(R.id.text);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断文件是否存在，如果存在则从文件中获取accessToken
                //如果不存在则表示未进行微信授权，前往进行授权
                File file = new File(rootFilePath + accessTokenPath);
                if (file.exists()) {
                    String s = FileUtil.readFileContent(rootFilePath + accessTokenPath);
                    try {
                        InitBean.initAccessTokenBean(new JSONObject(s));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Map<String, String> checkParams = new HashMap<>();
                    checkParams.put("access_token", MyApplication.accessTokenBean.access_token);
                    checkParams.put("openid", MyApplication.accessTokenBean.openid);
                    OkHttp.getWeChatData(UrlAddress.checkAccessTokenUrl, checkParams, MainActivity.this, UrlType.CHECK_ACCESS);
                } else {
                    IWXAPI wxapi = WXAPIFactory.createWXAPI(MainActivity.this, MainActivity.WECHAT_APP_ID, true);
                    if (!wxapi.isWXAppInstalled()) {
                        Toast.makeText(MainActivity.this, "微信未安装", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    SendAuth.Req req = new SendAuth.Req();
                    req.scope = "snsapi_userinfo";
                    req.state = "wx_login_duzun";
                    wxapi.sendReq(req);
                }
            }
        });
    }

    /**
     * 检测权限是否被允许
     * MainActivity.permissionsList内添加的是未被允许的权限
     *
     * @param activity
     * @param strings  需要被检测的权限
     */
    private void toCheckPermissions(Activity activity, String... strings) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        permissionsList.clear();
        for (String s :
                strings) {
            if (ContextCompat.checkSelfPermission(activity, s) == PackageManager.PERMISSION_GRANTED)
                continue;
            permissionsList.add(s);
        }
    }

    /**
     * 进行权限授权
     *
     * @param activity
     * @param requestCode
     */
    private void toRequestPermissions(Activity activity, int requestCode) {
        String[] permissionsArray = permissionsList.toArray(new String[permissionsList.size()]);
        //权限未授权，去申请权限
        Log.e(TAG, "onCreate: requestPermissions: 去申请权限");
        ActivityCompat.requestPermissions(activity, permissionsArray, requestCode);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "需要获取到文件的读写权限，让你查看你的assessToken与用户信息", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onRequestPermissionsResult: 权限被禁止");
                    //进行弹窗提醒
                    Log.e(TAG, "onRequestPermissionsResult: 去授权");
                    toSetPermissions();
                    //申请权限被拒绝，直接跳出当前方法
                    return;
                } else {
                    Log.e(TAG, "onRequestPermissionsResult: 权限被允许");
                    //单个权限被允许后进行下一个权限判断
                    continue;
                }
            }
            //权限都被允许后进行下一步操作
            Log.e(TAG, "onRequestPermissionsResult: 进行用到权限的相关操作");
        }
    }

    /**
     * 跳转到手机系统的应用信息页面
     */
    private void toSetPermissions() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        startActivity(intent);
    }
}
