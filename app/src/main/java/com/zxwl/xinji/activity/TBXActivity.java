package com.zxwl.xinji.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.immersionbar.ImmersionBar;
import com.jaeger.library.StatusBarUtil;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.tencent.smtt.utils.TbsLog;
import com.zxwl.network.Urls;
import com.zxwl.xinji.R;
import com.zxwl.xinji.widget.X5WebView;

import java.net.MalformedURLException;
import java.net.URL;

public class TBXActivity extends AppCompatActivity {
    /**
     * 作为一个浏览器的示例展示出来，采用android+web的模式
     */
    private X5WebView mWebView;
    private ViewGroup mViewParent;
    private ImageButton mBack;
    private ImageButton mForward;
    private ImageButton mExit;
    private ImageButton mHome;
    private ImageButton mMore;

    private String mHomeUrl;

    private static final String TAG = "SdkDemo";
    private static final int MAX_LENGTH = 14;
    private boolean mNeedTestPage = false;

    private final int disable = 120;
    private final int enable = 255;

    private ProgressBar mPageLoadingProgressBar = null;

    private ValueCallback<Uri> uploadFile;

    private URL mIntentUrl;

    public static final String PARAM_URL = "PARAM_URL";

    public static String HOME_URL = "question/question.html?accountId=";//答题首页

    public static String EXAM_LIST = "examList.html";//答题列表

    public static String DAY_URL = "question/questionDaily.html?accountId=";//每日首页

    public static String WEEK_URL = "question/questionWeekly.html?accountId=";//每周首页

    public static String SPECIAL_URL = "question/questionSpecial.html?accountId=";//专项答题

    public static String CHALLENGE_URL = "question/questionChallengeList.html?accountId=";//挑战答题

    private String currentUrl = "";

    private String title;

    private LinearLayout llTitle;
    private TextView tvTitle;
    private ImageView ivMore;

