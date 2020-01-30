package com.zxwl.frame.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.opensdk.callmgr.CallConstant;
import com.huawei.opensdk.callmgr.CallInfo;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.callmgr.VideoMgr;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.videoengine.ViERenderer;
import com.zxwl.ecsdk.common.UIConstants;
import com.zxwl.ecsdk.logic.CallFunc;
import com.zxwl.frame.R;
import com.zxwl.frame.adapter.AddSiteAdapter;
import com.zxwl.frame.service.AudioStateWatchService;
import com.zxwl.frame.utils.AppManager;
import com.zxwl.frame.utils.DensityUtil;
import com.zxwl.frame.utils.DragFrameLayout;
import com.zxwl.frame.utils.StatusBarUtils;
import com.zxwl.frame.utils.TimerCount;
import com.zxwl.frame.utils.sharedpreferences.PreferencesHelper;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

import me.jessyan.autosize.internal.CancelAdapt;

/**
 * 视频界面
 */
public class VideoActivity extends BaseLibActivity implements LocBroadcastReceiver, View.OnClickListener , CancelAdapt {
    private TextView tvTopTitle;
    private ImageView ivRightOperate;
    /*顶部按钮--end*/

    /*视频界面--*/
    private FrameLayout mRemoteView;
    private DragFrameLayout mLocalView;
    private FrameLayout mHideView;
    /*视频界面--end*/

    /*会控按钮--*/
    private TextView tvHangUp;
    private TextView tvMic;
    private TextView tvMute;
    /*会控按钮--end*/

    //计时器
    private TextView tvTimeCount;
    private TimerCount count;

    private static final int ADD_LOCAL_VIEW = 101;

    private String[] mActions = new String[]{
            CustomBroadcastConstants.ACTION_CALL_END,
            CustomBroadcastConstants.ADD_LOCAL_VIEW,
            CustomBroadcastConstants.DEL_LOCAL_VIEW
    };

    private CallInfo mCallInfo;
    private int mCallID;
    private Object thisVideoActivity = this;

    private int mCameraIndex = CallConstant.FRONT_CAMERA;

    private CallMgr mCallMgr;
    private CallFunc mCallFunc;
    private MeetingMgr instance;


    /*会控顶部*/
    private ImageView ivBg;
    private RelativeLayout llTopControl;
    private LinearLayout llBottomControl;
    /*会控顶部-end*/

    private ImageView ivBackOperate;

    private boolean showControl = true;//是否显示控制栏

    @Override
    protected void findViews() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivRightOperate = (ImageView) findViewById(R.id.iv_right_operate);
        mRemoteView = (FrameLayout) findViewById(R.id.conf_share_layout);
        mLocalView = (DragFrameLayout) findViewById(R.id.conf_video_small_logo);
        mHideView = (FrameLayout) findViewById(R.id.hide_video_view);
        tvHangUp = (TextView) findViewById(R.id.tv_hang_up);
        tvMic = (TextView) findViewById(R.id.tv_mic);
        tvMute = (TextView) findViewById(R.id.tv_mute);

