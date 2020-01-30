package com.zxwl.xinji.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.CustomVersionDialogListener;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.sdkwrapper.login.LoginCenter;
import com.zxwl.commonlibrary.AppManager;
import com.zxwl.commonlibrary.utils.DialogUtils;
import com.zxwl.commonlibrary.utils.LogUtil;
import com.zxwl.commonlibrary.utils.PackageUtil;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.ecsdk.common.UIConstants;
import com.zxwl.frame.inter.HuaweiCallImp;
import com.zxwl.frame.inter.HuaweiLoginImp;
import com.zxwl.frame.utils.NetworkUtil;
import com.zxwl.frame.utils.StatusBarUtil;
import com.zxwl.frame.utils.sharedpreferences.PreferencesHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.bean.response.VersionBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.interceptor.CommonLogger;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.bean.TabEntity;
import com.zxwl.xinji.fragment.OnlineOfficeFragment;
import com.zxwl.xinji.fragment.OrganizingLifeMaterialFragment;
import com.zxwl.xinji.fragment.PartyMapNewFragment;
import com.zxwl.xinji.fragment.PartyServiceFragment;
import com.zxwl.xinji.fragment.StudyFragment;
import com.zxwl.xinji.service.HeadService;
import com.zxwl.xinji.utils.Constant;
import com.zxwl.xinji.utils.PreferenceUtil;
import com.zxwl.xinji.utils.SystemUtil;
import com.zxwl.xinji.utils.UpdateUtils;
import com.zxwl.xinji.widget.BaseDialog;
import com.zxwl.xinji.widget.CustomCommonTabLayout;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import me.jessyan.autosize.AutoSize;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, LocBroadcastReceiver {
    private Subscription confInfoSubscribe;
    private MaterialDialog forceDialog;

    public static final String CURRENT_TAB = "CURRENT_TAB";
    //是否忽略更新
    public static final String IGNORE_UPDATE = "CHECK_UPDATE";

    private int currentTab = 0;

    private LoginBean.AccountBean accountBean;
    //重新登陆的次数
    private int retryCount;

    public static void startActivity(Context context, int currentTab, boolean igonreUpdate) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(CURRENT_TAB, currentTab);
        intent.putExtra(IGNORE_UPDATE, igonreUpdate);
        context.startActivity(intent);
    }

    //    private CommonTabLayout tabLayout;
    private CustomCommonTabLayout tabLayout;

    private final String[] mTitles = {
            "党建地图",
            "组织生活",
            "",
            "党群服务",
            "在线办公",
    };

    private final int[] mIconUnselectIds = {
            R.mipmap.icon_hone_one_false,
            R.mipmap.icon_hone_two_false,
            R.mipmap.icon_hone_center_false,
            R.mipmap.icon_hone_three_false,
            R.mipmap.icon_hone_four_false
    };

    private final int[] mIconSelectIds = {
            R.mipmap.icon_hone_one_true,
            R.mipmap.icon_hone_two_true,
            R.mipmap.icon_hone_center_true,
            R.mipmap.icon_hone_three_true,
            R.mipmap.icon_hone_four_true
    };

    private ArrayList<CustomTabEntity> mTabEntities;

    private ArrayList<Fragment> fragments;

    /**
     * 判断是否已经点击过一次回退键
     */
    private boolean isBackPressed = false;

    private String[] mActions = new String[]{
            CustomBroadcastConstants.GET_CONF_END,
            CustomBroadcastConstants.ACTION_CALL_END,
            CustomBroadcastConstants.ADD_LOCAL_VIEW,
            CustomBroadcastConstants.LOGIN_SUCCESS,
            CustomBroadcastConstants.LOGIN_FAILED
    };

    @Override
    protected void findViews() {
        tabLayout = (CustomCommonTabLayout) findViewById(R.id.tab_layout);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setTranslucentForImageView(this, 0, null);

        currentTab = getIntent().getIntExtra(CURRENT_TAB, 0);

        setTab();

        if (isLogin()) {
            //启动服务
            HeadService.startService(this);

            accountBean = PreferenceUtil.getUserInfo(this);
            //获取登陆状态
            String registerStatus = PreferencesHelper.getData(UIConstants.REGISTER_RESULT_TEMP);

            if (!HuaweiCallImp.LOGIN_STATUS.equals(registerStatus)) {
                //登录华为平台
                HuaweiLoginImp.getInstance().loginRequest(this,
                        accountBean.siteAccount,
                        accountBean.sitePwd,
                        Urls.SMC_REGISTER_SERVER,
                        Urls.SMC_REGISTER_PORT,
                        "",
                        "",
                        "",
                        "");
            }
            LocBroadcast.getInstance().registerBroadcast(this, mActions);
        } else {
            LocBroadcast.getInstance().unRegisterBroadcast(this, mActions);
        }

        //是否忽略更新检查
        boolean ignoreUpdate = getIntent().getBooleanExtra(IGNORE_UPDATE, false);

        if (!ignoreUpdate) {
            queryVersion();
        }
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    /**
     * 保存数据状态
     *
     * @param outState
     */
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    private void setTab() {
        Log.i(TAG, TAG + "-->setTab-->" + currentTab);
        fragments = new ArrayList<>();
        mTabEntities = new ArrayList<>();

        for (int i = 0, count = mTitles.length; i < count; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }

        fragments.add(PartyMapNewFragment.newInstance());
        fragments.add(OrganizingLifeMaterialFragment.newInstance());
        fragments.add(StudyFragment.newInstance());
        fragments.add(PartyServiceFragment.newInstance());
        fragments.add(OnlineOfficeFragment.newInstance());

        tabLayout.setTabData(mTabEntities, this, R.id.fl_change, fragments);

        tabLayout.setCurrentTab(currentTab);

        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                boolean twoIndex =
                        (0 == position) ||
                                (1 == position);
//                                || (3 == position)
                if (twoIndex && !isLogin()) {
                    LocBroadcast.getInstance().unRegisterBroadcast(MainActivity.this, mActions);
//                    tabLayout.setCurrentTab(1);
                    ToastHelper.showShort("请登录后查看");
                    LoginActivity.startActivity(MainActivity.this);
                }
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
    }

    /**
     * 是否有登录
     *
     * @return true：登录，false：没登录
     */
    private boolean isLogin() {
        return PreferenceUtil.getBoolean(Constant.AUTO_LOGIN, false);
    }

    private void doublePressBackToast() {
        if (!isBackPressed) {
            CommonLogger.i("doublePressBackToast", "再次点击返回退出程序");
            isBackPressed = true;
            Toast.makeText(this.getApplicationContext(), "再次点击返回退出程序", Toast.LENGTH_SHORT).show();
        } else {
            CommonLogger.i("doublePressBackToast", "exit");
            finish();
            AppManager.getInstance().appExit();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isBackPressed = false;
            }
        }, 2000);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            doublePressBackToast();
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    protected void onResume() {
        super.onResume();

        //普通情况下，autoConvertDensity一系列的方法需要自行根据项目修改。
        AutoSize.autoConvertDensityOfGlobal(this);
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, TAG + "-->onDestroy");
        unQueryConfInfoSubscribe();
        tabLayout.removeAllViews();
        fragments = null;
        super.onDestroy();
        AllenVersionChecker.getInstance().cancelAllMission(this);
    }

    /**
     * 检查更新
     */
    private void queryVersion() {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryNewVersion()
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<VersionBean>() {
                    @Override
                    public void onSuccess(VersionBean baseData) {
                        DialogUtils.dismissProgressDialog();

                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            if (baseData.data.versionNumber > PackageUtil.getVersionCode(MainActivity.this)) {
                                if (UpdateUtils.isPlayFreely()) {
                                    showPlayFreelyDialog(baseData.data);
                                } else {
                                    update(baseData.data);
                                }
                            }
                        } else {
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                    }
                });
    }

    private DownloadBuilder builder;

    private void update(VersionBean.DataBean dataBean) {
        UIData uiData = crateUIData(dataBean);
        builder = AllenVersionChecker
                .getInstance()
                .downloadOnly(uiData);

        builder.setShowDownloadingDialog(false);

        builder.setCustomVersionDialogListener(new CustomVersionDialogListener() {
            @Override
            public Dialog getCustomVersionDialog(Context context, UIData versionBundle) {
                return createCustomDialogTwo(context, versionBundle);
            }
        });

        builder.setForceRedownload(true);
        builder.setDownloadAPKPath(Environment.getExternalStorageDirectory() + "/xinji/updateVersion/");

        builder.executeMission(this);
    }

    private BaseDialog createCustomDialogTwo(Context context, UIData versionBundle) {
        BaseDialog baseDialog = new BaseDialog(context, R.style.BaseDialog, R.layout.dialog_update_version);
        TextView textView = baseDialog.findViewById(R.id.tv_msg);
        textView.setText(versionBundle.getContent());
        baseDialog.setCanceledOnTouchOutside(true);
        return baseDialog;
    }

    /**
     * @return
     * @important 使用请求版本功能，可以在这里设置downloadUrl
     * 这里可以构造UI需要显示的数据
     * UIData 内部是一个Bundle
     */
    private UIData crateUIData(VersionBean.DataBean dataBean) {
        UIData uiData = UIData.create();
        uiData.setTitle("检测到新版本");
        uiData.setDownloadUrl(dataBean.apkUrl);
        uiData.setContent(dataBean.context);
        return uiData;
    }


    /**
     * 监听广播
     *
     * @param broadcastName
     * @param obj
     */
    @Override
    public void onReceive(String broadcastName, Object obj) {
        switch (broadcastName) {
            case CustomBroadcastConstants.GET_CONF_END:
                break;

            case CustomBroadcastConstants.ACTION_CALL_END:
//                isAddLocal = false;
//                stopConfInfoSubscribe();
                break;

            case CustomBroadcastConstants.ADD_LOCAL_VIEW:
                Log.i("OrganizingLifeFragment", "CustomBroadcastConstants.ADD_LOCAL_VIEW" + System.currentTimeMillis());
                break;

            case CustomBroadcastConstants.LOGIN_SUCCESS:
                LogUtil.i(UIConstants.DEMO_TAG, "login success");

                retryCount = 0;

                LoginCenter.getInstance().getSipAccountInfo().setSiteName(PreferencesHelper.getData(UIConstants.SITE_NAME));
                //是否登录
                PreferencesHelper.saveData(UIConstants.IS_LOGIN, true);
                PreferencesHelper.saveData(UIConstants.REGISTER_RESULT, "0");
                ToastHelper.showShort("华为平台登录成功");

                //判断是否有白名单
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    //判断是否在白名单
                    if (!SystemUtil.isIgnoringBatteryOptimizations()) {
                        SystemUtil.requestIgnoreBatteryOptimizations();
                    }
                }

                createQueryConfInfoRetryRequest();
                break;

            case CustomBroadcastConstants.LOGIN_FAILED:
                String errorMessage = ((String) obj);
                LogUtil.i(UIConstants.DEMO_TAG, "login failed," + errorMessage);

//                ToastHelper.showShort("华为平台登录失败" + errorMessage);
                retryCount++;
                if (retryCount <= 3) {
                    //登录华为平台
                    HuaweiLoginImp.getInstance().loginRequest(MainActivity.this, accountBean.siteAccount, accountBean.sitePwd, Urls.SMC_REGISTER_SERVER, Urls.SMC_REGISTER_PORT, "", "", "", "");
                }
                break;

            case CustomBroadcastConstants.LOGOUT:
                LogUtil.i(UIConstants.DEMO_TAG, "logout success");
                break;
        }
    }

    private Subscription queryHuaweiRegisterSubscription;

    /**
     * 查询华为登陆状态
     */
    private void createQueryConfInfoRetryRequest() {
        unQueryConfInfoSubscribe();

        queryHuaweiRegisterSubscription = Observable.interval(5, TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (NetworkUtil.isNetworkAvailable(getApplicationContext())) {
                            String registerStatus = PreferencesHelper.getData(UIConstants.REGISTER_RESULT_TEMP);
                            if (!HuaweiCallImp.LOGIN_STATUS.equals(registerStatus) && PreferenceUtil.getBoolean(Constant.AUTO_LOGIN, false)) {
                                //登录华为平台
                                HuaweiLoginImp.getInstance().loginRequest(
                                        MainActivity.this,
                                        accountBean.siteAccount,
                                        accountBean.sitePwd,
                                        Urls.SMC_REGISTER_SERVER,
                                        Urls.SMC_REGISTER_PORT,
                                        "",
                                        "",
                                        "",
                                        "");
                            }
                        }
                    }
                });
    }

    private void unQueryConfInfoSubscribe() {
        if (null != queryHuaweiRegisterSubscription) {
            if (!queryHuaweiRegisterSubscription.isUnsubscribed()) {
                queryHuaweiRegisterSubscription.unsubscribe();
                queryHuaweiRegisterSubscription = null;
            }
        }
    }

    /**
     * 畅玩显示自定义的升级对话框
     */
    private void showPlayFreelyDialog(VersionBean.DataBean dataBean) {
        BaseDialog baseDialog = new BaseDialog(this, R.style.BaseDialog, R.layout.dialog_update_version_play_freely);
        TextView textView = baseDialog.findViewById(R.id.tv_msg);
        textView.setText(dataBean.context);
        baseDialog.setCanceledOnTouchOutside(true);

        Button btCancel = (Button) baseDialog.findViewById(R.id.bt_cancel);
        Button btCommit = (Button) baseDialog.findViewById(R.id.bt_commit);

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                baseDialog.dismiss();
            }
        });

        btCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateUtils.launchAppDetail(MainActivity.this, getApplication().getPackageName(), UpdateUtils.HUAWEI_STORE);
                baseDialog.dismiss();
            }
        });

        baseDialog.show();
    }
}
