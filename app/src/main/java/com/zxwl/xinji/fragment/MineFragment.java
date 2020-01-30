package com.zxwl.xinji.fragment;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zxwl.commonlibrary.BaseLazyFragment;
import com.zxwl.commonlibrary.utils.DialogUtils;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.commonlibrary.widget.CircleImageView;
import com.zxwl.frame.inter.HuaweiLoginImp;
import com.zxwl.network.Urls;
import com.zxwl.network.api.LoginApi;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.api.UserInfoApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.activity.AboutActivity;
import com.zxwl.xinji.activity.ChangePwdActivity;
import com.zxwl.xinji.activity.DataStatisticsActivity;
import com.zxwl.xinji.activity.LoginActivity;
import com.zxwl.xinji.activity.NotifActivity;
import com.zxwl.xinji.activity.ProposalActivity;
import com.zxwl.xinji.activity.RefreshRecyclerActivity;
import com.zxwl.xinji.service.HeadService;
import com.zxwl.xinji.utils.Constant;
import com.zxwl.xinji.utils.LQRPhotoSelectUtils;
import com.zxwl.xinji.utils.PreferenceUtil;
import com.zxwl.xinji.widget.ButtomSelectDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import okhttp3.RequestBody;
import rx.Observable;
import rx.functions.Func1;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * 我的
 */
public class MineFragment extends BaseLazyFragment implements View.OnClickListener {
    private ImageView ivMsg;
    private CircleImageView ivHead;
    private TextView tvName;
    private TextView tvOrganizationLable;
    private TextView tvSjdb;
    private TextView tvXspb;
    private TextView tvNotif;
    private TextView tvSjtj;
    private TextView tvChangePwd;
    private TextView tvJyfk;
    private TextView tvAbout;
    private TextView tvLogout;
    private TextView tvMsgNumber;
    private FrameLayout flMsg;

    private LoginBean.AccountBean accountBean;
    MineFragment fragment;

    public MineFragment() {
    }

    public static MineFragment newInstance() {
        MineFragment fragment = new MineFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected View inflateContentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }

    @Override
    protected void findViews(View view) {
        ivMsg = (ImageView) view.findViewById(R.id.iv_msg);
        ivHead = (CircleImageView) view.findViewById(R.id.iv_head);
        tvName = (TextView) view.findViewById(R.id.tv_name);
        tvOrganizationLable = (TextView) view.findViewById(R.id.tv_organization_lable);
        tvSjdb = (TextView) view.findViewById(R.id.tv_sjdb);
        tvXspb = (TextView) view.findViewById(R.id.tv_xspb);
        tvNotif = (TextView) view.findViewById(R.id.tv_notif);
        tvSjtj = (TextView) view.findViewById(R.id.tv_sjtj);
        tvChangePwd = (TextView) view.findViewById(R.id.tv_change_pwd);
        tvJyfk = (TextView) view.findViewById(R.id.tv_jyfk);
        tvAbout = (TextView) view.findViewById(R.id.tv_about);
        tvLogout = (TextView) view.findViewById(R.id.tv_logout);
        tvMsgNumber = (TextView) view.findViewById(R.id.tv_msg_number);
        flMsg = (FrameLayout) view.findViewById(R.id.fl_msg);

        view.findViewById(R.id.tv_update_pwd).setOnClickListener(this);
    }

    @Override
    protected void addListeners() {
        tvJyfk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProposalActivity.startActivity(getActivity(),ProposalActivity.TITLE_JYFK);
            }
        });
        tvAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutActivity.startActivity(getActivity());
            }
        });

//        if(isLogin()){
        ivMsg.setOnClickListener(this);
        tvSjdb.setOnClickListener(this);
        tvXspb.setOnClickListener(this);
        tvSjtj.setOnClickListener(this);
        tvChangePwd.setOnClickListener(this);
        tvLogout.setOnClickListener(this);
        ivHead.setOnClickListener(this);
