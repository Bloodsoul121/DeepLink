package com.deeplink;

import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.deeplink.web.CustomWebView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.lang.reflect.Method;
import java.net.URISyntaxException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.webview)
    CustomWebView mWebView;

    private Handler mHandler = new Handler();
    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        parseIntent();
        init();
        adapterO();
        checkGooglePlayServices();
        checkFirebaseToken();
    }

    private void parseIntent() {
        Intent intent = getIntent();
        String op = intent.getStringExtra("op");
        Log.i("Firebase", "op: "+ op);
    }

    private void init() {
        mWebView.initWebSetting();
        mWebView.initClient();
        mWebView.loadUrl("file:///android_asset/deeplink.html");
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(this::task, 2000);
    }

    private void task() {
//        sendNotify();

//        callReceiver();

//        canBackgroundStart(this);
    }

    private void callReceiver() {
        Intent intent = new Intent();
        intent.setAction("com.deeplink.receiver.action");
        intent.setComponent(new ComponentName("com.deeplink", "com.deeplink.CustomReceiver")); // 适配8.0的广播
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
    }

    private void adapterO() {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 通知渠道的id 这个地方只要一直即可
            String id = "111111";
            // 用户可以看到的通知渠道的名字.
            CharSequence name = "notification channel";
            // 用户可以看到的通知渠道的描述
            String description = "notification description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // 配置通知渠道的属性
            mChannel.setDescription(description);
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            // 自定义声音
//            mChannel.setSound(Uri.parse("android.resource://" + getPackageName() + "/raw/qqqq"), null);
            // 设置通知出现时的震动（如果 android 设备支持的话）
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            //最后在notificationmanager中创建该通知渠道
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    private void sendNotify() {
        Intent intent;
        try {
            intent = Intent.parseUri("blood://link", Intent.URI_INTENT_SCHEME);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "111111")
                .setSmallIcon(R.drawable.ic_launcher_round)
                .setContentTitle("title")
                .setContentText("text")
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setOngoing(false)
                .setAutoCancel(true);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        mNotificationManager.notify(1, notification);
    }

    private boolean canBackgroundStart(Context context) {
        boolean isSupport = false;
        boolean canBackStart = false;
        AppOpsManager ops = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        try {
            int op = 10021; // >= 23
            // ops.checkOpNoThrow(op, uid, packageName)
            Method method = ops.getClass().getMethod("checkOpNoThrow", new Class[]{int.class, int.class, String.class});
            Integer result = (Integer) method.invoke(ops, op, android.os.Process.myUid(), context.getPackageName());
            canBackStart = result == AppOpsManager.MODE_ALLOWED;
            isSupport = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isSupport) {
            Toast.makeText(this, "后台弹出权限 " + canBackStart, Toast.LENGTH_SHORT).show();
        }
        return canBackStart;
    }

    /**
     * 检查 Google Play 服务
     */
    private void checkGooglePlayServices() {
        // 验证是否已在此设备上安装并启用Google Play服务，以及此设备上安装的旧版本是否为此客户端所需的版本
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (code == ConnectionResult.SUCCESS) {
            // 支持Google服务
        } else {
            /**
             * 依靠 Play 服务 SDK 运行的应用在访问 Google Play 服务功能之前，应始终检查设备是否拥有兼容的 Google Play 服务 APK。
             * 我们建议您在以下两个位置进行检查：主 Activity 的 onCreate() 方法中，及其 onResume() 方法中。
             * onCreate() 中的检查可确保该应用在检查成功之前无法使用。
             * onResume() 中的检查可确保当用户通过一些其他方式返回正在运行的应用（比如通过返回按钮）时，检查仍将继续进行。
             * 如果设备没有兼容的 Google Play 服务版本，您的应用可以调用以下方法，以便让用户从 Play 商店下载 Google Play 服务。
             * 它将尝试在此设备上提供Google Play服务。如果Play服务已经可用，则Task可以立即完成返回。
             */
            GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);

            // 或者使用以下代码

            /**
             * 通过isUserResolvableError来确定是否可以通过用户操作解决错误
             */
            if (GoogleApiAvailability.getInstance().isUserResolvableError(code)) {
                /**
                 * 返回一个对话框，用于解决提供的errorCode。
                 * @param activity  用于创建对话框的父活动
                 * @param code      通过调用返回的错误代码
                 * @param activity  调用startActivityForResult时给出的requestCode
                 */
                GoogleApiAvailability.getInstance().getErrorDialog(this, code, 200).show();
            }

        }
    }

    private void checkFirebaseToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.e("Firebase", "getInstanceId failed", task.getException());
                    return;
                }

                // Get new Instance ID token
                String token = task.getResult().getToken();
                Log.i("Firebase", "Token: "+ token);
            }
        });
    }

}
