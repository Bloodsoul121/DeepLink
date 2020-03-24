package com.deeplink.web;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class CustomWebView extends WebView {

    public static final int ICE_CREAM_SANDWICH = 14;
    public static final int ICE_CREAM_SANDWICH_MR1 = 15;
    public static final int JELLY_BEAN = 16;
    public static final int JELLY_BEAN_MR1 = 17;
    public static final int JELLY_BEAN_MR2 = 18;
    public static final int KITKAT = 19;
    public static final int KITKAT_WATCH = 20;
    public static final int LOLLIPOP = 21;
    public static final int LOLLIPOP_MR1 = 22;
    public static final int MARSH_MALLOW = 23;
    public static final int N = 24;

    private static final String KEY_DATABASES = "databases";
    private static final String KEY_GEOLOCATION = "geolocation";
    private static final String KEY_APPCACHE = "appcache";

    private Context mContext;
    private String mAppCachePath;

    public CustomWebView(Context context) {
        super(context);
        init(context);
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }

    public void initWebSetting() {
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setGeolocationEnabled(true);

        settings.setDefaultFontSize(50);
        settings.setDefaultFixedFontSize(13);
        settings.setTextZoom(100);

        settings.setLoadWithOverviewMode(true);

        settings.setNeedInitialFocus(false);
//        settings.setSupportMultipleWindows(true);   // 多窗口
        settings.setEnableSmoothTransition(true);
        settings.setAllowContentAccess(true);

        // display
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setUseWideViewPort(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        settings.setSavePassword(true);
        settings.setSaveFormData(true);

        if (isAboveSpecifiedVersion(ICE_CREAM_SANDWICH)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                settings.setAllowUniversalAccessFromFileURLs(false);
                settings.setAllowFileAccessFromFileURLs(false);
            }
        }

        if (isAboveSpecifiedVersion(LOLLIPOP)) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        initCache(settings);
        initLoadsImagesAutomatically(settings);
    }

    private void initCache(WebSettings settings) {
        settings.setAppCacheEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        // HTML5 configuration parametersettings.
        settings.setAppCachePath(getAppCachePath());
        settings.setDatabasePath(mContext.getDir(KEY_DATABASES, Context.MODE_PRIVATE).getPath());
        settings.setGeolocationDatabasePath(mContext.getDir(KEY_GEOLOCATION, Context.MODE_PRIVATE).getPath());
    }

    private String getAppCachePath() {
        if (mAppCachePath == null) {
            mAppCachePath = mContext.getDir(KEY_APPCACHE, Context.MODE_PRIVATE).getPath();
        }
        return mAppCachePath;
    }

    private void initLoadsImagesAutomatically(WebSettings settings) {
        boolean loadImageAble = true;
        settings.setLoadsImagesAutomatically(loadImageAble);
        settings.setBlockNetworkImage(!loadImageAble);
    }

    private boolean isAboveSpecifiedVersion(int versionCode) {
        return Build.VERSION.SDK_INT >= versionCode;
    }

    public void initClient() {
        setWebViewClient(new CustomWebViewClient(mContext));
        setWebChromeClient(new CustomWebChromeClient());
    }


}
