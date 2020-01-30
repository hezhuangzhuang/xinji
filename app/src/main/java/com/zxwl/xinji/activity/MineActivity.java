package com.zxwl.xinji.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.CustomVersionDialogListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jaeger.library.StatusBarUtil;
import com.zxwl.commonlibrary.utils.DialogUtils;
import com.zxwl.commonlibrary.utils.PackageUtil;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.commonlibrary.widget.CircleImageView;
import com.zxwl.frame.inter.HuaweiLoginImp;
import com.zxwl.network.Urls;
import com.zxwl.network.api.LoginApi;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.api.UserInfoApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.bean.response.VersionBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.service.HeadService;
import com.zxwl.xinji.utils.Constant;
import com.zxwl.xinji.utils.LQRPhotoSelectUtils;
import com.zxwl.xinji.utils.PreferenceUtil;
import com.zxwl.xinji.utils.UpdateUtils;
import com.zxwl.xinji.widget.BaseDialog;
import com.zxwl.xinji.widget.ButtomSelectDialog;

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
public class MineActivity extends BaseActivity implements View.OnClickListener {
    private FrameLayout flBack;
    private CircleImageView ivHead;
    private TextView tvName;
    private TextView tvOrganizationLable;
    private TextView tvUpdatePwd;
    private TextView tvJyfk;
    private TextView tvAbout;
    private TextView tvLogout;
    private TextView tvCollection;
    private TextView tvCheckUpdate;
    private TextView tvParty;

    private LoginBean.AccountBean accountBean;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MineActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        flBack = (FrameLayout) findViewById(R.id.fl_back);
        ivHead = (CircleImageView) findViewById(R.id.iv_head);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvUpdatePwd = (TextView) findViewById(R.id.tv_update_pwd);
        tvJyfk = (TextView) findViewById(R.id.tv_jyfk);
        tvAbout = (TextView) findViewById(R.id.tv_about);
        tvLogout = (TextView) findViewById(R.id.tv_logout);
        tvOrganizationLable = (TextView) findViewById(R.id.tv_organization_lable);
        tvCollection = (TextView) findViewById(R.id.tv_collection);
        tvCheckUpdate = (TextView) findViewById(R.id.tv_check_update);
        tvParty = (TextView) findViewById(R.id.tv_party);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setTranslucentForImageView(this, 0, null);

        accountBean = PreferenceUtil.getUserInfo(this);

        if (null != accountBean) {
            tvName.setText(accountBean.name);
            tvOrganizationLable.setText(accountBean.flag);
            initPhotoSelect();

            setAvatar(accountBean.url);

            tvParty.setVisibility(1 == accountBean.checkAdmin ? View.VISIBLE : View.GONE);
        } else {
            tvLogout.setVisibility(View.GONE);
            tvParty.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setListener() {
        flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvJyfk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProposalActivity.startActivity(MineActivity.this, ProposalActivity.TITLE_JYFK);
            }
        });
        tvAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutNewActivity.startActivity(MineActivity.this);
            }
        });

        tvUpdatePwd.setOnClickListener(this);
        tvLogout.setOnClickListener(this);
        ivHead.setOnClickListener(this);
        tvCollection.setOnClickListener(this);
        tvCheckUpdate.setOnClickListener(this);
        tvParty.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mine;
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
            LoginActivity.startActivity(this);
            return;
        }

        switch (v.getId()) {
            case R.id.tv_update_pwd:
                ChangePwdActivity.startActivity(MineActivity.this);
                break;

            case R.id.tv_logout:
                showLogOutDialog();
                break;

            case R.id.iv_head:
                showSelectDialog();
                break;

            case R.id.tv_collection:
                CollectionActivity.startActivity(MineActivity.this);
                break;

            case R.id.tv_check_update:
                checkVersion();
                break;

            case R.id.tv_party:
                RefreshRecyclerActivity.startActivity(MineActivity.this, RefreshRecyclerActivity.TYPE_DZB);
                break;
        }
    }

    private ButtomSelectDialog buttomSelectDialog;

    /**
     * 显示选择对话框
     */
    private void showSelectDialog() {
        buttomSelectDialog = new ButtomSelectDialog(this, R.style.inputDialogStyle);
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
        Luban.with(this)
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                intent.setData(Uri.parse("package:" + getPackageName()));
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

        DialogUtils.showProgressDialog(this, "正在上传头像...");

        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(UserInfoApi.class)
                .loadAvatar(accountBean.id, body)
                .flatMap(new Func1<BaseData, Observable<?>>() {
                    @Override
                    public Observable<?> call(BaseData baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            avatarUrl = baseData.message;
                            return HttpUtils.getInstance(MineActivity.this)
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

                            accountBean.url = avatarUrl;
                            PreferenceUtil.putUserInfo(MineActivity.this, accountBean);
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
        Glide.with(this)
                .asBitmap()
                .apply(new RequestOptions().placeholder(R.mipmap.ic_minel_head).error(R.mipmap.ic_minel_head))
                .load(url)
                .into(ivHead);
    }

    private MaterialDialog logOutDialog = null;

    /**
     * 显示对话框
     */
    private void showLogOutDialog() {
        View inflate = View.inflate(this, R.layout.dialog_call, null);
        TextView tvNumber = inflate.findViewById(R.id.tv_title);
        TextView tvCall = inflate.findViewById(R.id.tv_ok);
        TextView tvCancle = inflate.findViewById(R.id.tv_cancle);

        tvNumber.setText("您确定要退出登录?");
        tvNumber.setTextColor(ContextCompat.getColor(this, R.color.color_E42417));
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

        logOutDialog = new MaterialDialog.Builder(this)
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
        DialogUtils.showProgressDialog(this, "正在登出...");

        HttpUtils.getInstance(this)
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
                            PreferenceUtil.putUserInfo(MineActivity.this, null);
                            PreferenceUtil.put(Constant.PASS_WORD, "");
                            HuaweiLoginImp.getInstance().logOut();
                            HeadService.stopService(MineActivity.this);
                            LoginActivity.startActivity(MineActivity.this);
                            finish();
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

    /**
     * 检查更新
     */
    private void checkVersion() {
        DialogUtils.showProgressDialog(this, "正在检查更新...");

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
                            if (baseData.data.versionNumber > PackageUtil.getVersionCode(MineActivity.this)) {
                                if (UpdateUtils.isPlayFreely()) {
                                    showPlayFreelyDialog(baseData.data);
                                } else {
                                    update(baseData.data);
                                }
                            } else {
                                ToastHelper.showShort("当前已是最新版本!");
                            }
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
                UpdateUtils.launchAppDetail(MineActivity.this, getApplication().getPackageName(), UpdateUtils.HUAWEI_STORE);
                baseDialog.dismiss();
            }
        });

        baseDialog.show();
    }

    private DownloadBuilder builder;

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AllenVersionChecker.getInstance().cancelAllMission(this);
    }

}
