package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
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

import com.jaeger.library.StatusBarUtil;
import com.zxwl.commonlibrary.utils.AESUtil;
import com.zxwl.commonlibrary.utils.DialogUtils;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.api.LoginApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.service.HeadService;
import com.zxwl.xinji.utils.Constant;
import com.zxwl.xinji.utils.PreferenceUtil;

/**
 * 修改密码
 */
public class ChangePwdActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private Button btSave;
    private EditText etOldPwd;
    private ImageView ivOldShowPwd;
    private EditText etNewPwd;
    private ImageView ivNewShowPwd;
    private EditText etConfirmPwd;
    private ImageView ivConfirmShowPwd;

    private LoginBean.AccountBean accountBean;

    private boolean showOldPwd = false;
    private boolean showNewPwd = false;
    private boolean showConfirmPwd = false;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ChangePwdActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        btSave = (Button) findViewById(R.id.bt_save);
        etOldPwd = (EditText) findViewById(R.id.et_old_pwd);
        ivOldShowPwd = (ImageView) findViewById(R.id.iv_old_show_pwd);
        etNewPwd = (EditText) findViewById(R.id.et_new_pwd);
        ivNewShowPwd = (ImageView) findViewById(R.id.iv_new_show_pwd);
        etConfirmPwd = (EditText) findViewById(R.id.et_confirm_pwd);
        ivConfirmShowPwd = (ImageView) findViewById(R.id.iv_confirm_show_pwd);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        tvTopTitle.setText("修改密码");

        accountBean = PreferenceUtil.getUserInfo(this);
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(this);
        btSave.setOnClickListener(this);
        ivOldShowPwd.setOnClickListener(this);
        ivNewShowPwd.setOnClickListener(this);
        ivConfirmShowPwd.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_pwd;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_operate:
                finish();
                break;


            case R.id.bt_save:
                String oldPwd = etOldPwd.getText().toString().trim();
                checkPwd(oldPwd, "旧密码");

                String newPwd = etNewPwd.getText().toString().trim();
                checkPwd(newPwd, "新密码");

                String confirmPwd = etConfirmPwd.getText().toString().trim();
                checkPwd(confirmPwd, "新密码");

                String passWord = PreferenceUtil.getString(Constant.PASS_WORD, "");

                String finalPwd = "";

                try {
                    if (!AESUtil.encrypt(oldPwd).equals(passWord)) {
                        ToastHelper.showShort("旧密码不正确");
                        return;
                    }

                    if (!newPwd.equals(confirmPwd)) {
                        ToastHelper.showShort("新密码两次输入不相同");
                        return;
                    }

                    finalPwd = AESUtil.encrypt(confirmPwd);

                    if (AESUtil.encrypt(oldPwd).equals(finalPwd)) {
                        ToastHelper.showShort("新密码不能与旧密码相同");
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                DialogUtils.showProgressDialog(ChangePwdActivity.this, "正在修改...");
                updatePwdRequest(finalPwd);
                break;

            case R.id.iv_old_show_pwd:
                showOldPwd = !showOldPwd;
                showPwd(showOldPwd,etOldPwd,ivOldShowPwd);
                break;

            case R.id.iv_new_show_pwd:
                showNewPwd =     ! showNewPwd;
                showPwd(showNewPwd,etNewPwd,ivNewShowPwd);
                break;

            case R.id.iv_confirm_show_pwd:
                showConfirmPwd = !showConfirmPwd;
                showPwd(showConfirmPwd,etConfirmPwd,ivConfirmShowPwd);
                break;
        }
    }

    private void updatePwdRequest(String finalPwd) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .builder(LoginApi.class)
                .updatePwd(finalPwd)
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        DialogUtils.dismissProgressDialog();
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            ToastHelper.showShort("密码修改成功");
                            PreferenceUtil.put(Constant.AUTO_LOGIN, false);
                            PreferenceUtil.putUserInfo(ChangePwdActivity.this, null);
                            PreferenceUtil.put(Constant.PASS_WORD, "");
                            HeadService.stopService(ChangePwdActivity.this);
                            LoginActivity.startActivity(ChangePwdActivity.this);
                            finish();
                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        DialogUtils.dismissProgressDialog();
                        ToastHelper.showShort("onError:" + responeThrowable.getCause().toString());
                    }
                });
    }

    public void checkPwd(String oldPwd, String hintText) {
        if (TextUtils.isEmpty(oldPwd)) {
            ToastHelper.showShort(hintText + "不能为空");
            return;
        }

        if (oldPwd.length() > 20 || oldPwd.length() < 6) {
            ToastHelper.showShort(hintText + "长度应为6~20位");
            return;
        }
    }

    private void showPwd(boolean isShowPwd, EditText etPwd, ImageView ivHidePwd) {
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
    }


    private void setSelection(EditText editText) {
        //设置光标位置
        CharSequence text = editText.getText();
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
    }
}
