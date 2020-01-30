package com.zxwl.frame.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.huawei.opensdk.callmgr.CallConstant;
import com.huawei.opensdk.callmgr.CallInfo;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.callmgr.VideoMgr;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.sdkwrapper.login.LoginCenter;
import com.huawei.videoengine.ViERenderer;
import com.zxwl.ecsdk.common.UIConstants;
import com.zxwl.ecsdk.logic.CallFunc;
import com.zxwl.ecsdk.utils.IntentConstant;
import com.zxwl.frame.R;
import com.zxwl.frame.adapter.ConfControlAdapter;
import com.zxwl.frame.adapter.onConfControlClickListener;
import com.zxwl.frame.bean.respone.ConfBeanRespone;
import com.zxwl.frame.net.Urls;
import com.zxwl.frame.service.AudioStateWatchService;
import com.zxwl.frame.utils.AppManager;
import com.zxwl.frame.utils.DensityUtil;
import com.zxwl.frame.utils.DragFrameLayout;
import com.zxwl.frame.utils.NotificationUtils;
import com.zxwl.frame.utils.SelectSitePopupWindow;
import com.zxwl.frame.utils.StatusBarUtils;
import com.zxwl.frame.utils.TimerCount;
import com.zxwl.frame.utils.sharedpreferences.PreferencesHelper;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.bean.response.DepartmentBean;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.utils.Messages;

import org.greenrobot.eventbus.EventBus;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import me.jessyan.autosize.internal.CancelAdapt;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

import static com.huawei.opensdk.commonservice.common.LocContext.getContext;

/**
 * 视频会议界面
 */
