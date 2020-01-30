package com.zxwl.frame.inter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.loginmgr.LoginConstant;
import com.huawei.opensdk.loginmgr.LoginMgr;
import com.huawei.opensdk.loginmgr.LoginParam;
import com.huawei.utils.DeviceManager;
import com.zxwl.ecsdk.common.UIConstants;
import com.zxwl.ecsdk.utils.FileUtil;
import com.zxwl.frame.bean.respone.SiteInfo;
import com.zxwl.frame.net.Urls;
import com.zxwl.frame.service.AudioStateWatchService;
import com.zxwl.frame.service.LoginStateWatchService;
import com.zxwl.frame.utils.OSUtils;
import com.zxwl.frame.utils.sharedpreferences.PreferencesHelper;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executors;

import static com.huawei.opensdk.commonservice.common.LocContext.getPackageName;

/**
 * author：pc-20171125
 * data:2019/1/3 11:11
 * 登录
 */
public class HuaweiLoginImp {
    private static HuaweiLoginImp loginImp = new HuaweiLoginImp();
    private String TAG = HuaweiLoginImp.class.getSimpleName();

    private Gson gson = new Gson();

    public static HuaweiLoginImp getInstance() {
        return loginImp;
    }

    /**
     * 登录
     *
     * @param activity
     * @param userName
     * @param password
     * @param smcRegisterServer
     * @param smcRegisterPort
     * @param otherServer
     * @param otherPort
     * @param guohangServer
     * @param guohangPort
     * @param isDisNetword      是否是断网充电
     */
    public void querySiteUri(Activity activity,
                             String userName,
                             String password,
                             String smcRegisterServer,
                             String smcRegisterPort,
                             String otherServer,
                             String otherPort,
                             String guohangServer,
                             String guohangPort,
                             boolean isDisNetword) {

        //修正授权不及时导致的本端黑屏问题
        if (!isCameraUseable()) {
            Toast.makeText(activity, "检测到摄像头权限未启用，请打开摄像头权限并重启应用", Toast.LENGTH_SHORT).show();
        }

        //修正MIUI后台无法弹窗，对MIUI的后台弹窗权限进行特殊校验
        if (OSUtils.check(OSUtils.ROM_MIUI) && !OSUtils.canBackgroundStart(activity)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("提示");
            builder.setMessage("检测到当前正在使用MIUI系统，请启用后台弹出界面权限以保证app的正常运行");
            builder.setCancelable(false);
            builder.setPositiveButton("前往设置", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MIUI_openAppPermission(activity, getPackageName());
                }
            });
            builder.show();
            return;
        }

        //启动登录状态监听服务
        activity.startService(new Intent(activity,LoginStateWatchService.class));
        activity.startService(new Intent(activity, AudioStateWatchService.class));

        if (TextUtils.isEmpty(otherServer)) {
            Toast.makeText(activity, "服务器地址不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        //如果是断网重连
        if (isDisNetword) {
            Urls.BASE_URL = otherServer;
        } else {
            if (TextUtils.isEmpty(otherPort)) {
                Urls.BASE_URL = "http://" + otherServer + "/";
            } else {
                Urls.BASE_URL = "http://" + otherServer + ":" + otherPort + "/";
            }
        }

        if (TextUtils.isEmpty(guohangServer)) {
            Toast.makeText(activity, "服务器地址不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isDisNetword) {
            Urls.GUOHANG_BASE_URL = guohangServer;
        } else {
            if (TextUtils.isEmpty(guohangPort)) {
                Urls.GUOHANG_BASE_URL = "http://" + guohangServer + "/";
            } else {
                Urls.GUOHANG_BASE_URL = "http://" + guohangServer + ":" + guohangPort + "/";
            }
        }

        RequestParams params = new RequestParams(Urls.BASE_URL + "site/queryByUri");
        params.addHeader("Content-Type", "application/json");
        params.addHeader("Accept", "application/json");

        params.addQueryStringParameter("siteUri", userName);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                SiteInfo siteInfo = new GsonBuilder().create().fromJson(result, SiteInfo.class);

                if (null != siteInfo && 0 == siteInfo.code) {
                    PreferencesHelper.saveData(UIConstants.SITE_NAME, siteInfo.data.siteName);

                    loginRequest(activity,
                            userName,
                            password,
                            smcRegisterServer,
                            smcRegisterPort,
                            otherServer,
                            otherPort,
                            guohangServer,
                            guohangPort);
                } else {
                    LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.LOGIN_FAILED, "会场名称请求失败,请稍后再试");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.LOGIN_FAILED, "会场名称请求失败:" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                //Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {

            }
        });
    }

    public void loginRequest(Activity activity,
                             String userName,
                             String password,
                             String smcRegisterServer,
                             String smcRegisterPort,
                             String otherServer,
                             String otherPort,
                             String guohangServer,
                             String guohangPort) {
        if (!DeviceManager.isNetworkAvailable(activity)) {
            return;
        }

        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            return;
        }

        String regServerAddress = smcRegisterServer;
        String serverPort = smcRegisterPort;

        if (TextUtils.isEmpty(regServerAddress)) {
            return;
        }

        if (TextUtils.isEmpty(serverPort)) {
            return;
        }

        if (null == Looper.myLooper()) {
            Looper.prepare();
        }

        LoginParam loginParam = new LoginParam();

        loginParam.setServerPort(Integer.parseInt(serverPort));
        loginParam.setProxyPort(Integer.parseInt(serverPort));
        loginParam.setServerUrl(regServerAddress);
        loginParam.setProxyUrl(regServerAddress);
        loginParam.setUserName(userName);
        loginParam.setPassword(password);

        loginParam.setVPN(false);

        loginParam.setSrtpMode(0);
        int mode = "5061".equals(serverPort) ? 1 : 0;

        //TLS:5061
        //UDP:5060
        //TCP:5060
        loginParam.setSipTransportMode(mode);//UDP:0,TLS:1,TCP:2
        loginParam.setServerType(2);

        final int login = LoginMgr.getInstance().login(loginParam);

        importFile(activity);
    }

    public void loginRequest(String userName,
                             String password,
                             String smcRegisterServer,
                             String smcRegisterPort,
                             String otherServer,
                             String otherPort,
                             String guohangServer,
                             String guohangPort) {

        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            return;
        }

        String regServerAddress = smcRegisterServer;
        String serverPort = smcRegisterPort;

        if (TextUtils.isEmpty(regServerAddress)) {
            return;
        }

        if (TextUtils.isEmpty(serverPort)) {
            return;
        }

        if (null == Looper.myLooper()) {
            Looper.prepare();
        }

        LoginParam loginParam = new LoginParam();

        loginParam.setServerPort(Integer.parseInt(serverPort));
        loginParam.setProxyPort(Integer.parseInt(serverPort));
        loginParam.setServerUrl(regServerAddress);
        loginParam.setProxyUrl(regServerAddress);
        loginParam.setUserName(userName);
        loginParam.setPassword(password);

        loginParam.setVPN(false);

        loginParam.setSrtpMode(0);
        int mode = "5061".equals(serverPort) ? 1 : 0;

        //TLS:5061
        //UDP:5060
        //TCP:5060
        loginParam.setSipTransportMode(mode);//UDP:0,TLS:1,TCP:2
        loginParam.setServerType(2);

        final int login = LoginMgr.getInstance().login(loginParam);
    }

    public void querySiteUri(Activity activity,
                             String userName,
                             String password,
                             String smcRegisterServer,
                             String smcRegisterPort,
                             String otherServer,
                             String otherPort,
                             String guohangServer,
                             String guohangPort) {
        querySiteUri(activity, userName, password, smcRegisterServer, smcRegisterPort, otherServer, otherPort, guohangServer, guohangPort, false);
    }
    /*华为登录相关end*/

    /**
     * 登出
     */
    public void logOut() {
        PreferencesHelper.saveData(UIConstants.IS_LOGOUT, true);
        String state = "";
        try {
            state = PreferencesHelper.getData(UIConstants.REGISTER_RESULT_TEMP);
        } catch (Exception e) {
        }

//        Toast.makeText(LocContext.getContext(), "state:" + state, Toast.LENGTH_SHORT).show();

//        if ("4".equals(state) || "0".equals(state) || "2".equals(state)) {
        if (!"3".equals(state)) {
            //没有调用登出接口
            //如果网络连接成功
            LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.LOGOUT, null);
        } else {
            LoginMgr.getInstance().logout();
        }
        //登出时，注销登录状态监听服务
        LocContext.getContext().stopService(new Intent(LocContext.getContext(),LoginStateWatchService.class));
        LocContext.getContext().stopService(new Intent(LocContext.getContext(), AudioStateWatchService.class));
    }

    /**
     * import file.
     *
     * @param activity
     */
    private static void importFile(Activity activity) {
        LogUtil.i(UIConstants.DEMO_TAG, "import media file!~");
        Executors.newFixedThreadPool(LoginConstant.FIXED_NUMBER).execute(new Runnable() {
            @Override
            public void run() {
                importMediaFile(activity);
                importBmpFile(activity);
                importAnnotFile(activity);
            }
        });
    }

    private static void importBmpFile(Activity activity) {
        if (FileUtil.isSdCardExist()) {
            try {
                String bmpPath = Environment.getExternalStorageDirectory() + File.separator + Urls.BMP_FILE;
                InputStream bmpInputStream = activity.getAssets().open(Urls.BMP_FILE);
                FileUtil.copyFile(bmpInputStream, bmpPath);
            } catch (IOException e) {
                LogUtil.e(UIConstants.DEMO_TAG, e.getMessage());
            }
        }
    }

    private static void importAnnotFile(Activity activity) {
        if (FileUtil.isSdCardExist()) {
            try {
                String bmpPath = Environment.getExternalStorageDirectory() + File.separator + Urls.ANNOT_FILE;
                File file = new File(bmpPath);
                if (!file.exists()) {
                    file.mkdir();
                }

                String[] bmpNames = new String[]{"check.bmp", "xcheck.bmp", "lpointer.bmp",
                        "rpointer.bmp", "upointer.bmp", "dpointer.bmp", "lp.bmp"};
                String[] paths = new String[bmpNames.length];

                for (int list = 0; list < paths.length; ++list) {
                    paths[list] = bmpPath + File.separator + bmpNames[list];
                    InputStream bmpInputStream = activity.getAssets().open(bmpNames[list]);
                    FileUtil.copyFile(bmpInputStream, paths[list]);
                }

            } catch (IOException e) {
                LogUtil.e(UIConstants.DEMO_TAG, e.getMessage());
            }
        }
    }

    private static void importMediaFile(Activity activity) {
        if (FileUtil.isSdCardExist()) {
            try {
                String mediaPath = Environment.getExternalStorageDirectory() + File.separator + Urls.RINGING_FILE;
                InputStream mediaInputStream = activity.getAssets().open(Urls.RINGING_FILE);
                FileUtil.copyFile(mediaInputStream, mediaPath);

                String ringBackPath = Environment.getExternalStorageDirectory() + File.separator + Urls.RING_BACK_FILE;
                InputStream ringBackInputStream = activity.getAssets().open(Urls.RING_BACK_FILE);
                FileUtil.copyFile(ringBackInputStream, ringBackPath);
            } catch (IOException e) {
                LogUtil.e(UIConstants.DEMO_TAG, e.getMessage());
            }
        }
    }

    /**
     * 检测摄像头可用性
     *
     * @return
     */
    public static boolean isCameraUseable() {
        boolean canUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            // setParameters 是针对魅族MX5。MX5通过Camera.open()拿到的Camera对象不为null
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            canUse = false;
        }
        if (mCamera != null) {
            mCamera.release();
        }

        return canUse;
    }

    /**
     * MIUI系統，进入权限管理页面
     *
     * @param activity
     * @param packageName
     */
    public static void MIUI_openAppPermission(Activity activity, String packageName) {
        try {
            Intent intent = new Intent();
            intent.setAction("miui.intent.action.APP_PERM_EDITOR");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.putExtra("extra_pkgname", packageName);
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
