package com.zxwl.frame.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.zxwl.ecsdk.common.UIConstants;
import com.zxwl.frame.R;
import com.zxwl.frame.utils.AppManager;
import com.zxwl.frame.utils.sharedpreferences.PreferencesHelper;

import java.util.Timer;
import java.util.TimerTask;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

/**
 * 来电显示界面
 */
public class CallerIDActivity extends BaseMediaActivity implements View.OnClickListener {
    private ImageView ivAvatar;
    private TextView tvNumber;
    private TextView tvHangUp;

    private static Timer timer;
    private static Task timerTask;

    private TextView tvAnswer;

    //使用静态内部类避免内存泄漏
    private static class Task extends TimerTask {
        @Override
        public void run() {
            AppManager.getInstance().finishActivity(LoadingActivity.class);
            CallMgr.getInstance().endCall(mCallID);
            CallMgr.getInstance().stopPlayRingBackTone();
            CallMgr.getInstance().stopPlayRingingTone();
            AppManager.getInstance().finishActivity(CallerIDActivity.class);
            cancelTimer();
        }
    }

    @Override
    protected void findViews() {
        ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
        tvNumber = (TextView) findViewById(R.id.tv_number);
        tvHangUp = (TextView) findViewById(R.id.tv_hang_up);
        tvAnswer = (TextView) findViewById(R.id.tv_answer);

        timer = new Timer();
        timerTask = new Task();
        timer.schedule(timerTask, 50000);

        //是否移动端创建会议填false
        PreferencesHelper.saveData(UIConstants.IS_CREATE, false);
    }

    @Override
    protected void setListener() {
        tvHangUp.setOnClickListener(this);
        tvAnswer.setOnClickListener(this);

        tvNumber.setText(TextUtils.isEmpty(String.valueOf(mCallNumber)) ? "" : String.valueOf(mCallNumber));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_caller_id;
    }

    @Override
    public void onClick(View v) {
        cancelTimer();
        if (R.id.tv_hang_up == v.getId()) {
            hangUp();
        } else if (R.id.tv_answer == v.getId()) {
            answer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
    }

    private boolean isAnswer = false;//是否点了接听键

    /**
     * 挂断
     */
    private void hangUp() {
        //结束掉等待的对话框
        AppManager.getInstance().finishActivity(LoadingActivity.class);
        CallMgr.getInstance().endCall(mCallID);
        CallMgr.getInstance().stopPlayRingBackTone();
        CallMgr.getInstance().stopPlayRingingTone();
        finish();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        hangUp();
    }

    /**
     * 移除计时器，避免产生内存泄漏
     */
    private static void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    public static final int CAMERA_REQUEST_CODE = 1;

    /**
     * 检查权限
     */
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                PermissionGen.with(this)
                        .addRequestCode(CAMERA_REQUEST_CODE)
                        .permissions(
                                Manifest.permission.CAMERA
//                                , Manifest.permission.RECORD_AUDIO
                        ).request();
            } else {
                answer();
            }
        } else {
            answer();
        }
    }

    private void answer() {
        LogUtil.i(UIConstants.DEMO_TAG, "获取权限");
        if (!isAnswer) {
            LogUtil.i(UIConstants.DEMO_TAG, "开始接听");
            CallMgr.getInstance().answerCall(mCallID, mIsVideoCall);
            isAnswer = true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    /**
     * 得到权限
     */
    @PermissionSuccess(requestCode = CAMERA_REQUEST_CODE)
    public void getPermissionSuccess() {
        answer();
    }

    /**
     * 没得到权限
     */
    @PermissionFail(requestCode = CAMERA_REQUEST_CODE)
    public void getPermissionFail() {
//        ToastHelper.showShort("视频需要摄像头权限");
        Toast.makeText(this, "视频需要摄像头权限", Toast.LENGTH_SHORT).show();
    }
}