        llTopControl = (RelativeLayout) findViewById(R.id.ll_top_control);
        llBottomControl = (LinearLayout) findViewById(R.id.ll_bottom_control);
        ivBg = (ImageView) findViewById(R.id.iv_bg);

        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);

        tvTimeCount = findViewById(R.id.tv_timeCount);
    }

    @Override
    protected void initData() {
        //结束掉等待的对话框
        AppManager.getInstance().finishActivity(LoadingActivity.class);

        if (Build.VERSION.SDK_INT < 28) {
            StatusBarUtils.setTransparent(this);
        }

        ivRightOperate.setVisibility(View.GONE);
        ivRightOperate.setImageResource(R.mipmap.icon_add);

        ivBackOperate.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        //是否是会议
//        mCallInfo = (CallInfo) intent.getSerializableExtra(UIConstants.CALL_INFO);
        mCallInfo = PreferencesHelper.getData(UIConstants.CALL_INFO, CallInfo.class);

        this.mCallID = mCallInfo.getCallID();

        mCallMgr = CallMgr.getInstance();
        mCallFunc = CallFunc.getInstance();
        instance = MeetingMgr.getInstance();

        //是否静音
        setMicStatus();

        count = new TimerCount(this);
        count.setContainer(tvTimeCount);
        count.start();
    }

    private long last = 0;

    @Override
    protected void setListener() {
        tvHangUp.setOnClickListener(this);
        tvMic.setOnClickListener(this);
        tvMute.setOnClickListener(this);

        ivRightOperate.setOnClickListener(this);
        ivBg.setOnClickListener(this);
        ivBackOperate.setOnClickListener(this);

        mLocalView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        last = System.currentTimeMillis();
                        break;

                    case MotionEvent.ACTION_UP:
                        long s = System.currentTimeMillis() - last;
                        if (s < 100) {
                            changeShowView();
                            return true;
                        }
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_conf_new;
    }

    @Override
    protected void onResume() {
        super.onResume();

        showControl = llBottomControl.getVisibility() == View.VISIBLE && llTopControl.getVisibility() == View.VISIBLE;

        LocBroadcast.getInstance().registerBroadcast(this, mActions);
        addSurfaceView(false);

        //是否开启画面自动旋转
        setAutoRotation(this, true, "148");

        //如果不是扬声器则切换成扬声器
//        setLoudSpeaker();
        AudioStateWatchService.suitCurrAudioDevice();

        //设置扬声器的状态
        setSpeakerStatus();

        setLocalView();

//        grayFixed(2000);
    }

    /**
     * 尝试修正灰屏问题
     * @param delay 重试延迟
     */
    private void grayFixed(int delay) {
        mLocalView.addView(getLoadingView());
        mRemoteView.addView(getLoadingView());
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new TimerTask() {
                    @Override
                    public void run() {

                        if (VideoMgr.getInstance() == null
                                || VideoMgr.getInstance().getRemoteVideoView() == null
                                || VideoMgr.getInstance().getLocalVideoView() == null) {
                            return;
                        }

                        VideoMgr.getInstance().getRemoteVideoView().setZOrderMediaOverlay(false);
                        VideoMgr.getInstance().getLocalVideoView().setZOrderMediaOverlay(true);

                        addSurfaceView(mRemoteView, VideoMgr.getInstance().getRemoteVideoView());
                        addSurfaceView(mLocalView, VideoMgr.getInstance().getLocalVideoView());

                        for (int i = mLocalView.getChildCount(); i > 1; i--) {
                            mLocalView.removeViewAt(i);
                        }
                        for (int i = mRemoteView.getChildCount(); i > 1; i--) {
                            mRemoteView.removeViewAt(i);
                        }
                    }
                });
            }
        }, delay);
    }

    private ObjectAnimator ra = null;

    private View getLoadingView() {
        //创建父布局
        LinearLayout content = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
        content.setGravity(Gravity.CENTER);
        content.setLayoutParams(layoutParams);
        content.setBackgroundColor(Color.parseColor("#000000"));

        //创建加载view
        ImageView iv = new ImageView(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-2, -2);
        iv.setLayoutParams(params);
        iv.setImageResource(R.mipmap.icon_loading);

        //创建动态效果
        ra = ObjectAnimator.ofFloat(iv, "rotation", 0f, 360f);
        ra.setDuration(1500);
        ra.setRepeatCount(ObjectAnimator.INFINITE);
        ra.setInterpolator(new LinearInterpolator());
        ra.start();

        //子view添加到父布局中
        content.addView(iv);

        return content;

    }

    private void setLocalView() {
        int virtualBarHeigh = 0;
        if (Build.VERSION.SDK_INT < 28) {
            virtualBarHeigh = DensityUtil.getNavigationBarHeight(this) + DensityUtil.dip2px(20);
        } else {
            virtualBarHeigh = DensityUtil.dip2px(20);
        }
        //显示
        mLocalView.animate().translationX(0 - virtualBarHeigh).setDuration(100).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocBroadcast.getInstance().unRegisterBroadcast(this, mActions);
        mHandler.removeCallbacksAndMessages(null);
        setAutoRotation(this, false, "163");

        PreferencesHelper.saveData(UIConstants.IS_AUTO_ANSWER, false);

        callClosed();

        reSetRenderer();

        //清理缓存
//        DataCleanManager.cleanApplicationData(LocContext.getContext(), "");
        count.end();
        count = null;
    }


    private void reSetRenderer(){
        Log.e("hme-video", "清空渲染器");
        //清空渲染器信息
        ViERenderer viERenderer = new ViERenderer();
        try {
            Field g_localRendererField = viERenderer.getClass().getDeclaredField("g_localRenderer");
            g_localRendererField.setAccessible(true);
            g_localRendererField.set(viERenderer, null);

            Field g_localRenderField = viERenderer.getClass().getDeclaredField("g_localRender");
            g_localRenderField.setAccessible(true);
            g_localRenderField.set(viERenderer, null);

            Field renderSysLockField = viERenderer.getClass().getDeclaredField("renderSysLock");
            renderSysLockField.setAccessible(true);
            renderSysLockField.set(viERenderer, new ReentrantLock());


            Field g_remoteRenderField = viERenderer.getClass().getDeclaredField("g_remoteRender");
            g_remoteRenderField.setAccessible(true);
            g_remoteRenderField.set(viERenderer, new SurfaceView[16]);

            Field listenThreadField = viERenderer.getClass().getDeclaredField("listenThread");
            listenThreadField.setAccessible(true);
            listenThreadField.set(viERenderer, null);

            g_localRendererField = null;
            g_localRenderField = null;
            renderSysLockField = null;
            g_remoteRenderField = null;
            listenThreadField = null;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            viERenderer = null;
        }
    }

    /**
     * 设置为扬声器
     */
    public void setLoudSpeaker() {
        //获取扬声器状态
        //如果不是扬声器则切换成扬声器
        if ((CallConstant.TYPE_LOUD_SPEAKER != CallMgr.getInstance().getCurrentAudioRoute())) {
            CallMgr.getInstance().switchAudioRoute();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ADD_LOCAL_VIEW:
                    addSurfaceView(true);
                    setAutoRotation(thisVideoActivity, true, "184");
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * 更新状态
     */
    public void muteMic() {
        boolean currentMuteStatus = mCallFunc.isMuteStatus();
        if (CallMgr.getInstance().muteMic(mCallID, !currentMuteStatus)) {
            mCallFunc.setMuteStatus(!currentMuteStatus);
            setMicStatus();
        }
    }

    private void setMicStatus() {
        boolean currentMuteStatus = mCallFunc.isMuteStatus();
        //更新状态静音按钮状态
        tvMic.setCompoundDrawablesWithIntrinsicBounds(0, currentMuteStatus ? R.mipmap.icon_mic_status_close : R.mipmap.icon_mic_status_open, 0, 0);
    }

    /**
     * 设置扬声器的图片
     */
    private void setSpeakerStatus() {
        tvMute.setCompoundDrawablesWithIntrinsicBounds(0, isMuteSpeakStatus() ? R.mipmap.icon_mute : R.mipmap.icon_unmute, 0, 0);
    }

    public void videoToAudio() {
        CallMgr.getInstance().delVideo(mCallID);
    }

    public void holdVideo() {
        CallMgr.getInstance().holdVideoCall(mCallID);
    }

    public void videoDestroy() {
        if (null != CallMgr.getInstance().getVideoDevice()) {
            LogUtil.i(UIConstants.DEMO_TAG, "onCallClosed destroy.");
            CallMgr.getInstance().videoDestroy();
        }
    }

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        mCameraIndex = VideoMgr.getInstance().getCurrentCameraIndex() == CallConstant.FRONT_CAMERA ? CallConstant.BACK_CAMERA : CallConstant.FRONT_CAMERA;
        CallMgr.getInstance().switchCamera(mCallID, mCameraIndex);
    }

    public void switchCameraStatus(boolean isCameraClose) {
        if (isCameraClose) {
            CallMgr.getInstance().closeCamera(mCallID);
        } else {
            CallMgr.getInstance().openCamera(mCallID);
        }
    }

    public SurfaceView getHideVideoView() {
        return VideoMgr.getInstance().getLocalHideView();
    }

    public SurfaceView getLocalVideoView() {
        return VideoMgr.getInstance().getLocalVideoView();
    }

    public SurfaceView getRemoteVideoView() {
        return VideoMgr.getInstance().getRemoteVideoView();
    }

    public void setAutoRotation(Object object, boolean isOpen, String line) {
        LogUtil.i(UIConstants.DEMO_TAG, "setAutoRotation-->" + line);
        VideoMgr.getInstance().setAutoRotation(object, isOpen, 1);
    }

    private void addSurfaceView(ViewGroup container, SurfaceView child) {
        if (child == null) {
            grayFixed(500);
            return;
        }
        if (child.getParent() != null) {
            ViewGroup vGroup = (ViewGroup) child.getParent();
            vGroup.removeAllViews();
        }
        container.addView(child);
    }

    private void addSurfaceView(boolean onlyLocal) {
        if (!onlyLocal) {
            addSurfaceView(mRemoteView, getRemoteVideoView());
        }
        addSurfaceView(mLocalView, getLocalVideoView());
        addSurfaceView(mHideView, getHideVideoView());
    }

    /**
     * On call closed.
     */
    private void callClosed() {
        LogUtil.i(UIConstants.DEMO_TAG, "onCallClosed enter.");
//        executorShutDown();
        videoDestroy();
    }

    @Override
    public void onReceive(String broadcastName, Object obj) {
        switch (broadcastName) {
            case CustomBroadcastConstants.ACTION_CALL_END:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callClosed();
                        finish();
                    }
                });
                break;

            case CustomBroadcastConstants.ADD_LOCAL_VIEW:
                mHandler.sendEmptyMessage(ADD_LOCAL_VIEW);
                break;

            case CustomBroadcastConstants.DEL_LOCAL_VIEW:
                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (R.id.tv_hang_up == v.getId()) {
            //结束会议
            mCallMgr.endCall(mCallID);
            finish();
        } else if (R.id.tv_mute == v.getId()) {
            //是否静音喇叭
            //true代表静音
            if (isMuteSpeakStatus()) {
                huaweiOpenSpeaker();
            } else {
                huaweiCloseSpeaker();
            }
        } else if (R.id.tv_mic == v.getId()) {
            //静音
            muteMic();
        } else if (R.id.iv_bg == v.getId()) {
            if (showControl) {
                hideControl();
            } else {
                showControl();
            }
        } else if (R.id.iv_back_operate == v.getId()) {
            switchCamera();
        } else if (R.id.conf_video_small_logo == v.getId()) {
            changeShowView();
        }
    }

    /**
     * true代表静音
     *
     * @return
     */
    private boolean isMuteSpeakStatus() {
        if (0 != mCallID) {
            return CallMgr.getInstance().getMuteSpeakStatus(mCallID);
        } else {
            return false;
        }
    }

    private void huaweiCloseSpeaker() {
        if (0 != mCallID) {
            boolean muteSpeak = CallMgr.getInstance().muteSpeak(mCallID, true);
        }
        setSpeakerStatus();
    }

    private void huaweiOpenSpeaker() {
        if (0 != mCallID) {
            CallMgr.getInstance().muteSpeak(mCallID, false);
        }
        setSpeakerStatus();
    }

    private void showControl() {
        llTopControl.setVisibility(View.VISIBLE);
        getViewAlphaAnimator(llTopControl, 1).start();
        llBottomControl.setVisibility(View.VISIBLE);
        getViewAlphaAnimator(llBottomControl, 1).start();
    }

    private void hideControl() {
        getViewAlphaAnimator(llBottomControl, 0).start();
        getViewAlphaAnimator(llTopControl, 0).start();
    }

    private ViewPropertyAnimator getViewAlphaAnimator(View view, float alpha) {
        ViewPropertyAnimator viewPropertyAnimator = view.animate().alpha(alpha).setDuration(300);
        viewPropertyAnimator.setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(alpha > 0 ? View.VISIBLE : View.GONE);
                showControl = alpha > 0 ? true : false;
            }
        });
        return viewPropertyAnimator;
    }

    /*添加会场*/
    private BottomSheetDialog mBottomSheetDialog;
    private TextView tvAddCancle;
    private TextView tvAddConfirm;

    private RecyclerView rvAddAttendees;
    private AddSiteAdapter addSiteAdapter;
    /*添加会场--end*/

    /**
     * 判断扬声器是否打开
     *
     * @return
     */
    private boolean isOpenSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            //audioManager.setMode(AudioManager.ROUTE_SPEAKER);
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);

            return audioManager.isSpeakerphoneOn();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    int currVolume = 60;

    public void OpenSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            //audioManager.setMode(AudioManager.ROUTE_SPEAKER);
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);

            if (!audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                        AudioManager.STREAM_VOICE_CALL);
                tvMute.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.icon_unmute, 0, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //关闭扬声器
    public void CloseSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                if (audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume,
                            AudioManager.STREAM_VOICE_CALL);

                    tvMute.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.icon_mute, 0, 0);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }


    private int changeNumber;

    private void changeShowView() {
        mRemoteView.removeAllViews();
        mLocalView.removeAllViews();
        changeNumber++;
        if (changeNumber % 2 == 0) {
            VideoMgr.getInstance().getRemoteVideoView().setZOrderMediaOverlay(false);
            VideoMgr.getInstance().getLocalVideoView().setZOrderMediaOverlay(true);

            addSurfaceView(mRemoteView, VideoMgr.getInstance().getRemoteVideoView());
            addSurfaceView(mLocalView, VideoMgr.getInstance().getLocalVideoView());
        } else {
            VideoMgr.getInstance().getRemoteVideoView().setZOrderMediaOverlay(true);
            VideoMgr.getInstance().getLocalVideoView().setZOrderMediaOverlay(false);

            addSurfaceView(mLocalView, VideoMgr.getInstance().getRemoteVideoView());
            addSurfaceView(mRemoteView, VideoMgr.getInstance().getLocalVideoView());
        }
    }
}
