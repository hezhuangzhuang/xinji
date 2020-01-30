package com.zxwl.frame;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.huawei.application.BaseApp;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.util.CrashUtil;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.loginmgr.LoginMgr;
import com.huawei.opensdk.servicemgr.ServiceMgr;
import com.zxwl.ecsdk.common.UIConstants;
import com.zxwl.ecsdk.logic.CallFunc;
import com.zxwl.ecsdk.logic.ConfFunc;
import com.zxwl.ecsdk.logic.LoginFunc;
import com.zxwl.ecsdk.utils.FileUtil;
import com.zxwl.ecsdk.utils.ZipUtil;
import com.zxwl.frame.utils.sharedpreferences.PreferencesHelper;

import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * author：pc-20171125
 * data:2019/1/2 14:27
 */
public class HuaweiApp extends Application {
    private static final int EXPECTED_FILE_LENGTH = 7;

    private static final String FRONT_PKG = "com.zxwl.testdemo";

    private static HuaweiApp mInstance;

    public static HuaweiApp getInstance() {
        return mInstance;
    }

    /**
     * 处于前台的activity数
     */
    private int activityCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        Log.i("HookApplication", "App初始化");

        PreferencesHelper.init(this);

        //腾讯bug线上搜集工具
//        CrashReport.initCrashReport(getApplicationContext(), "5010919617", false);

        BaseApp.setApp(this);

        if (!isFrontProcess(this, FRONT_PKG)) {
            LocContext.init(this);
            CrashUtil.getInstance().init(this);
            Log.i("SDKDemo", "onCreate: PUSH Process.");
            return;
        }

        String appPath = getApplicationInfo().dataDir + "/lib";
        boolean flag = ServiceMgr.getServiceMgr().startService(this, appPath);

        Log.i(UIConstants.DEMO_TAG, "onCreate: MAIN Process.初始化-->" + flag);

        LoginMgr.getInstance().regLoginEventNotification(LoginFunc.getInstance());
        CallMgr.getInstance().regCallServiceNotification(CallFunc.getInstance());
        MeetingMgr.getInstance().regConfServiceNotification(ConfFunc.getInstance());

        initResourceFile();

        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 开启debug会影响性能
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        ServiceMgr.getServiceMgr().stopService();
    }

    /**
     * 华为初始化
     *
     * @param context
     * @param frontPkg
     * @return
     */
    private static boolean isFrontProcess(Context context, String frontPkg) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> infos = null;
        if (manager != null) {
            infos = manager.getRunningAppProcesses();
        }
        if (infos == null || infos.isEmpty()) {
            return false;
        }

        final int pid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid) {
                Log.i(UIConstants.DEMO_TAG, "processName-->" + info.processName);
                return frontPkg.equals(info.processName);
            }
        }
        return false;
    }

    /**
     * 华为初始化
     */
    private void initResourceFile() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                initDataConfRes();
            }
        });
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                initDataConfRes();
//            }
//        }).start();
    }

    /**
     * 华为初始化
     */
    private void initDataConfRes() {
        String path = LocContext.
                getContext().getFilesDir() + "/AnnoRes";
        File file = new File(path);
        if (file.exists()) {
            LogUtil.i(UIConstants.DEMO_TAG, file.getAbsolutePath());
            File[] files = file.listFiles();
            if (null != files && EXPECTED_FILE_LENGTH == files.length) {
                return;
            } else {
                FileUtil.deleteFile(file);
            }
        }

        try {
            InputStream inputStream = getAssets().open("AnnoRes.zip");
            ZipUtil.unZipFile(inputStream, path);
        } catch (IOException e) {
            LogUtil.i(UIConstants.DEMO_TAG, "close...Exception->e" + e.toString());
        }
    }

}
