package com.zxwl.xinji.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.orhanobut.logger.Logger;
import com.zxwl.commonlibrary.utils.AppUtil;
import com.zxwl.network.Urls;
import com.zxwl.network.api.LoginApi;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.bean.response.CheckBean;
import com.zxwl.network.bean.response.MessageBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.interceptor.CommonLogger;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.activity.LoginActivity;
import com.zxwl.xinji.utils.Constant;
import com.zxwl.xinji.utils.PreferenceUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 心跳服务
 */
public class HeadService extends Service {
    private Subscription confInfoSubscribe;

    public HeadService() {
    }

    public static void startService(Context context) {
        Intent intent = new Intent(context, HeadService.class);
        context.startService(intent);
    }

    public static void stopService(Context context) {
        Intent intent = new Intent(context, HeadService.class);
        context.stopService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //检查是否有消息
        checkAccount();
        return START_STICKY;
    }

    /**
     * 停止查询数据
     */
    private void stopConfInfoSubscribe() {
        if (null != confInfoSubscribe) {
            if (!confInfoSubscribe.isUnsubscribed()) {
                CommonLogger.i("HeadService", "stopConfInfoSubscribe");
                confInfoSubscribe.unsubscribe();
                confInfoSubscribe = null;
            }
        }
    }

    //是否请求数据
    private boolean isRequest = true;

    /**
     * 查询会议是否有数据
     * 循环查询会议信息
     */
    private void checkAccount() {
        stopConfInfoSubscribe();

        String name = PreferenceUtil.getString(Constant.USER_NAME, "");
        String pwd = PreferenceUtil.getString(Constant.PASS_WORD, "");

        confInfoSubscribe = Observable
                .interval(2, TimeUnit.SECONDS)
                .flatMap(new Func1<Long, Observable<CheckBean>>() {
                    @Override
                    public Observable<CheckBean> call(Long aLong) {
                        if (isRequest) {
                            return HttpUtils.getInstance(HeadService.this)
                                    .getRetofitClinet()
                                    .setBaseUrl(Urls.BASE_URL)
                                    .builder(LoginApi.class)
                                    .checkAccount(name, AppUtil.getDeviceIdIMEI(HeadService.this));
                        } else {
                            return null;
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxSubscriber<CheckBean>() {
                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        CommonLogger.i("HeadService", "出异常了");
                        if (isRequest) {
                            checkAccount();
                        }
                    }

                    @Override
                    public void onSuccess(CheckBean baseData) {
                        Logger.i("HeadService", baseData.toString());
                        if ("1".equals(baseData.message)) {
                            PreferenceUtil.put(Constant.AUTO_LOGIN, false);
                            PreferenceUtil.putUserInfo(getApplication(), null);

                            stopConfInfoSubscribe();
                            //显示强制退出登录对话框
                            EventBus.getDefault().postSticky(new EventMessage(Messages.FORCE_LOGOUT, ""));
                            onDestroy();
                        } else if ("2".equals(baseData.message)) {
                            boolean notifa = PreferenceUtil.getBoolean(Constant.NOTIFA_HINT, true);

                            if (!notifa) {
                                return;
                            }

                            MessageBean messageBean = baseData.data;

                            // 1普通公告  2组织邀请
                            switch (messageBean.type) {
                                //普通公告
                                case 1:
                                    break;

                                //组织邀请
                                case 2:
                                    break;

                                //华为账号掉线
                                case 3:
                                    break;

                                default:
                                    break;
                            }
                        }//有未读消息
                        else if ("3".equals(baseData.message)) {
                        }//有未读消息
                        else if (baseData.message.startsWith("4")) {
//                            String[] split = baseData.message.split(",");
//                            String number = split.length>1?split[1]:"";
//                            //显示强制退出登录对话框
//                            EventBus.getDefault().post(new EventMessage(Messages.NEW_NOTIF, number));
                        }
                    }
                });
    }

    private MaterialDialog forceDialog;

    /**
     * 显示强制退出
     */
    private void showForceDialog() {
        View inflate = View.inflate(this, R.layout.dialog_force_logout, null);
        TextView tvCall = inflate.findViewById(R.id.tv_ok);

        tvCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != forceDialog) {
                    forceDialog.dismiss();
                }
                LoginActivity.startActivity(HeadService.this);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonLogger.i("HeadService", "服务停止了");
        stopConfInfoSubscribe();
        isRequest = false;
    }
}