public class VideoConfActivity extends BaseLibActivity
        implements LocBroadcastReceiver,
        View.OnClickListener,
        CancelAdapt {
    private TextView tvTopTitle;
    private ImageView ivRightOperate;
    /*顶部按钮--end*/

    private FrameLayout mRemoteView;
    private DragFrameLayout mLocalView;
    private FrameLayout mHideView;
    /*视频界面--end*/

    /*会控按钮--*/
    private TextView tvHangUp;
    private TextView tvMic;
    private TextView tvMute;
    /*会控按钮--end*/

    private TextView tvTimeCount;
    private TimerCount count;

    private TextView tvCloseCamera;
    private TextView tvParticipants;

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

    private String peerNumber;//会议接入号

    private String confID;//会议id
    private int callID;//会议id
    private String subject;//会议主题
    private String smcConfId;//smc的会议id，添加会场使用
    private String groupId;//查询会议人员的groupID
    private String TAG = VideoConfActivity.class.getSimpleName();

    private boolean isCreate = false;//会议是否是自己创建的

    private boolean isMic = false; //麦克风外放是否静音
    private boolean isMute = false;//外放是否静音
    private boolean isChair = false;//是否是主席

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

        tvCloseCamera = (TextView) findViewById(R.id.tv_close_camera);
        tvParticipants = (TextView) findViewById(R.id.tv_participants);

        tvCloseCamera.setVisibility(View.VISIBLE);
        tvParticipants.setVisibility(View.VISIBLE);

        tvCloseCamera.setOnClickListener(this);
        tvParticipants.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        //结束掉等待的对话框
        AppManager.getInstance().finishActivity(LoadingActivity.class);

        accountBean = getUserBean();

        if (Build.VERSION.SDK_INT < 28) {
            StatusBarUtils.setTransparent(this);
        }

        ivRightOperate.setImageResource(R.mipmap.icon_add);
        ivRightOperate.setVisibility(View.GONE);

        ivBackOperate.setVisibility(View.VISIBLE);

        Intent intent = getIntent();

        try {
            mCallInfo = PreferencesHelper.getData(UIConstants.CALL_INFO, CallInfo.class);
        } catch (Exception e) {
        }

        confID = intent.getStringExtra(UIConstants.CONF_ID);
        callID = intent.getIntExtra(UIConstants.CALL_ID, -1);
        peerNumber = intent.getStringExtra(UIConstants.PEER_NUMBER);

        this.mCallID = mCallInfo.getCallID();

        try {
            isCreate = PreferencesHelper.getData(UIConstants.IS_CREATE, Boolean.class);
        } catch (Exception e) {
        }

        mCallMgr = CallMgr.getInstance();
        mCallFunc = CallFunc.getInstance();
        instance = MeetingMgr.getInstance();

        count = new TimerCount(this);
        count.setContainer(tvTimeCount);
        count.start();

        //发送notif
        sendNotif(mCallInfo);
    }

    //本地view最后点击的时间
    private long localViewTouchLastTime = 0;

    @Override
    protected void setListener() {
        tvHangUp.setOnClickListener(this);
        tvMic.setOnClickListener(this);
        tvMute.setOnClickListener(this);

        ivBg.setOnClickListener(this);
        ivBackOperate.setOnClickListener(this);
        ivRightOperate.setOnClickListener(this);

        mLocalView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        localViewTouchLastTime = System.currentTimeMillis();
                        break;

                    case MotionEvent.ACTION_UP:
                        long s = System.currentTimeMillis() - localViewTouchLastTime;
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
        AudioStateWatchService.suitCurrAudioDevice();

        setLocalView();

//        grayFixed(2000);
    }

    /**
     * 灰屏修正
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

                        for (int i = 1; i < mLocalView.getChildCount(); i++) {
                            mLocalView.removeViewAt(i);
                        }
                        for (int i = 1; i < mRemoteView.getChildCount(); i++) {
                            mRemoteView.removeViewAt(i);
                        }
                    }
                });
            }
        }, delay);
    }

    private ObjectAnimator ra = null;

    /**
     * 生存缓冲时的遮盖
     *
     * @return
     */
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callClosed();
            }
        });
        super.onDestroy();

        //刷新会议列表界面
        EventBus.getDefault().post(new EventMessage(Messages.REFRESH_RECYCLER, ""));

        LocBroadcast.getInstance().unRegisterBroadcast(this, mActions);
        mHandler.removeCallbacksAndMessages(null);
        setAutoRotation(this, false, "163");

        PreferencesHelper.saveData(UIConstants.IS_AUTO_ANSWER, false);

        MeetingMgr.getInstance().setCurrentConferenceCallID(0);

        callClosed();

        reSetRenderer();

        count.end();
        count = null;
    }

    /**
     * 清空渲染器
     */
    private void reSetRenderer() {
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

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        try {
            int callID = getCallID();
            mCameraIndex = VideoMgr.getInstance().getCurrentCameraIndex() == CallConstant.FRONT_CAMERA ? CallConstant.BACK_CAMERA : CallConstant.FRONT_CAMERA;
            CallMgr.getInstance().switchCamera(callID, mCameraIndex);
        } catch (Exception e) {

        }
    }

    //摄像头是否关闭
    private boolean isCloseCamera = false;

    /**
     * 开关本地摄像头
     */
    public void closeCamera() {
        isCloseCamera = !isCloseCamera;
        if (isCloseCamera) {
            CallMgr.getInstance().closeCamera(mCallID);
            tvCloseCamera.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_camera_status_close, 0, 0);
        } else {
            CallMgr.getInstance().openCamera(mCallID);
            tvCloseCamera.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_camera_status_open, 0, 0);
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
//        LogUtil.i(UIConstants.DEMO_TAG, "setAutoRotation-->" + line);
        VideoMgr.getInstance().setAutoRotation(object, isOpen, 1);
    }

    private void addSurfaceView(ViewGroup container, SurfaceView child) {
        if (child == null) {
            runOnUiThread(new TimerTask() {
                @Override
                public void run() {
//                    Toast.makeText(VideoConfActivity.this, "灰屏修正方法生效", Toast.LENGTH_LONG).show();
                    grayFixed(200);
                }
            });
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

    private void callClosed() {
        LogUtil.i(UIConstants.DEMO_TAG, "onCallClosed enter.");
//        executorShutDown();
        videoDestroy();
    }

    public void videoDestroy() {
        if (null != CallMgr.getInstance().getVideoDevice()) {
            LogUtil.i(UIConstants.DEMO_TAG, "onCallClosed destroy.");
            CallMgr.getInstance().videoDestroy();

            //从会话列表中移除一路会话
            CallMgr.getInstance().removeCallSessionFromMap(callID);
        }
    }

    @Override
    public void onReceive(String broadcastName, Object obj) {
        switch (broadcastName) {
            case CustomBroadcastConstants.ACTION_CALL_END:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callClosed();
                        finishActivity("470");
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

    /**
     * 离开会议
     */
    private void leaveConf(int line) {
        boolean isLeaveResult = false;
        int callID = getCallID();
        if (callID != 0) {
            isLeaveResult = CallMgr.getInstance().endCall(callID);
        }

        int result = MeetingMgr.getInstance().leaveConf();
        if (result != 0) {
            return;
        }
    }

    public void finishActivity(String lineNumber) {
        NotificationUtils.cancelAll();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (R.id.tv_hang_up == v.getId()) {
            //结束会议
            if (isChair) {
                showExitConfDialog();
            } else {
                leaveConf(0);
            }
        } else if (R.id.tv_mute == v.getId()) {
            //是否静音喇叭
            //true代表静音
            if (TextUtils.isEmpty(smcConfId)) {
//                queryConfInfo();
            } else {
                if (isMute) {
                    setSitesQuietRequest(false);
                } else {
                    setSitesQuietRequest(true);
                }
            }
        } else if (R.id.tv_mic == v.getId()) {
            //静音
            if (TextUtils.isEmpty(smcConfId)) {
//                queryConfInfo();
            } else {
                //处于静音
                if (isMic) {
                    setSiteMuteRequest(false, getCurrentSiteUri(), false);
                } else {
                    setSiteMuteRequest(true, getCurrentSiteUri(), false);
                }
            }
        } else if (R.id.iv_bg == v.getId()) {
            if (showControl) {
                hideControl();
            } else {
                showControl();
            }
        } else if (R.id.iv_back_operate == v.getId()) {
            if (changeNumber % 2 == 0) {
                switchCamera();
            }
        } else if (R.id.iv_right_operate == v.getId()) {
            queryConfInfo();
        }//大小画面切换
        else if (R.id.conf_video_small_logo == v.getId()) {
            changeShowView();
        } //关闭摄像头
        else if (R.id.tv_close_camera == v.getId()) {
            closeCamera();
        }//与会人
        else if (R.id.tv_participants == v.getId()) {
            queryConfInfo();
        }
    }

    /**
     * 获取当前账号的号码
     *
     * @return
     */
    private String getCurrentSiteUri() {
        return LoginCenter.getInstance().getSipAccountInfo().getTerminal();
    }

    /**
     * 离开会议的网络请求
     */
    private void leaveConfRequest() {
        RequestParams params = new RequestParams(Urls.BASE_URL + "conf/disconnectSite");
        params.addHeader("Content-Type", "application/json");
        params.addHeader("Accept", "application/json");

        params.addQueryStringParameter("smcConfId", smcConfId);
        params.addQueryStringParameter("siteUri", LoginCenter.getInstance().getSipAccountInfo().getTerminal());

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ConfBeanRespone baseData = new GsonBuilder().create().fromJson(result, ConfBeanRespone.class);

                if (null != baseData && 0 == baseData.code) {
                    PreferencesHelper.saveData(UIConstants.IS_CREATE, false);
                } else {
                    Toast.makeText(VideoConfActivity.this, "离开会议失败,请稍后再试！", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "离开会议失败,稍后再试");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //setRightOperateShow(View.VISIBLE);
                Toast.makeText(VideoConfActivity.this, "离开会议失败,请稍后再试", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "离开会议失败，错误:" + ex.getCause());
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    /**
     * 外放闭音
     */
    private void setSitesQuietRequest(boolean isMuteParam) {
        RequestParams params = new RequestParams(Urls.BASE_URL + "conf/setSitesQuiet");
        params.addHeader("Content-Type", "application/json");
        params.addHeader("Accept", "application/json");

        params.addQueryStringParameter("smcConfId", smcConfId);
        params.addQueryStringParameter("siteUri", LoginCenter.getInstance().getSipAccountInfo().getTerminal());
        params.addQueryStringParameter("isQuiet", String.valueOf(isMuteParam));

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ConfBeanRespone baseData = new GsonBuilder().create().fromJson(result, ConfBeanRespone.class);

                if (null != baseData && 0 == baseData.code) {
                    if (isMuteParam) {
                        //静音成功
                        tvMute.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.icon_mute, 0, 0);
                    } else {
                        //取消静音成功
                        tvMute.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.icon_unmute, 0, 0);
                    }
                    isMute = isMuteParam;
                } else {
                    Toast.makeText(VideoConfActivity.this, baseData.msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //setRightOperateShow(View.VISIBLE);
//                Toast.makeText(VideoConfActivity.this, "外放静音失败，错误:" + ex.getCause(), Toast.LENGTH_SHORT).show();
                Log.i(TAG, "外放静音失败，错误:" + ex.getCause());
            }

            @Override
            public void onCancelled(CancelledException cex) {
//                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {
            }
        });
    }

    /**
     * true代表静音
     *
     * @return
     */
    private boolean isMuteSpeakStatus() {
        int currentConferenceCallID = getCallID();
        if (0 != currentConferenceCallID) {
            return CallMgr.getInstance().getMuteSpeakStatus(currentConferenceCallID);
        } else {
            return false;
        }
    }

    private int getCallID() {
        if (-1 != callID) {
            return callID;
        } else {
            return MeetingMgr.getInstance().getCurrentConferenceCallID();
        }
    }

    /**
     * 显示控制栏
     */
    private void showControl() {
        llTopControl.setVisibility(View.VISIBLE);
        getViewAlphaAnimator(llTopControl, 1).start();
        llBottomControl.setVisibility(View.VISIBLE);
        getViewAlphaAnimator(llBottomControl, 1).start();
    }

    /**
     * 隐藏控制栏
     */
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


    private ProgressDialog dialog;

    private void showProgressDialog() {
        showProgressDialog(this, "");
    }

    private void dismissDialog() {
        dismissProgressDialog(this);
    }

    private Dialog progressDialog;

    public void showProgressDialog(Context context, String content) {
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }

        if (null != progressDialog && progressDialog.isShowing()) {
            return;
        }

        if (null == progressDialog) {
            progressDialog = new Dialog(context, R.style.CustomDialogStyle);
        }

        View dialogView = View.inflate(context, R.layout.dialog_progress, null);
        TextView tvContent = dialogView.findViewById(R.id.tv_content);
        if (TextUtils.isEmpty(content)) {
            tvContent.setVisibility(View.GONE);
        } else {
            tvContent.setText(content);
            tvContent.setVisibility(View.VISIBLE);
        }
        progressDialog.setContentView(dialogView);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        try {
            progressDialog.show();
        } catch (WindowManager.BadTokenException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * 隐藏对话框
     *
     * @return
     */
    public void dismissProgressDialog(Context context) {
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                progressDialog = null;
                return;
            }
        }

        if (progressDialog != null && progressDialog.isShowing()) {
            Context loadContext = progressDialog.getContext();
            if (loadContext != null && loadContext instanceof Activity) {
                if (((Activity) loadContext).isFinishing()) {
                    progressDialog = null;
                    return;
                }
            }
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private List<ConfBeanRespone.DataBean.SiteStatusInfoListBean> existList = new ArrayList<>();

    /**
     * 查询会议详情并显示与会人对话框
     */
    private void queryConfInfo() {
        RequestParams params = new RequestParams(Urls.BASE_URL + "conf/queryBySmcConfIdOrAccessCode");
        params.addHeader("Content-Type", "application/json");
        params.addHeader("Accept", "application/json");

        params.addQueryStringParameter("accessCode", peerNumber);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ConfBeanRespone baseData = new GsonBuilder().create().fromJson(result, ConfBeanRespone.class);
                if (null != baseData && 0 == baseData.code) {
                    //会议中已存在的人员
                    existList.clear();
                    existList.addAll(baseData.data.siteStatusInfoList);

                    //显示会控的对话框
                    showConfControlDialog(existList);

                    //smcconfid
                    smcConfId = baseData.data.smcConfId;

                    //会议名称
                    subject = baseData.data.confName;

                    //截取获得groupId
                    String[] split = subject.split("_");
                    if (null != split && split.length >= 2) {
                        groupId = split[1];
                    }

                    //如果是主席
                    if (!TextUtils.isEmpty(baseData.data.chairUri) && baseData.data.chairUri.equals(LoginCenter.getInstance().getAccount())) {
                        //setRightOperateShow(View.VISIBLE);
                    }

                    repeatCount = 0;
                } else {
                    //setRightOperateShow(View.VISIBLE);
                    Toast.makeText(VideoConfActivity.this, baseData.msg, Toast.LENGTH_SHORT).show();

                    Log.i(TAG, "查询会议详情失,稍后再试");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //setRightOperateShow(View.VISIBLE);
                Log.i(TAG, "查询会议详情失败，错误:" + ex.getCause());
                if (repeatCount < 3) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            repeatCount++;
                            queryConfInfo();
                        }
                    }, 10 * 1000);
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
//                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {

            }
        });
    }

    //错误重新请求次数
    private int repeatCount = 0;

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

    /************************会控对话框-start*****************************/
    private BottomSheetDialog confControlDialog;
    private TextView tvControlCancle;
    private TextView tvControlConfirm;

    private RecyclerView rvControl;
    private ConfControlAdapter confControlAdapter;

    //最后控制的会场
    private int controlPosition = -1;

    /**
     * 显示会控的对话框
     *
     * @param siteList
     */
    private void showConfControlDialog(List<ConfBeanRespone.DataBean.SiteStatusInfoListBean> siteList) {
        WindowManager wm = this.getWindowManager();
        int height = wm.getDefaultDisplay().getHeight();

        if (null == confControlDialog) {
            //构造函数的第二个参数可以设置BottomSheetDialog的主题样式
            confControlDialog = new BottomSheetDialog(this);
            //导入底部reycler布局
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_conf_control, null, false);

            rvControl = view.findViewById(R.id.rv_add_attendees);

            confControlAdapter = new ConfControlAdapter(this, siteList);
            //会控操作
            confControlAdapter.setRecyclerClick(new onConfControlClickListener() {
                @Override
                public void onClick(int position) {

                }

                @Override
                public void onHangUp(int position) {
                    controlPosition = position;
                    ConfBeanRespone.DataBean.SiteStatusInfoListBean site = confControlAdapter.getDatas().get(position);
                    setSiteDisconnectRequest(site.siteUri);
                }

                @Override
                public void onMic(int position) {
                    controlPosition = position;
                    ConfBeanRespone.DataBean.SiteStatusInfoListBean site = confControlAdapter.getDatas().get(position);

                    if (site.siteStatus == 2) {
                        setSiteMuteRequest(site.microphoneStatus == 1, site.siteUri, true);
                    }
                }

                @Override
                public void onBroadcast(int position) {
                    controlPosition = position;
                    ConfBeanRespone.DataBean.SiteStatusInfoListBean site = confControlAdapter.getDatas().get(position);
                    setSiteBroadcastRequest(!(site.broadcastStatus == 1), site.siteUri);
                }

                @Override
                public void onCall(int position) {
                    controlPosition = position;
                    ConfBeanRespone.DataBean.SiteStatusInfoListBean site = confControlAdapter.getDatas().get(position);
                    setSiteCallRequest(site.siteUri);
                }
            });
            rvControl.setLayoutManager(new LinearLayoutManager(this));
            rvControl.setAdapter(confControlAdapter);

            tvControlCancle = view.findViewById(R.id.tv_add_cancle);
            tvControlConfirm = view.findViewById(R.id.tv_add_confirm);
            TextView tvLable = view.findViewById(R.id.tv_lable);
            tvLable.setText("与会列表");

            tvControlCancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confControlDialog.dismiss();
                }
            });

            //如果是管理员则显示添加会场
            if (accountBean.checkAdmin == 1 && accountBean.level != 3) {
                tvControlConfirm.setVisibility(View.VISIBLE);
            } else {
                tvControlConfirm.setVisibility(View.INVISIBLE);
            }


            tvControlConfirm.setText("添加会场");
            tvControlConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confControlDialog.dismiss();
                    //查询
                    if (null != addSiteDialog) {
                        showAddSiteDialog();
                    } else {
                        //
                        if (1 == accountBean.level) {
                            getDepartmentList(Integer.valueOf(accountBean.unitId));
                        } //如果是乡镇
                        else if (2 == accountBean.level) {
                            //添加辛集市在左边
                            DepartmentBean departmentBean = new DepartmentBean();
                            departmentBean.id = accountBean.unitId;
                            departmentBean.departmentName = accountBean.unitName;
                            leftDepartments = new ArrayList<>();
                            leftDepartments.add(0, departmentBean);

                            initCityDialog(leftDepartments, new ArrayList<>());
                        }
                    }
                }
            });
            confControlDialog.setContentView(view);
            try {
                ViewGroup parent = (ViewGroup) view.getParent();
                parent.setBackgroundColor(ContextCompat.getColor(this, R.color.tran));
                // ViewGroup.LayoutParams layoutParams = parent.getLayoutParams();
                // layoutParams.width = ((int) (0.5 * DensityUtil.getScreenWidth(this)));
                // parent.setLayoutParams(layoutParams);
            } catch (Exception e) {
                e.printStackTrace();
            }

            BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) view.getParent());
            //设置默认弹出高度为屏幕的0.4倍
