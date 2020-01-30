package com.zxwl.xinji.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.commonservice.util.CrashUtil;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.loginmgr.LoginMgr;
import com.huawei.opensdk.sdkwrapper.login.LoginCenter;
import com.huawei.opensdk.servicemgr.ServiceMgr;
import com.huawei.utils.ZipUtil;
import com.jaeger.library.StatusBarUtil;
import com.zxwl.commonlibrary.utils.AppUtil;
import com.zxwl.commonlibrary.utils.DialogUtils;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.ecsdk.common.UIConstants;
import com.zxwl.ecsdk.logic.CallFunc;
import com.zxwl.ecsdk.logic.ConfFunc;
import com.zxwl.ecsdk.logic.LoginFunc;
import com.zxwl.ecsdk.utils.FileUtil;
import com.zxwl.frame.inter.HuaweiLoginImp;
import com.zxwl.frame.utils.sharedpreferences.PreferencesHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.LoginApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.xinji.BuildConfig;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.utils.Constant;
import com.zxwl.xinji.utils.PreferenceUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;


public class WelcomeActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    @Override
    protected void findViews() {
    }

    @Override
    protected void initData() {
        //不注册广播
        isRegisterEventBus = false;

        //登录广播
        LocBroadcast.getInstance().registerBroadcast(loginReceiver, mActions);

        StatusBarUtil.setTranslucentForImageView(this, 0, null);

//        checkLogin();
    }

    private void checkLogin() {
        boolean isLogin = false;
        String userName = "";
        String pwd = "";
        isLogin = PreferenceUtil.getBoolean(Constant.AUTO_LOGIN, false);
        userName = PreferenceUtil.getString(Constant.USER_NAME, "");
        pwd = PreferenceUtil.getString(Constant.PASS_WORD, "");

        String finalUserName = userName;
        String finalPwd = pwd;
        boolean finalIsLogin = isLogin;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!finalIsLogin || TextUtils.isEmpty(finalUserName) || TextUtils.isEmpty(finalPwd)) {
                    PreferenceUtil.putUserInfo(WelcomeActivity.this, null);
                    MainActivity.startActivity(WelcomeActivity.this, 2, false);
                    finish();
                } else {
                    loginRequest(finalUserName, finalPwd);
                }
            }
        }, 100);
    }

    private void loginRequest(String name, String pwd) {
        String finalPwd = pwd;

        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(LoginApi.class)
                .login(name, pwd, AppUtil.getDeviceIdIMEI(WelcomeActivity.this))
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<LoginBean>() {
                    @Override
                    public void onSuccess(LoginBean baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            //设置访问地址
                            Urls.BASE_URL = baseData.requestUrl;

                            //smc的地址
                            Urls.SMC_REGISTER_SERVER = baseData.SMC_URL;

                            baseData.account.siteAccount = baseData.siteAccount;
                            baseData.account.sitePwd = baseData.sitePwd;

                            baseData.account.latitude = baseData.latitude;
                            baseData.account.longitude = baseData.longitude;

                            PreferenceUtil.putUserInfo(WelcomeActivity.this, baseData.account);

                            if (TextUtils.isEmpty(baseData.siteAccount) || TextUtils.isEmpty(baseData.sitePwd)) {
                                MainActivity.startActivity(WelcomeActivity.this, 2, false);
                                finish();
                            } else {
                                //登录华为
                                loginHuawei(baseData.siteAccount, baseData.sitePwd);
                            }
                        } else {
                            PreferenceUtil.putUserInfo(WelcomeActivity.this, null);
                            PreferenceUtil.put(Constant.AUTO_LOGIN, false);
                            MainActivity.startActivity(WelcomeActivity.this, 2, false);
                            finish();
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        PreferenceUtil.putUserInfo(WelcomeActivity.this, null);
                        PreferenceUtil.put(Constant.AUTO_LOGIN, false);
                        MainActivity.startActivity(WelcomeActivity.this, 2, false);
                        finish();
                    }
                });
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_welcome;
    }

    private void loginHuawei(String name, String pwd) {
        HuaweiLoginImp.getInstance().loginRequest(
                WelcomeActivity.this,
                name,
                pwd,
                Urls.SMC_REGISTER_SERVER,
                Urls.SMC_REGISTER_PORT,
                "",
                "",
                "",
                ""
        );
    }

    /*华为登录相关start*/
    public static String[] mActions = new String[]{
            CustomBroadcastConstants.LOGIN_SUCCESS,
            CustomBroadcastConstants.LOGIN_FAILED,
            CustomBroadcastConstants.LOGOUT
    };

    private LocBroadcastReceiver loginReceiver = new LocBroadcastReceiver() {
        @Override
        public void onReceive(String broadcastName, Object obj) {
            switch (broadcastName) {
                case CustomBroadcastConstants.LOGIN_SUCCESS:
                    DialogUtils.dismissProgressDialog();
                    LogUtil.i(UIConstants.DEMO_TAG, "login success");

                    LoginCenter.getInstance().getSipAccountInfo().setSiteName(PreferencesHelper.getData(UIConstants.SITE_NAME));
                    //是否登录
                    PreferencesHelper.saveData(UIConstants.IS_LOGIN, true);
                    PreferencesHelper.saveData(UIConstants.REGISTER_RESULT, "0");

                    MainActivity.startActivity(WelcomeActivity.this, 2, false);
                    finish();
                    break;

                case CustomBroadcastConstants.LOGIN_FAILED:
                    DialogUtils.dismissProgressDialog();

                    String errorMessage = ((String) obj);
                    LogUtil.i(UIConstants.DEMO_TAG, "login failed," + errorMessage);
                    ToastHelper.showShort("华为平台登录失败" + errorMessage);

                    MainActivity.startActivity(WelcomeActivity.this, 2, false);
                    finish();
                    break;

                case CustomBroadcastConstants.LOGOUT:
                    LogUtil.i(UIConstants.DEMO_TAG, "logout success");
                    break;

                default:
                    break;
            }
        }
    };
    /*华为登录相关end*/

    @Override
    protected void onResume() {
        super.onResume();

        if (isNeedCheck) {
            checkPermissions(needPermissions);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocBroadcast.getInstance().unRegisterBroadcast(loginReceiver, mActions);
    }

    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    private static final int PERMISSON_REQUESTCODE = 0;

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;

    /**
     * @since 2.5.0
     */
    private void checkPermissions(String... permissions) {
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (null != needRequestPermissonList
                && needRequestPermissonList.size() > 0) {
            String[] strings = needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]);
            ActivityCompat.requestPermissions(this,
                    strings,
                    PERMISSON_REQUESTCODE
            );
        } else {
            initHuawei();
            checkLogin();
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        for (String perm : permissions) {
            Log.i(TAG, TAG + "-->findDeniedPermissions");
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                needRequestPermissonList.add(perm);
            }
        }
        Log.i(TAG, TAG + "-->needRequestPermissonList-->" + needRequestPermissonList.size());
        return needRequestPermissonList;
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] paramArrayOfInt) {
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                showMissingPermissionDialog();
                isNeedCheck = false;
            } else {
                List<String> needRequestPermissonList = findDeniedPermissions(needPermissions);
                if (needRequestPermissonList.size() <= 0) {
                    initHuawei();
                    checkLogin();
                }
            }
        }
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("当前应用缺少必要权限。\n\n请点击\"设置\"-\"权限\"-打开所需权限");

        // 拒绝, 退出应用
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        builder.setPositiveButton("设置",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                        finish();
                    }
                });

        builder.setCancelable(false);

        builder.show();
    }

    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    private static final int EXPECTED_FILE_LENGTH = 7;

    private boolean initHuawei() {
        PreferencesHelper.init(this);

        com.huawei.application.BaseApp.setApp(getApplication());

        if (!isFrontProcess(this, BuildConfig.APPLICATION_ID)) {
            com.huawei.opensdk.commonservice.common.LocContext.init(this);
            CrashUtil.getInstance().init(this);
            Log.i("SDKDemo", "onCreate: PUSH Process.");
            return true;
        }

        String appPath = getApplicationInfo().dataDir + "/lib";

        boolean falg = ServiceMgr.getServiceMgr().startService(this, appPath);

        Log.i(UIConstants.DEMO_TAG, "onCreate: MAIN Process.初始化-->" + falg);

        LoginMgr.getInstance().regLoginEventNotification(LoginFunc.getInstance());
        CallMgr.getInstance().regCallServiceNotification(CallFunc.getInstance());
        MeetingMgr.getInstance().regConfServiceNotification(ConfFunc.getInstance());

        initResourceFile();
        return false;
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
                getFilesDir() + "/AnnoRes";
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
//            LogUtil.i(UIConstants.DEMO_TAG, "close...Exception->e" + e.toString());
        }
    }
}
