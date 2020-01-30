package com.zxwl.frame.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.sdkwrapper.login.LoginCenter;
import com.zxwl.ecsdk.common.UIConstants;
import com.zxwl.frame.activity.LoginDialogActivity;
import com.zxwl.frame.utils.Constants;
import com.zxwl.frame.utils.sharedpreferences.PreferencesHelper;

/**
 * 监听登录状态
 */
public class LoginStateWatchService extends Service {
    public static String[] mActions = new String[]{
            CustomBroadcastConstants.LOGIN_SUCCESS,
            CustomBroadcastConstants.LOGIN_FAILED
    };

    public LoginStateWatchService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LocBroadcast.getInstance().registerBroadcast(loginReceiver, mActions);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }

    private LocBroadcastReceiver loginReceiver = new LocBroadcastReceiver() {
        @Override
        public void onReceive(String broadcastName, Object obj) {
            switch (broadcastName) {
                case CustomBroadcastConstants.LOGIN_SUCCESS:
                    LogUtil.i(UIConstants.DEMO_TAG, "服务广播监听——登录成功");
                    PreferencesHelper.saveData(UIConstants.IS_LOGIN, true);
                    PreferencesHelper.saveData(UIConstants.REGISTER_RESULT, "0");
                    Constants.reConCount = 0;
                    Constants.reloginLock = false;
                    break;

                case CustomBroadcastConstants.LOGIN_FAILED:
                    String errorMessage = ((String) obj);
                    LogUtil.i(UIConstants.DEMO_TAG, "服务广播监听——登录失败," + errorMessage);
                    Constants.reloginLock = false;
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocBroadcast.getInstance().unRegisterBroadcast(loginReceiver, mActions);
    }
}
