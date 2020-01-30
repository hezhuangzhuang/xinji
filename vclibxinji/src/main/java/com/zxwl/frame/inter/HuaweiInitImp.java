package com.zxwl.frame.inter;

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
import com.huawei.utils.ZipUtil;
import com.zxwl.ecsdk.common.UIConstants;
import com.zxwl.ecsdk.logic.CallFunc;
import com.zxwl.ecsdk.logic.ConfFunc;
import com.zxwl.ecsdk.logic.LoginFunc;
import com.zxwl.ecsdk.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * 提供华为组件初始化和注销接口
 */
public class HuaweiInitImp {
    private  static HuaweiInitImp initImp = new HuaweiInitImp();
    private static final int EXPECTED_FILE_LENGTH = 7;
    private Application hostApp;

    public static HuaweiInitImp getInstance(){
        return initImp;
    }

    /**
     * 初始化华为组件
     * @param app   宿主application
     * @param packageName   宿主包名
     * @return
     */
    public boolean initHuawei(Application app,String packageName) {
        hostApp = app;

        BaseApp.setApp(app);

        if (!isFrontProcess(app, packageName)) {
            LocContext.init(app);
            CrashUtil.getInstance().init(app);
            Log.i("SDKDemo", "onCreate: PUSH Process.");
            return true;
        }

        String appPath = app.getApplicationInfo().dataDir + "/lib";
        boolean falg = ServiceMgr.getServiceMgr().startService(app, appPath);

        Log.i(UIConstants.DEMO_TAG, "onCreate: MAIN Process.初始化-->" + falg);

        LoginMgr.getInstance().regLoginEventNotification(LoginFunc.getInstance());
        CallMgr.getInstance().regCallServiceNotification(CallFunc.getInstance());
        MeetingMgr.getInstance().regConfServiceNotification(ConfFunc.getInstance());

        initResourceFile();
        return false;
    }

    /**
     * 注销华为组件
     */
    public void stopHuawei() {
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
        String path =
                LocContext.getFilesDir() + "/AnnoRes";
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
            InputStream inputStream = hostApp.getAssets().open("AnnoRes.zip");
            ZipUtil.unZipFile(inputStream, path);
        } catch (IOException e) {
//            LogUtil.i(UIConstants.DEMO_TAG, "close...Exception->e" + e.toString());
        }
    }
}
