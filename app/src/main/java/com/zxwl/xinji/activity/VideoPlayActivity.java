package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.View;

import com.gyf.immersionbar.ImmersionBar;
import com.orhanobut.logger.Logger;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.Urls;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.widget.VideoListener;

import me.jessyan.autosize.internal.CancelAdapt;

/**
 * 视频播放界面
 */
public class VideoPlayActivity extends BaseActivity implements CancelAdapt {
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
        mVideoView.setAutoFullWithSize(true);

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
                mVideoView.startWindowFullscreen(VideoPlayActivity.this, true, true);
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

        startPlay(Urls.GSK_URL);
    }

    private void startPlay(String newUrl) {
        mVideoView.setUp(newUrl, false, "");
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
        super.onDestroy();
        try{
            getCurPlay().release();
            orientationUtils.releaseListener();
        }catch (Exception e){

        }
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, VideoPlayActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        mVideoView = findViewById(R.id.videoView);
    }

    @Override
    protected void initData() {
        ImmersionBar.with(this)
                .fitsSystemWindows(false)
                .init();  //必须调用方可应用以上所配置的参数
        initVideoViewConfig();
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_play;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (isPlay && !isPause) {
            mVideoView.onConfigurationChanged(this, newConfig, orientationUtils);
        }

    }
}
