package com.zxwl.frame.activity;

import android.content.Intent;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.huawei.opensdk.callmgr.CallConstant;
import com.huawei.opensdk.callmgr.CallInfo;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.zxwl.ecsdk.common.UIConstants;
import com.zxwl.ecsdk.logic.CallFunc;
import com.zxwl.frame.R;
import com.zxwl.frame.adapter.AddSiteAdapter;
import com.zxwl.frame.utils.Constants;
import com.zxwl.frame.utils.StatusBarUtils;
import com.zxwl.frame.utils.sharedpreferences.PreferencesHelper;

public class AudioActivity extends BaseLibActivity implements LocBroadcastReceiver, View.OnClickListener {
    /*会控按钮--*/
    private TextView tvHangUp;
    private TextView tvMic;
    private TextView tvMute;
    /*会控按钮--end*/

    private String[] mActions = new String[]{
            CustomBroadcastConstants.ACTION_CALL_END,
            CustomBroadcastConstants.ADD_LOCAL_VIEW,
            CustomBroadcastConstants.DEL_LOCAL_VIEW
    };

    private CallInfo mCallInfo;
    private int mCallID;
    private Object thisVideoActivity = this;

    private CallMgr mCallMgr;
    private CallFunc mCallFunc;

    private MeetingMgr instance;

    private String confId;

    @Override
    protected void findViews() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        tvHangUp = (TextView) findViewById(R.id.tv_hang_up);
        tvMic = (TextView) findViewById(R.id.tv_mic);
        tvMute = (TextView) findViewById(R.id.tv_mute);
    }

    @Override
    protected void initData() {
        StatusBarUtils.setTransparent(this);

        Intent intent = getIntent();
//        mCallInfo = (CallInfo) intent.getSerializableExtra(UIConstants.CALL_INFO);

        mCallInfo = PreferencesHelper.getData(UIConstants.CALL_INFO, CallInfo.class);

        this.mCallID = mCallInfo.getCallID();

        mCallMgr = CallMgr.getInstance();
        mCallFunc = CallFunc.getInstance();

        //设置扬声器的状态
        setAudioRouteStatus();

        //是否静音
        setMuteStatus();

        instance = MeetingMgr.getInstance();

        confId = PreferencesHelper.getData(Constants.CONF_ID);

        if (!TextUtils.isEmpty(confId)) {
//            queryConfInfo();
        }
    }


    @Override
    protected void setListener() {
        tvHangUp.setOnClickListener(this);
        tvMic.setOnClickListener(this);
        tvMute.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_audio;
    }

    /**
     * 更新扬声器状态
     */
    public void switchAudioRoute() {
        int audioRoute = CallMgr.getInstance().switchAudioRoute();
        setAudioRouteStatus(audioRoute);
    }

    public void setAudioRouteStatus() {
        int audioRoute = CallMgr.getInstance().getCurrentAudioRoute();
        setAudioRouteStatus(audioRoute);
    }

    public void setAudioRouteStatus(int audioRoute) {
        boolean isLoudSpeaker = CallConstant.TYPE_LOUD_SPEAKER == audioRoute;
        tvMute.setCompoundDrawablesWithIntrinsicBounds(0, isLoudSpeaker ? R.mipmap.icon_unmute : R.mipmap.icon_mute, 0, 0);
    }

    /**
     * 麦克风静音
     */
    public void muteMic() {
        boolean currentMuteStatus = mCallFunc.isMuteStatus();
        if (CallMgr.getInstance().muteMic(mCallID, !currentMuteStatus)) {
            mCallFunc.setMuteStatus(!currentMuteStatus);
//            Toast.makeText(this, "麦克风" + (currentMuteStatus ? "当前静音" : "非静音"), Toast.LENGTH_SHORT).show();
            setMuteStatus();
        }
    }

    private void setMuteStatus() {
        boolean currentMuteStatus = mCallFunc.isMuteStatus();
        //更新状态静音按钮状态
        tvMic.setCompoundDrawablesWithIntrinsicBounds(0, currentMuteStatus ? R.mipmap.icon_mic_status_close : R.mipmap.icon_mic_status_open, 0, 0);
    }

    @Override
    public void onReceive(String broadcastName, Object obj) {
        switch (broadcastName) {
            case CustomBroadcastConstants.ACTION_CALL_END:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
                break;

            case CustomBroadcastConstants.ADD_LOCAL_VIEW:
                break;

            case CustomBroadcastConstants.DEL_LOCAL_VIEW:
                break;

            default:
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        LocBroadcast.getInstance().registerBroadcast(this, mActions);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocBroadcast.getInstance().unRegisterBroadcast(this, mActions);
    }

    @Override
    public void onClick(View v) {
        if (R.id.tv_hang_up == v.getId()) {
            //结束会议
            mCallMgr.endCall(mCallID);
        } else if (R.id.tv_mute == v.getId()) {
            if (isMuteSpeakStatus()){
                huaweiOpenSpeaker();
            }else {
                huaweiCloseSpeaker();
            }
        } else if (R.id.tv_mic == v.getId()) {
            muteMic();
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

    /**
     * 设置扬声器的图片
     */
    private void setSpeakerStatus() {
        tvMute.setCompoundDrawablesWithIntrinsicBounds(0, isMuteSpeakStatus() ? R.mipmap.icon_mute : R.mipmap.icon_unmute, 0, 0);
    }

    /*添加会场*/
    private BottomSheetDialog mBottomSheetDialog;
    private TextView tvAddCancle;
    private TextView tvAddConfirm;

    private RecyclerView rvAddAttendees;
    private AddSiteAdapter addSiteAdapter;
    /*添加会场--end*/


}