    public static void startActivity(Context context, String url) {
        Intent intent = new Intent(context, TBXActivity.class);
        intent.putExtra(PARAM_URL, url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        mHomeUrl = getIntent().getStringExtra(PARAM_URL);
        title = getIntent().getStringExtra(PARAM_URL);

        Intent intent = getIntent();
        if (intent != null) {
            try {
                mIntentUrl = new URL(intent.getData().toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {

            } catch (Exception e) {
            }
        }
        try {
            if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 11) {
                getWindow()
                        .setFlags(
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
        } catch (Exception e) {
        }

        /*
         * getWindow().addFlags(
         * android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
         */
        setContentView(R.layout.activity_tbx);

        setBlackFont();

        llTitle = (LinearLayout) findViewById(R.id.ll_title);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivMore = (ImageView) findViewById(R.id.iv_more);

        tvTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.ic_web_back, 0, 0, 0);

        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWebView == null) {
                    finish();
                    return;
                }
                //是否包含根目录
//                boolean contains = currentUrl.contains(HOME_URL) || currentUrl.contains(EXAM_LIST) || currentUrl.contains(mHomeUrl);
//                if (!TextUtils.isEmpty(currentUrl) && contains) {
//                    finish();
//                    return;
//                }

                if (mWebView != null && mWebView.canGoBack()) {
                    mWebView.goBack();
                    if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 16) {
                        changGoForwardButton(mWebView);
                    }
                    return;
                } else {
                    finish();
                    return;
                }
            }
        });

        ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(title);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        getTopTitle();

        mViewParent = (ViewGroup) findViewById(R.id.webView1);

        initBtnListenser();

        mTestHandler.sendEmptyMessageDelayed(MSG_INIT_UI, 10);
    }

    private String getTopTitle() {
        String topTitle = "";
        switch (title) {
            case Urls.DZDG_URL:
                llTitle.setVisibility(View.VISIBLE);
                tvTitle.setText("党章党规_共产党员网");
                StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_FFF5E5), 0);
                return "党章党规";

            case Urls.FWDT_URL:
                llTitle.setVisibility(View.VISIBLE);
                tvTitle.setText("服务大厅");

                StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_FFF5E5), 0);
                return "服务大厅";

            case Urls.DWWD_URL:
                llTitle.setVisibility(View.VISIBLE);
                tvTitle.setText("党务知识_共产党员网");
                StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_FFF5E5), 0);
                return "党务问答";

            default:
                StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_e6493b), 0);
                llTitle.setVisibility(View.GONE);
                break;
        }
        return topTitle;
    }

    private void changGoForwardButton(WebView view) {
        if (view.canGoBack()) {
            mBack.setAlpha(enable);
        } else {
            mBack.setAlpha(disable);
        }
        if (view.canGoForward()) {
            mForward.setAlpha(enable);
        } else {
            mForward.setAlpha(disable);
        }
        if (view.getUrl() != null && view.getUrl().equalsIgnoreCase(mHomeUrl)) {
            mHome.setAlpha(disable);
            mHome.setEnabled(false);
        } else {
            mHome.setAlpha(enable);
            mHome.setEnabled(true);
        }
    }

    private void init() {
        mWebView = new X5WebView(this, null);

        mViewParent.addView(mWebView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.FILL_PARENT,
                FrameLayout.LayoutParams.FILL_PARENT));

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // mTestHandler.sendEmptyMessage(MSG_OPEN_TEST_URL);
                mTestHandler.sendEmptyMessageDelayed(MSG_OPEN_TEST_URL, 5000);// 5s?
                if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 16) {
                    changGoForwardButton(view);
                }
                currentUrl = url;
                /* mWebView.showLog("test Log"); */
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsConfirm(WebView arg0, String arg1, String arg2,
                                       JsResult arg3) {
                return super.onJsConfirm(arg0, arg1, arg2, arg3);
            }

            View myVideoView;
            View myNormalView;
            IX5WebChromeClient.CustomViewCallback callback;

            /**
             * 全屏播放配置
             */
            @Override
            public void onShowCustomView(View view,
                                         IX5WebChromeClient.CustomViewCallback customViewCallback) {
//                FrameLayout normalView = (FrameLayout) findViewById(R.id.web_filechooser);
//                ViewGroup viewGroup = (ViewGroup) normalView.getParent();
//                viewGroup.removeView(normalView);
//                viewGroup.addView(view);
//                myVideoView = view;
//                myNormalView = normalView;
//                callback = customViewCallback;
            }

            @Override
            public void onHideCustomView() {
                if (callback != null) {
                    callback.onCustomViewHidden();
                    callback = null;
                }
                if (myVideoView != null) {
                    ViewGroup viewGroup = (ViewGroup) myVideoView.getParent();
                    viewGroup.removeView(myVideoView);
                    viewGroup.addView(myNormalView);
                }
            }

            @Override
            public boolean onJsAlert(WebView arg0, String arg1, String arg2,
                                     JsResult arg3) {
                /**
                 * 这里写入你自定义的window alert
                 */
                return super.onJsAlert(null, arg1, arg2, arg3);
            }
        });

        // 添加js交互接口类，并起别名 AndroidWebView
        mWebView.addJavascriptInterface(new JavascriptInterface(this), "android");
        //设置默认缩放比
        if (!Urls.ZTSJ_URL.equals(mHomeUrl)) {
            mWebView.setInitialScale(100);
        }

        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String arg0, String arg1, String arg2,
                                        String arg3, long arg4) {
                TbsLog.d(TAG, "url: " + arg0);
                new AlertDialog.Builder(TBXActivity.this)
                        .setTitle("allow to download？")
                        .setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Toast.makeText(
                                                TBXActivity.this,
                                                "fake message: i'll download...",
                                                Toast.LENGTH_LONG).show();
                                    }
                                })
                        .setNegativeButton("no",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub
                                        Toast.makeText(
                                                TBXActivity.this,
                                                "fake message: refuse download...",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .setOnCancelListener(
                                new DialogInterface.OnCancelListener() {

                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        // TODO Auto-generated method stub
                                        Toast.makeText(
                                                TBXActivity.this,
                                                "fake message: refuse download...",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }).show();
            }
        });

        WebSettings webSetting = mWebView.getSettings();
        webSetting.setAllowFileAccess(true);
//        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0).getPath());
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // webSetting.setPreFectch(true);
        long time = System.currentTimeMillis();
        if (mIntentUrl == null) {
            mWebView.loadUrl(mHomeUrl);
        } else {
            mWebView.loadUrl(mIntentUrl.toString());
        }
        TbsLog.d("time-cost", "cost time: "
                + (System.currentTimeMillis() - time));
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }

    private void initBtnListenser() {
        mBack = (ImageButton) findViewById(R.id.btnBack1);
        mForward = (ImageButton) findViewById(R.id.btnForward1);
        mExit = (ImageButton) findViewById(R.id.btnExit1);
        mHome = (ImageButton) findViewById(R.id.btnHome1);
        mMore = (ImageButton) findViewById(R.id.btnMore);

        if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 16) {
            mBack.setAlpha(disable);
            mForward.setAlpha(disable);
            mHome.setAlpha(disable);
        }

        mHome.setEnabled(false);

        mBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mWebView != null && mWebView.canGoBack()) {
                    mWebView.goBack();
                }
            }
        });

        mForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWebView != null && mWebView.canGoForward()) {
                    mWebView.goForward();
                }
            }
        });

        mMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TBXActivity.this, "not completed",
                        Toast.LENGTH_LONG).show();
            }
        });

        mHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWebView != null) {
                    mWebView.loadUrl(mHomeUrl);
                }
            }
        });

        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Process.killProcess(Process.myPid());
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!TextUtils.isEmpty(currentUrl) && currentUrl.contains(HOME_URL)) {
            finish();
            return;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mWebView == null) {
            finish();
        }

        //是否包含根目录
