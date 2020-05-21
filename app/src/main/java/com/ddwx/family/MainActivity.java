package com.ddwx.family;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ddwx.family.app.MyApplication;
import com.ddwx.family.bean.ShareContent;
import com.ddwx.family.url.UrlAddress;
import com.ddwx.family.utils.CommonUtil;
import com.ddwx.family.utils.ConstantApi;
import com.ddwx.family.utils.FileUtil;
import com.ddwx.family.utils.OkHttp;
import com.ddwx.family.utils.ShareContentToVX;
import com.ddwx.family.utils.UrlType;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ddwx.family.utils.ConstantApi.CODE;
import static com.ddwx.family.utils.ConstantApi.SHARE_LINK_TO_WECHAT;
import static com.ddwx.family.utils.ConstantApi.SHARE_PICTURE_TO_WECHAT;
import static com.ddwx.family.utils.ConstantApi.SHARE_TEXT_TO_WECHAT;
import static com.ddwx.family.utils.ConstantApi.SHARE_TO_WECHAT_FRIENDS;
import static com.ddwx.family.utils.ConstantApi.SHARE_TO_WECHAT_SESSION;
import static com.ddwx.family.utils.ConstantApi.accessTokenPath;
import static com.ddwx.family.utils.ConstantApi.rootFilePath;
import static com.ddwx.family.utils.ConstantApi.wxapi;

/**
 * 在app文件夹下有说明.txt文件，内部是这次的整体流程
 * 基本功能已经实现
 * 所有的信息以Log日志的形式输出
 * 2020年5月14日17:11:06
 * 已更改为以文件的形式打印
 */

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    private List<String> permissionsList = new ArrayList<>();
    private ShareContent shareContent = new ShareContent();


    private String[] permissionsArray = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toCheckPermissions(this, permissionsArray);
        if (permissionsList.size() > 0) {
            Log.e(TAG, "onCreate: APP需要获取您的XXX权限，以保证您的正常使用");
            //去申请权限
            toRequestPermissions(this, CODE);
        }

        //微信登陆，获取用户信息
        TextView mWeChatLogin = findViewById(R.id.weChatLogin);
        /**
         * 微信授权登录
         */
        mWeChatLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtil.checkVX(MainActivity.this)) {
                    FileUtil.getRootFilePath(MainActivity.this);
                    if (FileUtil.checkFileExists(rootFilePath + "/" + accessTokenPath)) {
                        //判断文件是否存在，如果存在则从文件中获取accessToken
                        checkAccessToken(UrlType.CHECK_ACCESS_TO_LOGIN);
                    } else {
                        //如果不存在则表示未进行微信授权，前往进行授权
                        SendAuth.Req req = new SendAuth.Req();
                        req.scope = "snsapi_userinfo";
                        wxapi.sendReq(req);
                    }
                }
            }
        });

        TextView mUserInfo = findViewById(R.id.userInfo);
        /**
         * 获取用户信息
         */
        mUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CommonUtil.checkVX(MainActivity.this)) return;
                FileUtil.getRootFilePath(MainActivity.this);
                if (FileUtil.checkFileExists(rootFilePath + "/" + accessTokenPath)) {
                    //判断文件是否存在，如果存在则从文件中获取accessToken
                    checkAccessToken(UrlType.CHECK_ACCESS_FOR_GET_USERINFO);
                } else {
                    //如果不存在则表示未进行微信授权，前往进行授权
                    Toast.makeText(MainActivity.this, "请先授权微信登陆", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //微信会话分享
        TextView mShareTextToSession = findViewById(R.id.session_text);
        TextView mSharePicToSession = findViewById(R.id.session_picture);
        TextView mShareLinkToSession = findViewById(R.id.session_link);

        mShareTextToSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CommonUtil.checkVX(MainActivity.this)) return;
                initShareContent(SHARE_TEXT_TO_WECHAT, "分享文本内容", SHARE_TO_WECHAT_SESSION);
                shareToWeChat();
            }
        });

        mSharePicToSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CommonUtil.checkVX(MainActivity.this)) return;
                initShareContent(SHARE_PICTURE_TO_WECHAT, "分享图片", SHARE_TO_WECHAT_SESSION);
                shareToWeChat();
            }
        });

        mShareLinkToSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CommonUtil.checkVX(MainActivity.this)) return;
                initShareContent(SHARE_LINK_TO_WECHAT, "链接分享", SHARE_TO_WECHAT_SESSION);
                shareToWeChat();
            }
        });

        //微信朋友圈分享
        TextView mShareTextToFriends = findViewById(R.id.friends_text);
        TextView mSharePicToFriends = findViewById(R.id.friends_picture);
        TextView mShareLinkToFriends = findViewById(R.id.friends_link);

        mShareTextToFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CommonUtil.checkVX(MainActivity.this)) return;
                initShareContent(SHARE_TEXT_TO_WECHAT, "分享文本内容", SHARE_TO_WECHAT_FRIENDS);
                shareToWeChat();
            }
        });

        mSharePicToFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CommonUtil.checkVX(MainActivity.this)) return;
                initShareContent(SHARE_PICTURE_TO_WECHAT, "分享图片", SHARE_TO_WECHAT_FRIENDS);
                shareToWeChat();
            }
        });

        mShareLinkToFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CommonUtil.checkVX(MainActivity.this)) return;
                initShareContent(SHARE_LINK_TO_WECHAT, "链接分享", SHARE_TO_WECHAT_FRIENDS);
                shareToWeChat();
            }
        });
    }

    private void checkAccessToken(UrlType type) {
        try {
            FileUtil.initBeanByFileContent(rootFilePath + "/" + accessTokenPath, UrlType.ACCRSSTOKEN);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Map<String, String> checkParams = new HashMap<>();
        checkParams.put("access_token", MyApplication.accessTokenBean.access_token);
        checkParams.put("openid", MyApplication.accessTokenBean.openid);
        OkHttp.getWeChatData(UrlAddress.checkAccessTokenUrl, checkParams, this, type);
    }

    private void initShareContent(int type, String content, int way) {
        shareContent.initParams();
        shareContent.type = type;
        shareContent.iconId = R.mipmap.resize;
        shareContent.content = content;
        shareContent.title = "微信分享";
        shareContent.webUrl = ConstantApi.linkUrl;
        shareContent.way = way;
    }

    private void shareToWeChat() {
        WXMediaMessage msg;
        if ((msg = ShareContentToVX.setVXMsg(this,shareContent)) != null) {
            // 构造一个Req
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            // transaction用于唯一标识一个请求（可自定义）
            req.transaction = shareContent.content;
            // 上文的WXMediaMessage对象
            req.message = msg;
            req.scene = shareContent.way;
            // 向微信发送请求
            wxapi.sendReq(req);
        }
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
