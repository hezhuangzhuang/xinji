package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.orhanobut.logger.Logger;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.bean.response.VideoBean;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.widget.VideoListener;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;


/**
 * 视频详情
 */
public class VideoDetailsActivity extends BaseActivity {
    private TextView tvTitle;
    private TextView tvTime;
    private TextView tvWebsite;
    private WebView webview;

    private WeakReference<VideoDetailsActivity> weakReference;

    private VideoBean videoBean;

    private StandardGSYVideoPlayer mVideoView;
    private OrientationUtils orientationUtils;

    /**
     * 是否播放
     */
    private boolean isPlay = false;

    /**
     * 是否暂停
     */
    private boolean isPause = false;

    /**
     * 初始化videoview的配置
     */
    private void initVideoViewConfig() {
        //设置旋转
        orientationUtils = new OrientationUtils(this, mVideoView);
        //是否开启自动旋转
        mVideoView.setRotateViewAuto(false);

        //是否可以滑动调整
        mVideoView.setIsTouchWiget(true);

        mVideoView.setVideoAllCallBack(new VideoListener() {
            @Override
            public void onPrepared(String url, Object... objects) {
                super.onPrepared(url, objects);
                //开始播放了才能旋转和全屏
                orientationUtils.setEnable(true);
                isPlay = true;
            }

            @Override
            public void onAutoComplete(String url, Object... objects) {
                super.onAutoComplete(url, objects);
            }

            @Override
            public void onEnterFullscreen(String url, Object... objects) {
                super.onEnterFullscreen(url, objects);
            }

            @Override
            public void onQuitFullscreen(String url, Object... objects) {
                super.onQuitFullscreen(url, objects);
                Logger.d("***** onQuitFullscreen **** ");
                //列表返回的样式判断
                orientationUtils.backToProtVideo();
            }

            @Override
            public void onPlayError(String url, Object... objects) {
                super.onPlayError(url, objects);
                ToastHelper.showShort("播放失败");
                startPlay("https://media6.smartstudy.com/ae/07/3997/2/dest.m3u8");
            }
        });

        //设置返回按键功能
        mVideoView.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //设置全屏按键功能
        mVideoView.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横盘
                orientationUtils.resolveByClick();
                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                mVideoView.startWindowFullscreen(VideoDetailsActivity.this, true, true);
            }
        });

        //锁屏事件
        mVideoView.setLockClickListener(new LockClickListener() {
            @Override
            public void onClick(View view, boolean lock) {
                //配合下方的onConfigurationChanged
                orientationUtils.setEnable(!lock);
            }
        });

        String newUrl = getIntent().getStringExtra(VIDEO_URL);
        newUrl = "http://192.168.16.236:10014/xjdj/videoFolder/life/20191106143754805/20191106143754805.mp4";
        startPlay(newUrl);
    }

    private void startPlay(String newUrl) {
        mVideoView.setUp(newUrl, false, "这是标题");
        //开始自动播放
        mVideoView.startPlayLogic();
    }

    private GSYVideoPlayer getCurPlay() {
        if (mVideoView.getFullWindowPlayer() != null) {
            return mVideoView.getFullWindowPlayer();
        } else {
            return mVideoView;
        }
    }

    @Override
    public void onBackPressed() {
        orientationUtils.backToProtVideo();
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        //释放所有
        mVideoView.setVideoAllCallBack(null);
        getCurPlay().release();
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();

        setBlackFont(true);

        getCurPlay().onVideoResume();
        isPause = false;
    }

    @Override
    public void onPause() {
        super.onPause();

        getCurPlay().onVideoPause();
        isPause = true;
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().post(new EventMessage(Messages.UPDATE_VIDEO, videoBean));

        super.onDestroy();
        getCurPlay().release();
        orientationUtils.releaseListener();
    }

    public static final String VIDEO_URL = "VIDEO_URL";

    public static void startActivity(Context context, String url) {
        Intent intent = new Intent(context, VideoDetailsActivity.class);
        intent.putExtra(VIDEO_URL, url);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvWebsite = (TextView) findViewById(R.id.tv_website);
        webview = (WebView) findViewById(R.id.webview);

        mVideoView = findViewById(R.id.videoView);
    }

    @Override
    protected void initData() {
        weakReference = new WeakReference<VideoDetailsActivity>(this);

        initVideoViewConfig();

        findViewById(R.id.bt_switch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mType++;
                mType %= 4;
                ToastHelper.showShort("mType-->" + mType);
                if (mType == 1) {
                    ToastHelper.showShort("模式16:9");
                    GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_16_9);
                } else if (mType == 2) {
                    ToastHelper.showShort("模式4:3");
                    GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_4_3);
                } else if (mType == 3) {
                    ToastHelper.showShort("模式全屏");
                    GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL);
                } else if (mType == 4) {
                    ToastHelper.showShort("模式拉伸全屏");
                    GSYVideoType.setShowType(GSYVideoType.SCREEN_MATCH_FULL);
                } else if (mType == 0) {
                    ToastHelper.showShort("模式默认比例");
                    GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT);
                }
                changeTextureViewShowType();
                if (mVideoView.getRenderProxy() != null) {
                    mVideoView.getRenderProxy().requestLayout();
                }
            }
        });
    }

    private int mType = 0;

    /**
     * 调整TextureView去适应比例变化
     */
    protected void changeTextureViewShowType() {
        if (mVideoView.getRenderProxy() != null) {
            int params = getTextureParams();
            ViewGroup.LayoutParams layoutParams = mVideoView.getRenderProxy().getLayoutParams();
            layoutParams.width = params;
            layoutParams.height = params;
            mVideoView.getRenderProxy().setLayoutParams(layoutParams);
        }
    }

    /**
     * 获取布局参数
     *
     * @return
     */
    protected int getTextureParams() {
        boolean typeChanged = (GSYVideoType.getShowType() != GSYVideoType.SCREEN_TYPE_DEFAULT);
        return (typeChanged) ? ViewGroup.LayoutParams.WRAP_CONTENT : ViewGroup.LayoutParams.MATCH_PARENT;
    }


    @Override
    protected void setListener() {

    }

    private void setWebContent() {
        StringBuilder sb = new StringBuilder();
        sb.append(getHtmlData(videoBean.context));
        webview.loadDataWithBaseURL(null, sb.toString(), "text/html", "utf-8", null);
    }

    private String getHtmlData(String bodyHTML) {
        String head = "<head>"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> "
                + "<style>img{max-width: 100%; width:auto; height:auto;}</style>"
                + "</head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }


    /**
     * 设置黑色字体
     */
    private void setBlackFont(boolean fits) {
        ImmersionBar.with(weakReference.get())
                .transparentStatusBar()  //透明状态栏，不写默认透明色
//                .transparentNavigationBar()  //透明导航栏，不写默认黑色(设置此方法，fullScreen()方法自动为true)
//                .transparentBar()             //透明状态栏和导航栏，不写默认状态栏为透明色，导航栏为黑色（设置此方法，fullScreen()方法自动为true）
//                .statusBarColor(R.color.colorPrimary)     //状态栏颜色，不写默认透明色
//                .navigationBarColor(R.color.colorPrimary) //导航栏颜色，不写默认黑色
//                .barColor(R.color.colorPrimary)  //同时自定义状态栏和导航栏颜色，不写默认状态栏为透明色，导航栏为黑色
                .fitsSystemWindows(fits)
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        exoPlayerManager.onConfigurationChanged(newConfig);//横竖屏切换

        if (isPlay && !isPause) {
            mVideoView.onConfigurationChanged(this, newConfig, orientationUtils);
        }

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setBlackFont(false);
        } else {
            setBlackFont(true);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_details;
    }

}