//          mBehavior.setPeekHeight((int) (0.4 * height));
            mBehavior.setPeekHeight((int) (height));

            //设置点击dialog外部不消失
            confControlDialog.setCanceledOnTouchOutside(false);
        } else {
            confControlAdapter.replceData(siteList);
        }

        if (!confControlDialog.isShowing()) {
            confControlDialog.show();
        } else {
            confControlDialog.dismiss();
        }
    }
    /************************会控对话框-end*****************************/

    /**
     * 麦克风闭音
     *
     * @param isMicParam true：静音
     * @param siteUri    静音的会场号码
     * @param isList     true:通过会控对话框控制
     */
    private void setSiteMuteRequest(boolean isMicParam, String siteUri, boolean isList) {
        RequestParams params = new RequestParams(Urls.BASE_URL + "conf/setSiteMute");
        params.addHeader("Content-Type", "application/json");
        params.addHeader("Accept", "application/json");

        params.addQueryStringParameter("smcConfId", smcConfId);
        params.addQueryStringParameter("siteUri", siteUri);
        params.addQueryStringParameter("isMute", String.valueOf(isMicParam));

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ConfBeanRespone baseData = new GsonBuilder().create().fromJson(result, ConfBeanRespone.class);

                if (null != baseData && 0 == baseData.code) {
                    //如果是通过列表控制的则刷新列表
                    if (isList) {

                        //如果是通过列表控制的则刷新列表
                        confControlAdapter.getDatas().get(controlPosition).microphoneStatus = isMicParam ? 0 : 1;
                        confControlAdapter.notifyDataSetChanged();
                    }
                    if (getCurrentSiteUri().equals(siteUri)) {
                        if (isMicParam) {
                            //静音成功
                            tvMic.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.icon_mic_status_close, 0, 0);
                        } else {
                            //取消静音成功
                            tvMic.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.icon_mic_status_open, 0, 0);
                        }
                        isMic = isMicParam;
                    }
                } else {
                    Toast.makeText(VideoConfActivity.this, baseData.msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //setRightOperateShow(View.VISIBLE);
//                Toast.makeText(VideoConfActivity.this, "麦克风静音失败，错误:" + ex.getCause(), Toast.LENGTH_SHORT).show();
                Log.i(TAG, "麦克风静音失败，错误:" + ex.getCause());
            }

            @Override
            public void onCancelled(CancelledException cex) {
//                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 广播会场
     *
     * @param isBroadcast true：广播
     * @param siteUri     广播的会场号码
     *                    confAction_setBroadcastSite.action
     */
    private void setSiteBroadcastRequest(boolean isBroadcast, String siteUri) {
        RequestParams params = new RequestParams(Urls.BASE_URL + "conf/setBroadcastSite");
        params.addHeader("Content-Type", "application/json");
        params.addHeader("Accept", "application/json");

        params.addQueryStringParameter("smcConfId", smcConfId);
        params.addQueryStringParameter("siteUri", siteUri);
        params.addQueryStringParameter("isBroadcast", String.valueOf(isBroadcast));

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ConfBeanRespone baseData = new GsonBuilder().create().fromJson(result, ConfBeanRespone.class);

                if (null != baseData && 0 == baseData.code) {
                    for (int i = 0; i < confControlAdapter.getItemCount(); i++) {
                        confControlAdapter.getDatas().get(i).broadcastStatus = 0;
                    }
                    Toast.makeText(VideoConfActivity.this, isBroadcast ? "广播会场成功" : "取消广播会场成功", Toast.LENGTH_SHORT).show();

                    //如果是通过列表控制的则刷新列表
                    confControlAdapter.getDatas().get(controlPosition).broadcastStatus = isBroadcast ? 1 : 0;
                    confControlAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(VideoConfActivity.this, baseData.msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //setRightOperateShow(View.VISIBLE);
                Log.i(TAG, "麦克风静音失败，错误:" + ex.getCause());
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 呼叫会场
     *
     * @param siteUri 呼叫的会场号码
     *                connectSite
     */
    private void setSiteCallRequest(String siteUri) {
        RequestParams params = new RequestParams(Urls.BASE_URL + "conf/connectSite");
        params.addHeader("Content-Type", "application/json");
        params.addHeader("Accept", "application/json");

        params.addQueryStringParameter("smcConfId", smcConfId);
        params.addQueryStringParameter("siteUri", siteUri);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ConfBeanRespone baseData = new GsonBuilder().create().fromJson(result, ConfBeanRespone.class);

                if (null != baseData && 0 == baseData.code) {
                    Toast.makeText(VideoConfActivity.this, "呼叫会场成功", Toast.LENGTH_SHORT).show();

                    //如果是通过列表控制的则刷新列表
                    confControlAdapter.notifyItemChanged(controlPosition);
                } else {
                    Toast.makeText(VideoConfActivity.this, baseData.msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //setRightOperateShow(View.VISIBLE);
//                Toast.makeText(VideoConfActivity.this, "麦克风静音失败，错误:" + ex.getCause(), Toast.LENGTH_SHORT).show();
                Log.i(TAG, "麦克风静音失败，错误:" + ex.getCause());
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 挂断
     *
     * @param siteUri 挂断的会场号码
     *                disconnectSite
     */
    private void setSiteDisconnectRequest(String siteUri) {
        RequestParams params = new RequestParams(Urls.BASE_URL + "conf/disconnectSite");
        params.addHeader("Content-Type", "application/json");
        params.addHeader("Accept", "application/json");

        params.addQueryStringParameter("smcConfId", smcConfId);
        params.addQueryStringParameter("siteUri", siteUri);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ConfBeanRespone baseData = new GsonBuilder().create().fromJson(result, ConfBeanRespone.class);

                if (null != baseData && 0 == baseData.code) {
                    //如果是通过列表控制的则刷新列表
                    //如果是通过列表控制的则刷新列表
                    confControlAdapter.getDatas().get(controlPosition).siteStatus = 3;
                    confControlAdapter.notifyItemChanged(controlPosition);
                } else {
                    Toast.makeText(VideoConfActivity.this, baseData.msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //setRightOperateShow(View.VISIBLE);
//                Toast.makeText(VideoConfActivity.this, "麦克风静音失败，错误:" + ex.getCause(), Toast.LENGTH_SHORT).show();
                Log.i(TAG, "麦克风静音失败，错误:" + ex.getCause());
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {

            }
        });

//        HttpUtils.getInstance(this)
//                .getRetofitClinet()
//                .setBaseUrl(com.zxwl.network.Urls.BASE_URL)
//                .builder(StudyApi.class)
//                .removeSite(smcConfId, "_TN_C1," + selectSiteId)
//                .compose(new CustomCompose())
//                .subscribe(new RxSubscriber<BaseData>() {
//                    @Override
//                    public void onSuccess(BaseData baseData) {
//                        if (BaseData.SUCCESS.equals(baseData.result)) {
//                            Toast.makeText(VideoConfActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
//                            addSiteDialog.clearCheckStatus();
//                            addSiteDialog.dismiss();
//                        }
//                    }
//
//                    @Override
//                    protected void onError(ResponeThrowable responeThrowable) {
//
//                    }
//                });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        moveTaskToBack(true);
//        Intent intent = new Intent("com.hw.select");
//        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        createQueryConfInfoRetryRequest();
    }

    @Override
    protected void onStop() {
        super.onStop();

        unQueryConfInfoSubscribe();
    }

    private Subscription queryConfInfoSubscribe;

    private void createQueryConfInfoRetryRequest() {
        unQueryConfInfoSubscribe();

        queryConfInfoSubscribe = Observable.interval(3, TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        intervalQueryConfInfo();
                    }
                });
    }

    private void unQueryConfInfoSubscribe() {
        if (null != queryConfInfoSubscribe) {
            if (!queryConfInfoSubscribe.isUnsubscribed()) {
                queryConfInfoSubscribe.unsubscribe();
                queryConfInfoSubscribe = null;
            }
        }
    }

    /**
     * 循环查询状态
     */
    private void intervalQueryConfInfo() {
        RequestParams params = new RequestParams(Urls.BASE_URL + "conf/queryBySmcConfIdOrAccessCode");
        params.addHeader("Content-Type", "application/json");
        params.addHeader("Accept", "application/json");

        params.addQueryStringParameter("accessCode", peerNumber);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ConfBeanRespone baseData = new GsonBuilder().create().fromJson(result, ConfBeanRespone.class);
                if (null != baseData && 0 == baseData.code) {
                    //smcconfid
                    smcConfId = baseData.data.smcConfId;

                    //会议名称
                    subject = baseData.data.confName;

                    boolean isConfChair = !TextUtils.isEmpty(baseData.data.chairUri) && baseData.data.chairUri.equals(LoginCenter.getInstance().getAccount());
                    //判断是否是主席
                    if (isConfChair || isCreate) {
                        //等于主席
                        isChair = true;
                    } else {
                        isChair = false;
                    }
                    for (ConfBeanRespone.DataBean.SiteStatusInfoListBean siteBean : baseData.data.siteStatusInfoList) {
                        if (siteBean.siteUri.equals(getCurrentSiteUri())) {
                            tvMic.setCompoundDrawablesWithIntrinsicBounds(0, 1 == siteBean.microphoneStatus ? R.mipmap.icon_mic_status_open : R.mipmap.icon_mic_status_close, 0, 0);
                            tvMute.setCompoundDrawablesWithIntrinsicBounds(0, 1 == siteBean.loudspeakerStatus ? R.mipmap.icon_unmute : R.mipmap.icon_mute, 0, 0);
                            return;
                        }
                    }
                } else {
                    Toast.makeText(VideoConfActivity.this, baseData.msg, Toast.LENGTH_SHORT).show();

                    Log.i(TAG, "查询会议详情失,稍后再试");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //setRightOperateShow(View.VISIBLE);
                Log.i(TAG, "查询会议详情失败，错误:" + ex.getCause());
            }

            @Override
            public void onCancelled(CancelledException cex) {
//                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {

            }
        });
    }

    private LoginBean.AccountBean accountBean;

    private Gson gson = new Gson();

    private LoginBean.AccountBean getUserBean() {
        String userString = PreferenceManager.getDefaultSharedPreferences(this).getString("USER_INFO", "");
        return gson.fromJson(userString, LoginBean.AccountBean.class);
    }

    private List<DepartmentBean> selectList = new ArrayList<>();

    private List<DepartmentBean> leftDepartments;

    private HashMap<Integer, List<DepartmentBean>> allDepartmentMap = new HashMap<>();

    /**
     * 获取组织信息
     */
    private void getDepartmentList(int currentUnitId) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(com.zxwl.network.Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryDepartment(currentUnitId)
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<DepartmentBean>>() {
                    @Override
                    public void onSuccess(BaseData<DepartmentBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<DepartmentBean> tempList = baseData.dataList;
                            List<DepartmentBean> dataList = new ArrayList<>();

                            dataList = tempList;
                            DepartmentBean departmentBean = null;

                            //如果左边的列表等于null，代表第一次请求
                            if (null == leftDepartments) {
                                //设置左边的列表
                                leftDepartments = dataList;

                                departmentBean = new DepartmentBean();
                                departmentBean.departmentName = "全部";
                                departmentBean.id = 0;
                                leftDepartments.add(0, departmentBean);

                                //如果是用户bean为空
                                initCityDialog(leftDepartments, new ArrayList<>());
                            } else {
                                departmentBean = new DepartmentBean();
                                departmentBean.departmentName = "全部";
                                departmentBean.id = 0;
                                dataList.add(0, departmentBean);

                                //存储到map中
                                allDepartmentMap.put(currentUnitId, dataList);

                                addSiteDialog.setRightNewData(dataList);
                            }

                            //
//                            if (null != dataList && dataList.size() > 0) {
//                                //判断是否在已选列表里
//                                for (int i = 0; i < dataList.size(); i++) {
//                                    if (selectList.contains(dataList.get(i))) {
//                                        dataList.get(i).isCheck = true;
//                                    }
//                                }
//                            }
                        } else {
                            Toast.makeText(VideoConfActivity.this, baseData.message, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        Toast.makeText(VideoConfActivity.this, "请求失败,error：" + responeThrowable.getCause().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 添加会场
     */
    private void addSiteToConf(String selectSiteId) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(com.zxwl.network.Urls.CREATE_BASE_URL)
                .builder(StudyApi.class)
                .addSite(smcConfId, "_TN_C1,-" + selectSiteId)
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            Toast.makeText(VideoConfActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                            addSiteDialog.clearCheckStatus();
                            addSiteDialog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), baseData.message, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        Toast.makeText(getApplicationContext(), "添加人员错误:" + responeThrowable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private SelectSitePopupWindow addSiteDialog;

    /**
     * 显示筛选对话框
     */
    private void initCityDialog(List<DepartmentBean> leftData,
                                List<DepartmentBean> rightData) {
        if (null == addSiteDialog) {
            addSiteDialog = new SelectSitePopupWindow(
                    this,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    leftData,
                    rightData,
                    true
            );
            addSiteDialog.setLevel(accountBean.level);
            addSiteDialog.setOnScreenClick(new SelectSitePopupWindow.onScreenClick() {
                @Override
                public void onLeftClick(int cityId) {
                    //判断map中是否包含当前列表
                    if (allDepartmentMap.containsKey(cityId)) {
                        //包含则直接显示
                        addSiteDialog.setRightNewData(allDepartmentMap.get(cityId));
                    } else {
                        getDepartmentList(cityId);
                    }
                }

                @Override
                public void onLeftCheck(int cityId) {
                }

                @Override
                public void onRightClick(int cityId) {
                }

                @Override
                public void onConfirmClick(String selectSiteUri, String selectSiteId) {
                    Toast.makeText(VideoConfActivity.this, "正在添加会场...", Toast.LENGTH_SHORT).show();
                    addSiteToConf(selectSiteId);
                }

//                @Override
//                public void onConfirmClick(String selectSiteUri) {
//                    addSiteToConf(selectSiteUri);
//                }
            });
        }
        showAddSiteDialog();
    }

    private void showAddSiteDialog() {
        addSiteDialog.setAlignBackground(false);
        addSiteDialog.setPopupGravity(Gravity.RIGHT);
        addSiteDialog.showPopupWindow();
    }

    private Dialog exitConfDialog;

    /**
     * 显示结束会议的对话框
     */
    private void showExitConfDialog() {
        if (null == exitConfDialog) {
            View view = View.inflate(this, R.layout.dialog_exit_conf, null);
            RelativeLayout rlContent = (RelativeLayout) view.findViewById(R.id.rl_content);
            ImageView ivClose = (ImageView) view.findViewById(R.id.ic_close);
            TextView tvEndConf = (TextView) view.findViewById(R.id.tv_end_conf);
            TextView tvLeaveConf = (TextView) view.findViewById(R.id.tv_leave_conf);

            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    exitConfDialog.dismiss();
                }
            });

            tvEndConf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    unQueryConfInfoSubscribe();
                    endConfReuqest();
                }
            });

            tvLeaveConf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    unQueryConfInfoSubscribe();
                    leaveConf(1832);
                }
            });

            exitConfDialog = new MaterialDialog.Builder(this)
                    .customView(view, false)
                    .build();

            Window window = exitConfDialog.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);  //全屏的flag
            window.setBackgroundDrawableResource(android.R.color.transparent); //设置window背景透明
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.alpha = 0.8f;
            lp.dimAmount = 0.1f; //dimAmount在0.0f和1.0f之间，0.0f完全不暗，1.0f全暗
            window.setAttributes(lp);
        }
        exitConfDialog.show();
    }

    /**
     * 结束会议
     */
    private void endConfReuqest() {
        RequestParams params = new RequestParams(Urls.BASE_URL + "conf/stopConf");
        params.addHeader("Content-Type", "application/json");
        params.addHeader("Accept", "application/json");

        params.addQueryStringParameter("smcConfId", smcConfId);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ConfBeanRespone baseData = new GsonBuilder().create().fromJson(result, ConfBeanRespone.class);
                if (null != baseData && 0 == baseData.code) {
                    Toast.makeText(getApplicationContext(), "结束会议成功", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "结束会议失败,稍后再试");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onCancelled(CancelledException cex) {
//                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {

            }
        });
    }

    private static Intent intent = new Intent(IntentConstant.VIDEO_CONF_ACTIVITY_ACTION);

    /**
     * 发送notif
     */
    private void sendNotif(CallInfo callInfo) {
        NotificationUtils.notify(1, new NotificationUtils.Func1<Void, NotificationCompat.Builder>() {
            @Override
            public Void call(NotificationCompat.Builder param) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
                //判断是否是会议
                param.setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_launcher))
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setContentText((callInfo.isVideoCall() ? "视频" : "语音") + "通话中,点击以继续")
                        .setContentIntent(PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                        //设置该通知的优先级
                        .setPriority(Notification.PRIORITY_HIGH)
                        //让通知右滑是否能能取消通知,默认是false
                        .setOngoing(true)
                        .setAutoCancel(false);
                return null;
            }
        });
    }

}
