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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.deeplink.web.CustomWebView;

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
        init();
        adapterO();
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

}
