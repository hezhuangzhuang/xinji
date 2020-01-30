package com.zxwl.xinji.base;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zxwl.commonlibrary.AppManager;
import com.zxwl.commonlibrary.utils.NotificationHelper;
import com.zxwl.frame.inter.HuaweiLoginImp;
import com.zxwl.network.Urls;
import com.zxwl.network.api.LoginApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.interceptor.CommonLogger;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.activity.LoginActivity;
import com.zxwl.xinji.activity.MainActivity;
import com.zxwl.xinji.service.HeadService;
import com.zxwl.xinji.utils.Constant;
import com.zxwl.xinji.utils.PreferenceUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.jessyan.autosize.AutoSize;
import rx.Subscription;

/**
 * author：pc-20171125
 * data:2018/12/18 15:42
 */
public abstract class BaseActivity extends RxAppCompatActivity {
    protected String TAG = getClass().getSimpleName();

    private NotificationHelper notificationHelper;

    private Subscription latitudeSubscribe;

    /**
     * 初始化view
     */
    protected abstract void findViews();

    /**
     * 初始化view的数据
     */
    protected abstract void initData();

    /**
     * 设置view的监听事件
     */
    protected abstract void setListener();

    /**
     * 获得布局layout id
     *
     * @return
     */
    protected abstract int getLayoutId();

    protected boolean isRegisterEventBus = true;//是否注册EventBus

    private boolean isLogout = false;//是否已经登出

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        //添加Activity到管理栈中
        AppManager.getInstance().addActivity(this);
        findViews();
        initData();
        setListener();

        register();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (this instanceof MainActivity) {
            register();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();

        //如果主界面取消注册，则在主界面时则无法收到强制登出的消息
//        if (this instanceof MainActivity) {
//            unRegister();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unRegister();

        // 结束Activity&从堆栈中移除
        AppManager.getInstance().finishActivity(this);
    }

    private MaterialDialog forceDialog;

    /**
     * 显示强制退出
     */
    private void showForceDialog() {
        if (null != forceDialog && forceDialog.isShowing()) {
            return;
        }
        View inflate = View.inflate(this, R.layout.dialog_force_logout, null);
        TextView tvCall = (TextView) inflate.findViewById(R.id.tv_ok);


        tvCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != forceDialog) {
                    forceDialog.dismiss();
                }
                LoginActivity.startActivity(BaseActivity.this);
                AppManager.getInstance().finishActivity(MainActivity.class);
            }
        });

        forceDialog = new MaterialDialog.Builder(this)
                .customView(inflate, false)
                .build();
        //点击对话框以外的地方，对话框不消失
        forceDialog.setCanceledOnTouchOutside(false);
        //点击对话框以外的地方和返回键，对话框都不消失
        forceDialog.setCancelable(false);
        forceDialog.show();
    }

    private void register() {
        if (isRegisterEventBus) {
            if (!EventBus.getDefault().isRegistered(this)) {
                //注册EventBsus,注意参数是this，传入activity会报错
                EventBus.getDefault().register(this);
            }
        }
    }

    private void unRegister() {
        if (isRegisterEventBus) {
            if (EventBus.getDefault().isRegistered(this)) {
                //注册EventBus,注意参数是this，传入activity会报错
                EventBus.getDefault().unregister(this);
            }
        }
    }

    //EventBus的处理事件函数  该方法可以随意取名 必须为public 必须添加注解并指定线程模型
    //之后EventBus会自动扫描到此函数，进行数据传递
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getData(EventMessage eventMessage) {
        switch (eventMessage.message) {
            case Messages.FORCE_LOGOUT:
                CommonLogger.i("logOut", "BaseActivity收到logout消息,isLogout:" + isLogout);
                HeadService.stopService(BaseActivity.this);
                //华为登出
                HuaweiLoginImp.getInstance().logOut();

                PreferenceUtil.put(Constant.AUTO_LOGIN, false);
                PreferenceUtil.putUserInfo(getApplication(), null);
                PreferenceUtil.put(Constant.PASS_WORD, "");

                if (!isLogout) {
                    logOut();
                    showForceDialog();
                    isLogout = true;
                }
                break;
        }
    }

    /**
     * 登出
     */
    private void logOut() {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(LoginApi.class)
                .doubleLogout(1)
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        CommonLogger.i("logOut", "调用登出接口");
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            PreferenceUtil.put(Constant.AUTO_LOGIN, false);
                            PreferenceUtil.putUserInfo(getApplication(), null);
                            PreferenceUtil.put(Constant.PASS_WORD, "");
                            unRegister();
                            isLogout = true;
                        } else {}
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                    }
                });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //普通情况下，autoConvertDensity一系列的方法需要自行根据项目修改。
            AutoSize.autoConvertDensityOfGlobal(this);
        }else {
            //普通情况下，autoConvertDensity一系列的方法需要自行根据项目修改。
            AutoSize.autoConvertDensityOfGlobal(this);
        }
    }


}
