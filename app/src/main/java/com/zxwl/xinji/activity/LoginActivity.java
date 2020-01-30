package com.zxwl.xinji.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gyf.immersionbar.ImmersionBar;
import com.jaeger.library.StatusBarUtil;
import com.zxwl.commonlibrary.AppManager;
import com.zxwl.commonlibrary.utils.AESUtil;
import com.zxwl.commonlibrary.utils.AppUtil;
import com.zxwl.commonlibrary.utils.DialogUtils;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.frame.inter.HuaweiLoginImp;
import com.zxwl.frame.utils.NotificationUtils;
import com.zxwl.network.Urls;
import com.zxwl.network.api.LoginApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.utils.Constant;
import com.zxwl.xinji.utils.PreferenceUtil;
import com.zxwl.xinji.utils.UpdateUtils;

import java.lang.ref.WeakReference;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

import static com.huawei.opensdk.commonservice.common.LocContext.getContext;

/**
 * 登录界面
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText etName;
    private EditText etPwd;
    private TextView tvBackPwd;
    private TextView tvNoLogin;
    private Button btLogin;

    private boolean isAutoLogin = false;//是否自动登录

    private int REQUEST_PHONE_INFO = 0X10;//获取手机信息的权限

    private boolean isShowPwd;
    private ImageView ivHidePwd;

    private WeakReference<LoginActivity> weakReference;

    public static final String NEWS_ID = "NEWS_ID";
    private int newsId;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, int newsId) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(NEWS_ID, newsId);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        etName = (EditText) findViewById(R.id.et_name);
        etPwd = (EditText) findViewById(R.id.et_pwd);
        tvNoLogin = (TextView) findViewById(R.id.tv_no_login);
        tvBackPwd = (TextView) findViewById(R.id.tv_back_pwd);
        btLogin = (Button) findViewById(R.id.bt_login);

        ivHidePwd = (ImageView) findViewById(R.id.iv_pwd_show);
    }

    @Override
    protected void initData() {
        newsId = getIntent().getIntExtra(NEWS_ID, -1);

        weakReference = new WeakReference<LoginActivity>(this);

        //不注册
        isRegisterEventBus = false;

        String userName = PreferenceUtil.getString(Constant.USER_NAME, "");
        String pwd = PreferenceUtil.getString(Constant.PASS_WORD, "");

        try {
            pwd = AESUtil.decrypt(pwd);
        } catch (Exception e) {
            e.printStackTrace();
        }

        etName.setText(userName);
        etPwd.setText(pwd);

        setSelection(etName);

        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.white), 0);

        ImmersionBar.with(this)
//                .transparentStatusBar()  //透明状态栏，不写默认透明色
//                .transparentNavigationBar()  //透明导航栏，不写默认黑色(设置此方法，fullScreen()方法自动为true)
//                .transparentBar()             //透明状态栏和导航栏，不写默认状态栏为透明色，导航栏为黑色（设置此方法，fullScreen()方法自动为true）
//                .statusBarColor(R.color.colorPrimary)     //状态栏颜色，不写默认透明色
//                .navigationBarColor(R.color.colorPrimary) //导航栏颜色，不写默认黑色
//                .barColor(R.color.colorPrimary)  //同时自定义状态栏和导航栏颜色，不写默认状态栏为透明色，导航栏为黑色
                .fitsSystemWindows(true)
                .statusBarAlpha(0.3f)  //状态栏透明度，不写默认0.0f
                .navigationBarAlpha(0.4f)  //导航栏透明度，不写默认0.0F
                .barAlpha(0.3f)  //状态栏和导航栏透明度，不写默认0.0f
                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                .navigationBarDarkIcon(true) //导航栏图标是深色，不写默认为亮色
                .autoDarkModeEnable(true) //自动状态栏字体和导航栏图标变色，必须指定状态栏颜色和导航栏颜色才可以自动变色哦
                .autoStatusBarDarkModeEnable(true, 0.2f) //自动状态栏字体变色，必须指定状态栏颜色才可以自动变色哦
                .autoNavigationBarDarkModeEnable(true, 0.2f) //自动导航栏图标变色，必须指定导航栏颜色才可以自动变色哦
                .flymeOSStatusBarFontColor(R.color.black)  //修改flyme OS状态栏字体颜色
//                .reset()  //重置所以沉浸式参数
                .init();  //必须调用方可应用以上所配置的参数
    }

    private void setSelection(EditText editText) {
        //设置光标位置
        CharSequence text = editText.getText();
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
    }

    @Override
    protected void setListener() {
        btLogin.setOnClickListener(this);
        tvBackPwd.setOnClickListener(this);
        tvNoLogin.setOnClickListener(this);
        ivHidePwd.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void onResume() {
        super.onResume();
        HuaweiLoginImp.getInstance().logOut();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login:
                login();
                break;

            case R.id.iv_pwd_show:
                isShowPwd = !isShowPwd;
                if (isShowPwd) {
                    //如果选中，显示密码
                    etPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivHidePwd.setImageResource(R.mipmap.ic_pwd_show);
                } else {
                    //否则隐藏密码
                    etPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivHidePwd.setImageResource(R.mipmap.ic_pwd_hide);
                }
                setSelection(etPwd);
                break;

            case R.id.tv_no_login:
                PreferenceUtil.put(Constant.AUTO_LOGIN, false);
                PreferenceUtil.putUserInfo(getApplication(), null);
                MainActivity.startActivity(LoginActivity.this, 2, true);
                break;

            case R.id.tv_back_pwd:
                showForgetDialog();
                break;

            default:
                break;
        }
    }


    private String name;
    private String pwd;

    private void login() {
        name = etName.getText().toString().trim();
        pwd = etPwd.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this.getApplicationContext(), "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(this.getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionGen.with(this)
                    .addRequestCode(LoginActivity.REQUEST_IMEI_CODE)
                    .permissions(
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.ACCESS_FINE_LOCATION

                    ).request();
        } else {
            loginRequest(name, pwd);
        }
    }

    public final static int REQUEST_IMEI_CODE = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    /**
     * 获取信息的权限
     */
    @PermissionSuccess(requestCode = LoginActivity.REQUEST_IMEI_CODE)
    public void takePhoto() {
        loginRequest(name, pwd);
    }

    /**
     * 得到拍照权限
     */
    @PermissionFail(requestCode = LoginActivity.REQUEST_IMEI_CODE)
    public void take() {
        Toast.makeText(this.getApplicationContext(), "需要获取手机唯一码", Toast.LENGTH_SHORT).show();
    }

    private void loginRequest(String name, String pwd) {
        DialogUtils.showProgressDialog(weakReference.get(), "正在登录...");

        try {
            pwd = AESUtil.encrypt(pwd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String finalPwd = pwd;

        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(LoginApi.class)
                .login(name, pwd, AppUtil.getDeviceIdIMEI(LoginActivity.this))
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<LoginBean>() {
                    @Override
                    public void onSuccess(LoginBean baseData) {
                        DialogUtils.dismissProgressDialog();
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            //设置访问地址
                            Urls.BASE_URL = baseData.requestUrl;

                            //smc的地址
                            Urls.SMC_REGISTER_SERVER = baseData.SMC_URL;


                            PreferenceUtil.put(Constant.USER_NAME, name);
                            PreferenceUtil.put(Constant.PASS_WORD, finalPwd);
                            PreferenceUtil.put(Constant.AUTO_LOGIN, true);

                            baseData.account.siteAccount = baseData.siteAccount;
                            baseData.account.sitePwd = baseData.sitePwd;

                            baseData.account.longitude = baseData.longitude;
                            baseData.account.latitude = baseData.latitude;

                            PreferenceUtil.putUserInfo(LoginActivity.this, baseData.account);

                            if (-1 == newsId) {
                                MainActivity.startActivity(LoginActivity.this, 2, false);
                            } else {
                                ContentDetailsActivity.startActivity(LoginActivity.this, newsId);
                            }
                            finish();
                        } else {
                            ToastHelper.showShort(baseData.message);

                            if (baseData.message.contains("密码错误")) {
                                //如果选中，显示密码
                                etPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                                ivHidePwd.setImageResource(R.mipmap.ic_pwd_show);

                                setSelection(etPwd);
                            }
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        DialogUtils.dismissProgressDialog();
                        if (responeThrowable.getCause().getMessage().contains("Failed to connect to")) {
                            ToastHelper.showShort("请检查您的网络");
                        } else {
                            ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        AppManager.getInstance().appExit();
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(getPackageName());
    }

    private MaterialDialog forgetDialog = null;

    /**
     * 显示对话框
     */
    private void showForgetDialog() {
        View inflate = View.inflate(this, R.layout.dialog_forget_pwd, null);
        TextView tvOk = inflate.findViewById(R.id.tv_ok);

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != forgetDialog) {
                    forgetDialog.dismiss();
                }
            }
        });

        forgetDialog = new MaterialDialog.Builder(this)
                .customView(inflate, false)
                .build();
        //点击对话框以外的地方，对话框不消失
//        forgetDialog.setCanceledOnTouchOutside(false);
        //点击对话框以外的地方和返回键，对话框都不消失
        //callDialog.setCancelable(false);
        forgetDialog.show();
    }

}
