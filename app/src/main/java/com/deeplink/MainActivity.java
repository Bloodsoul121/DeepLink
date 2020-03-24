package com.deeplink;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.deeplink.web.CustomWebView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.webview)
    CustomWebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
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
}