//        }
    }

    @Override
    protected void initData() {
        if (null != accountBean) {
            tvName.setText(accountBean.name);
            tvOrganizationLable.setText(accountBean.flag);

            initPhotoSelect();
            register();
        } else {
            tvLogout.setVisibility(View.GONE);
        }
    }

    /**
     * 显示状态改变时调用
     * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
     * 若是初始就show的Fragment 为了触发该事件 需要先hide再show
     *
     * @param hidden hidden True if the fragment is now hidden, false if it is not
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (isLogin()) {
            if (null == accountBean) {
                accountBean = PreferenceUtil.getUserInfo(getActivity());
            }

            //普通账户
            if(accountBean.checkAdmin==0){
                flMsg.setVisibility(View.INVISIBLE);
                tvSjdb.setVisibility(View.GONE);
            }else {
                flMsg.setVisibility(View.VISIBLE);
                tvSjdb.setVisibility(View.VISIBLE);

                //查询新的督办数量
                queryDbsjCount();
                //查询消息
                queryNotifCount();
            }
            setAvatar(accountBean.url);
        }
    }

    /**
     * 是否有登录
     *
     * @return true：登录，false：没登录
     */
    private boolean isLogin() {
        return PreferenceUtil.getBoolean(Constant.AUTO_LOGIN, false);
    }

    @Override
    public void onClick(View v) {
        if (!isLogin()) {
            ToastHelper.showShort("请登录后查看");
            LoginActivity.startActivity(getActivity());
            return;
        }

        switch (v.getId()) {
            case R.id.iv_msg:
                tvMsgNumber.setVisibility(View.GONE);
                NotifActivity.startActivity(getActivity());
                break;

            //事件督办
            case R.id.tv_sjdb:
                RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_SJDB);
                break;

            //数据统计
            case R.id.tv_sjtj:
                DataStatisticsActivity.startActivity(getActivity());
                break;

            case R.id.tv_logout:
                showCallDialog();
                break;

            case R.id.iv_head:
                showSelectDialog();
                break;

            case R.id.tv_xspb:
                RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_XSPB);
                break;

            case R.id.tv_update_pwd:
                ChangePwdActivity.startActivity(getActivity());
                break;
        }
    }

    private MaterialDialog logOutDialog = null;

    /**
     * 显示对话框
     */
    private void showCallDialog() {
        View inflate = View.inflate(getActivity(), R.layout.dialog_call, null);
        TextView tvNumber = inflate.findViewById(R.id.tv_title);
        TextView tvCall = inflate.findViewById(R.id.tv_ok);
        TextView tvCancle = inflate.findViewById(R.id.tv_cancle);

        tvNumber.setText("您确定要退出登录?");
        tvNumber.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_E42417));
        tvCall.setText("确认");
        tvCancle.setText("取消");

        tvCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != logOutDialog) {
                    logOutDialog.dismiss();
                }
                logOut();
            }
        });

        tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != logOutDialog) {
                    logOutDialog.dismiss();
                }
            }
        });

        logOutDialog = new MaterialDialog.Builder(getActivity())
                .customView(inflate, false)
                .build();
        //点击对话框以外的地方，对话框不消失
        logOutDialog.setCanceledOnTouchOutside(false);
        //点击对话框以外的地方和返回键，对话框都不消失
        //callDialog.setCancelable(false);
        logOutDialog.show();
    }

    /**
     * 登出
     */
    private void logOut() {
        DialogUtils.showProgressDialog(getActivity(), "正在登出...");

        HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(LoginApi.class)
                .logout(0)
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        DialogUtils.dismissProgressDialog();

                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            PreferenceUtil.put(Constant.AUTO_LOGIN, false);
                            PreferenceUtil.putUserInfo(getActivity(), null);
                            PreferenceUtil.put(Constant.PASS_WORD, "");
                            HuaweiLoginImp.getInstance().logOut();
                            HeadService.stopService(getActivity());
                            LoginActivity.startActivity(getActivity());
                            getActivity().finish();
                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        DialogUtils.dismissProgressDialog();
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }


    private ButtomSelectDialog buttomSelectDialog;

    /**
     * 显示选择对话框
     */
    private void showSelectDialog() {
        buttomSelectDialog = new ButtomSelectDialog(getActivity(), R.style.inputDialogStyle);
        buttomSelectDialog.setClickListener(new ButtomSelectDialog.onItemClickListener() {
            @Override
            public void selectClick(int type) {
                switch (type) {
                    case ButtomSelectDialog.TYPE_PICTURE:
                        takePhoto = true;
                        takePhotoPermission();
                        buttomSelectDialog.dismiss();
                        break;

                    case ButtomSelectDialog.TYPE_ALBUM:
                        takePhoto = false;
                        takePhotoPermission();
                        buttomSelectDialog.dismiss();
                        break;
                }
            }
        });
        buttomSelectDialog.show();
    }

    private LQRPhotoSelectUtils lqrPhotoSelectUtils;
    private String base64;
    private String avatarUrl;
    private boolean takePhoto = false;//是否是拍照获取
    private void initPhotoSelect() {
        // 1、创建LQRPhotoSelectUtils（一个Activity对应一个LQRPhotoSelectUtils）
//        lqrPhotoSelectUtils = new LQRPhotoSelectUtils(this, new LQRPhotoSelectUtils.PhotoSelectListener() {
//            @Override
//            public void onFinish(File outputFile, Uri outputUri) {
//                lunban(outputFile);
//            }
//        }, true);//true裁剪，false不裁剪

        // 1、创建LQRPhotoSelectUtils（一个Activity对应一个LQRPhotoSelectUtils）
        lqrPhotoSelectUtils = new LQRPhotoSelectUtils(this, new LQRPhotoSelectUtils.PhotoSelectListener() {
            @Override
            public void onFinish(File outputFile, Uri outputUri) {
                lunban(outputFile);
            }
        }, true);
    }

    private void lunban(File outputFile) {
        Luban.with(getActivity())
                .load(outputFile)
                .ignoreBy(100)
                .putGear(50)
                .setTargetDir(getPath())
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                    }
                })
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                    }

                    @Override
                    public void onSuccess(File file) {
                        //TODO 压缩成功后调用，返回压缩后的图片文件
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] bytes = baos.toByteArray();

                        base64 = Base64.encodeToString(bytes, Base64.NO_WRAP);

                        //上传头像
                        loadAvatar();
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO 当压缩过程出现问题时调用
                        // Toast.makeText(CheckInActivity.this, "压缩失败", Toast.LENGTH_SHORT).show();
                    }
                }).launch();
    }

    private String getPath() {
        String path = Environment.getExternalStorageDirectory() + "/Luban/image/";
        File file = new File(path);
        if (file.mkdirs()) {
            return path;
        }
        return path;
    }

    /**
     * 请求拍照权限
     */
    private void takePhotoPermission() {
        // 3、调用拍照方法
        PermissionGen.with(this)
                .addRequestCode(LQRPhotoSelectUtils.REQ_TAKE_PHOTO)
                .permissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                ).request();
    }

    /**
     * 得到拍照权限
     */
    @PermissionSuccess(requestCode = LQRPhotoSelectUtils.REQ_TAKE_PHOTO)
    private void takePhoto() {
        if (takePhoto) {
            lqrPhotoSelectUtils.takePhoto();
        } else {
            lqrPhotoSelectUtils.selectPhoto();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 2、在Activity中的onActivityResult()方法里与LQRPhotoSelectUtils关联
        lqrPhotoSelectUtils.attachToActivityForResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    /**
     * 没有获取权限
     */
    @PermissionFail(requestCode = LQRPhotoSelectUtils.REQ_TAKE_PHOTO)
    private void showTip1() {
        showDialog();
    }

    /**
     * 显示权限对话框
     */
    public void showDialog() {
        //创建对话框创建器
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //设置对话框显示小图标
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        //设置标题
        builder.setTitle("权限申请");
        //设置正文
        builder.setMessage("在设置-应用-权限 中开启相机、存储权限，才能正常使用拍照或图片选择功能");

        //添加确定按钮点击事件
        builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {//点击完确定后，触发这个事件
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //这里用来跳到手机设置页，方便用户开启权限
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        //添加取消按钮点击事件
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        //使用构建器创建出对话框对象
        AlertDialog dialog = builder.create();
        dialog.show();//显示对话框
    }

    /**
     * 上传头像
     */
    private void loadAvatar() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("base64", base64);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(Urls.MEDIA_TYPE, jsonObject.toString());

        DialogUtils.showProgressDialog(getActivity(), "正在上传头像...");

        HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(UserInfoApi.class)
                .loadAvatar(accountBean.id, body)
                .flatMap(new Func1<BaseData, Observable<?>>() {
                    @Override
                    public Observable<?> call(BaseData baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            avatarUrl = baseData.message;
                            return HttpUtils.getInstance(getActivity())
                                    .getRetofitClinet()
                                    .setBaseUrl(Urls.BASE_URL)
                                    .builder(UserInfoApi.class)
                                    .updateAvatar(accountBean.id, avatarUrl);
                        } else {
                            return null;
                        }
                    }
                })
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            // 4、当拍照或从图库选取图片成功后回调
                            setAvatar(avatarUrl);
                            ToastHelper.showShort("上传头像成功");
                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                        DialogUtils.dismissProgressDialog();
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        DialogUtils.dismissProgressDialog();
                        ToastHelper.showShort("上传头像失败");
                    }
                });
    }

    private void setAvatar(String url) {
        Glide.with(mContext)
                .asBitmap()
                .apply(new RequestOptions().placeholder(R.mipmap.ic_minel_head).error(R.mipmap.ic_minel_head))
                .load(url)
                .into(ivHead);
    }

    //EventBus的处理事件函数  该方法可以随意取名 必须为public 必须添加注解并指定线程模型
    //之后EventBus会自动扫描到此函数，进行数据传递
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getData(EventMessage eventMessage) {
        switch (eventMessage.message) {
            case Messages.NEW_NOTIF:
                tvMsgNumber.setText(eventMessage.succeed);
                tvMsgNumber.setVisibility(View.VISIBLE);
                break;

            case Messages.NEW_SJDB:
                queryDbsjCount();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unRegister();
    }

    private void register() {
        if (!EventBus.getDefault().isRegistered(this)) {
            //注册EventBsus,注意参数是this，传入activity会报错
            EventBus.getDefault().register(this);
        }
    }

    private void unRegister() {
        if (EventBus.getDefault().isRegistered(this)) {
            //注册EventBus,注意参数是this，传入activity会报错
            EventBus.getDefault().unregister(this);
        }
    }


    /**
     * 查询新的督办数量
     */
    private void queryDbsjCount() {
        HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryDbsjUnReadCount(Integer.valueOf(accountBean.id))
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            if ("0".equals(baseData.urgeCount)) {
                                setTextVisibility(tvNotif,false);
                            } else {
                                setTextVisibility(tvNotif,true);
                                if (Integer.valueOf(baseData.urgeCount) > 99) {
                                    tvNotif.setText("99+");
                                } else {
                                    tvNotif.setText(baseData.urgeCount);
                                }
                            }

                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        ToastHelper.showShort("onError:" + responeThrowable.getCause().toString());
                    }
                });
    }
    /**
     * 查询消息
     */
    private void queryNotifCount() {
        HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryNotifUnReadCount()
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            if ("0".equals(baseData.message)) {
                                tvMsgNumber.setVisibility(View.GONE);
                            } else {
                                tvMsgNumber.setVisibility(View.VISIBLE);
                                if (Integer.valueOf(baseData.message) > 99) {
                                    tvMsgNumber.setText("99+");
                                } else {
                                    tvMsgNumber.setText(baseData.message);
                                }
                            }
                        } else {
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        ToastHelper.showShort("onError:" + responeThrowable.getCause().toString());
                    }
                });
    }
    /**
     * 设置view的显示
     * @param textView
     * @param show
     */
    private void setTextVisibility(TextView textView,boolean show){
        textView.setVisibility(show?View.VISIBLE:View.GONE);
    }

}
