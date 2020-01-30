package com.zxwl.frame.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.loginmgr.LoginMgr;
import com.huawei.opensdk.sdkwrapper.login.LoginCenter;
import com.zxwl.ecsdk.common.UIConstants;
import com.zxwl.frame.R;
import com.zxwl.frame.inter.HuaweiLoginImp;
import com.zxwl.frame.net.Urls;
import com.zxwl.frame.utils.Constants;
import com.zxwl.frame.utils.sharedpreferences.PreferencesHelper;

public class LoginDialogActivity extends AppCompatActivity {

    /**
     * 启动断线重连，进行若干次后台重连后，再给出用户提示
     * @param context
     */
    public static void startActivity(Context context) {
        //判断当前是否处理断线重连过程中，避免重复进行登录
        if (Constants.reloginLock){
            return;
        }
        Constants.reloginLock = true;
        //此处通过计数器Constants.reConCount，进行判断是后台静默重连，还是启动此activity
        if (Constants.reConCount >= 3){
            Intent intent = new Intent(LocContext.getContext(), LoginDialogActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            LocContext.getContext().startActivity(intent);
            Constants.reConCount = 0;
        }else {
            backlogin();
            Constants.reConCount = Constants.reConCount + 1;
        }
        LogUtil.i(UIConstants.DEMO_TAG, "重试计次 Constants.reConCount = " + Constants.reConCount);
    }

    private void setListener() {
        findViewById(R.id.bt_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(LoginDialogActivity.this, "登录超时,请稍后再试", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }, 60 * 1000);
                login();
            }
        });

        findViewById(R.id.bt_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HuaweiLoginImp.getInstance().logOut();
                finish();
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_dialog);

        setListener();
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
                    LogUtil.i(UIConstants.DEMO_TAG, "login success");

                    LoginCenter.getInstance().getSipAccountInfo().setSiteName(PreferencesHelper.getData(UIConstants.SITE_NAME));
                    //是否登录
                    PreferencesHelper.saveData(UIConstants.IS_LOGIN, true);
                    PreferencesHelper.saveData(UIConstants.REGISTER_RESULT, "0");
//                    Toast.makeText(LoginDialogActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    dismissDialog();
                    finish();
                    break;

                case CustomBroadcastConstants.LOGIN_FAILED:
                    String errorMessage = ((String) obj);
                    LogUtil.i(UIConstants.DEMO_TAG, "login failed," + errorMessage);

//                    if (errorMessage.contains("error code:480") || errorMessage.contains("Timeout error")) {
                    login();
//                    }
                    break;

                case CustomBroadcastConstants.LOGOUT:
                    LogUtil.i(UIConstants.DEMO_TAG, "logout success");
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        //登录广播
        LocBroadcast.getInstance().registerBroadcast(loginReceiver, mActions);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //登录广播
        LocBroadcast.getInstance().unRegisterBroadcast(loginReceiver, mActions);
    }

    private void login() {
        showProgressDialog();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtil.i(UIConstants.DEMO_TAG, "开始登录");

                HuaweiLoginImp.getInstance().logOut();

                HuaweiLoginImp
                        .getInstance()
                        .querySiteUri(
                                LoginDialogActivity.this,
                                LoginCenter.getInstance().getAccount(),
                                LoginCenter.getInstance().getPassword(),
                                LoginCenter.getInstance().getLoginServerAddress(),
                                LoginCenter.getInstance().getSipPort() + "",
                                Urls.BASE_URL,
                                "",
                                Urls.GUOHANG_BASE_URL,
                                "",
                                true);


            }
        }, 1 * 1000);
    }

    /**
     * 后台登录接口
     */
    private static void backlogin() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtil.i(UIConstants.DEMO_TAG, "后台登录开始");
                Toast.makeText(LocContext.getContext(),"开始断线重连...",Toast.LENGTH_SHORT).show();

                LoginMgr.getInstance().logout();

                HuaweiLoginImp.getInstance().loginRequest(
                        LoginCenter.getInstance().getAccount(),
                        LoginCenter.getInstance().getPassword(),
                        LoginCenter.getInstance().getLoginServerAddress(),
                        LoginCenter.getInstance().getSipPort() + "",
                        Urls.BASE_URL,
                        "",
                        Urls.GUOHANG_BASE_URL,
                        "");

//                HuaweiLoginImp.getInstance().logOut();
//                HuaweiLoginImp
//                        .getInstance()
//                        .querySiteUri(AppManager.getInstance().getStack().peek(),
//                                LoginCenter.getInstance().getAccount(),
//                                LoginCenter.getInstance().getPassword(),
//                                LoginCenter.getInstance().getLoginServerAddress(),
//                                LoginCenter.getInstance().getSipPort() + "",
//                                Urls.BASE_URL,
//                                "",
//                                Urls.GUOHANG_BASE_URL,
//                                "",
//                                true);


            }
        }, 0);
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

}