//        boolean contains = currentUrl.contains(HOME_URL) || currentUrl.contains(EXAM_LIST) || currentUrl.contains(mHomeUrl);
//
//        if ((keyCode == KeyEvent.KEYCODE_BACK && !TextUtils.isEmpty(currentUrl) && contains) || mHomeUrl.equals(Urls.ZTSJ_URL)) {
//            finish();
//            return true;
//        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView != null && mWebView.canGoBack()) {
                mWebView.goBack();
                if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 16) {
                    changGoForwardButton(mWebView);
                }
                return true;
            } else {
                finish();
                return true;
//                return super.onKeyDown(keyCode, event);
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TbsLog.d(TAG, "onActivityResult, requestCode:" + requestCode
                + ",resultCode:" + resultCode);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    if (null != uploadFile) {
                        Uri result = data == null || resultCode != RESULT_OK ? null
                                : data.getData();
                        uploadFile.onReceiveValue(result);
                        uploadFile = null;
                    }
                    break;
                default:
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (null != uploadFile) {
                uploadFile.onReceiveValue(null);
                uploadFile = null;
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mTestHandler != null) {
            mTestHandler.removeCallbacksAndMessages(null);
        }
        if (mWebView != null) {
            mWebView.destroy();
        }
        super.onDestroy();
    }

    public static final int MSG_OPEN_TEST_URL = 0;
    public static final int MSG_INIT_UI = 1;
    private final int mUrlStartNum = 0;
    private int mCurrentUrl = mUrlStartNum;

    private Handler mTestHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OPEN_TEST_URL:
                    if (!mNeedTestPage) {
                        return;
                    }

                    String testUrl = "file:///sdcard/outputHtml/html/"
                            + Integer.toString(mCurrentUrl) + ".html";
                    if (mWebView != null) {
                        mWebView.loadUrl(testUrl);
                    }

                    mCurrentUrl++;
                    break;

                case MSG_INIT_UI:
                    init();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * Native JS 接口
     */
    @SuppressLint("JavascriptInterface")
    public class JavascriptInterface {
        /**
         * 构造方法
         */
        private Context context;

        public JavascriptInterface(Context context) {
            this.context = context;
        }

        /**
         * JS 调用 Android
         */
        @android.webkit.JavascriptInterface
        public void callAndroid() {
            finish();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        try {
            super.onConfigurationChanged(newConfig);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置黑色字体
     */
    private void setBlackFont() {
        ImmersionBar.with(this)
                .transparentStatusBar()  //透明状态栏，不写默认透明色
//                .transparentNavigationBar()  //透明导航栏，不写默认黑色(设置此方法，fullScreen()方法自动为true)
//                .transparentBar()             //透明状态栏和导航栏，不写默认状态栏为透明色，导航栏为黑色（设置此方法，fullScreen()方法自动为true）
                .statusBarColor(R.color.color_FFF5E5)     //状态栏颜色，不写默认透明色
//                .navigationBarColor(R.color.colorPrimary) //导航栏颜色，不写默认黑色
//                .barColor(R.color.colorPrimary)  //同时自定义状态栏和导航栏颜色，不写默认状态栏为透明色，导航栏为黑色
                .fitsSystemWindows(true)
                .statusBarAlpha(0.3f)  //状态栏透明度，不写默认0.0f
                .navigationBarAlpha(0.4f)  //导航栏透明度，不写默认0.0F
                .barAlpha(0.3f)  //状态栏和导航栏透明度，不写默认0.0f
                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                .navigationBarDarkIcon(true) //导航栏图标是深色，不写默认为亮色
                .autoDarkModeEnable(true) //自动状态栏字体和导航栏图标变色，必须指定状态栏颜色和导航栏颜色才可以自动变色哦
                .autoStatusBarDarkModeEnable(true, 0.2f) //自动状态栏字体变色，必须指定状态栏颜色才可以自动变色哦
                .autoNavigationBarDarkModeEnable(true, 0.2f) //自动导航栏图标变色，必须指定导航栏颜色才可以自动变色哦
                .flymeOSStatusBarFontColor(R.color.black)  //修改flyme OS状态栏字体颜色
//                .reset()  //重置所以沉浸式参数
                .init();  //必须调用方可应用以上所配置的参数
    }
}
