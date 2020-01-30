package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jaeger.library.StatusBarUtil;
import com.zxwl.commonlibrary.utils.DialogUtils;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.utils.Constant;
import com.zxwl.xinji.utils.PreferenceUtil;
import com.zxwl.xinji.utils.StringUtils;
import com.zxwl.xinji.widget.ContainsEmojiEditText;

import org.greenrobot.eventbus.EventBus;

/**
 * 添加帮扶责任人
 */
public class AddBfzrrglActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvRightOperate;

    private ContainsEmojiEditText etPoorName;//贫困户姓名
    private ContainsEmojiEditText etDutyUnit;//责任单位
    private ContainsEmojiEditText etDutyPersonName;//责任人姓名
    private ContainsEmojiEditText etDutyPhone;//责任人电话
    private TextView tvType;//脱贫属性
    private TextView tvSex;//帮扶人性别

    private String poorName;//贫困户姓名
    private String dutyUnit;//责任单位
    private String dutyPersonName;//责任人姓名
    private String dutyPersonPhone;//责任人电话
    private int type = -1;//脱贫属性
    private int sex = -1;//帮扶人性别

    private String[] types = {"未脱贫", "已脱贫"};
    private String[] sexs = {"男", "女"};

    public static final String TYPE = "请选择脱贫属性";
    public static final String SEX = "请选择性别";

    private LoginBean.AccountBean accountBean;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AddBfzrrglActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvRightOperate = (TextView) findViewById(R.id.tv_right_operate);

        etPoorName = (ContainsEmojiEditText) findViewById(R.id.et_poor_name);
        etDutyUnit = (ContainsEmojiEditText) findViewById(R.id.et_duty_unit);
        etDutyPersonName = (ContainsEmojiEditText) findViewById(R.id.et_duty_person_name);
        etDutyPhone = (ContainsEmojiEditText) findViewById(R.id.et_duty_phone);
        tvType = (TextView) findViewById(R.id.tv_type);
        tvSex = (TextView) findViewById(R.id.tv_sex);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A),0);


        tvTopTitle.setText("帮扶责任人");
        tvRightOperate.setText("发布");
        tvRightOperate.setVisibility(View.VISIBLE);

        accountBean = PreferenceUtil.getUserInfo(this);
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(this);
        tvRightOperate.setOnClickListener(this);

        tvType.setOnClickListener(this);
        tvSex.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_bfzrr;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_operate:
                finish();
                break;

            case R.id.tv_type:
                showSelectDialog(types, TYPE);
                break;
            case R.id.tv_sex:
                showSelectDialog(sexs, SEX);
                break;

            case R.id.tv_right_operate:
                if (checkText()) {
                    return;
                }
                addBfzrr();
                break;
        }
    }

    /**
     * 判断文本是否有输入
     *
     * @return
     */
    private boolean checkText() {
        poorName = etPoorName.getText().toString().trim();
        if (TextUtils.isEmpty(poorName)) {
            ToastHelper.showShort("姓名不能为空");
            return true;
        }

        dutyUnit = etDutyUnit.getText().toString().trim();
        if (TextUtils.isEmpty(dutyUnit)) {
            ToastHelper.showShort("责任单位不能为空");
            return true;
        }

        dutyPersonName = etDutyPersonName.getText().toString().trim();
        if (TextUtils.isEmpty(dutyPersonName)) {
            ToastHelper.showShort("责任人姓名不能为空");
            return true;
        }

        dutyPersonPhone = etDutyPhone.getText().toString().trim();
        if (TextUtils.isEmpty(dutyPersonPhone)) {
            ToastHelper.showShort("联系电话不能为空");
            return true;
        }

        if (dutyPersonPhone.length() != Constant.PHONE_LENGHT) {
            ToastHelper.showShort("联系电话格式不正确");
            return true;
        }

        if (-1 == type) {
            ToastHelper.showShort("请选择脱贫属性");
            return true;
        }

        if (-1 == sex) {
            ToastHelper.showShort("请选择性别");
            return true;
        }

        return false;
    }

    /**
     * 添加党员联系户
     */
    private void addBfzrr() {
        DialogUtils.showProgressDialog(this, "正在添加...");

        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .addBfzrr(accountBean.id,
                        StringUtils.encoder(poorName),
                        type,
                        StringUtils.encoder(dutyUnit),
                        StringUtils.encoder(dutyPersonName),
                        sex, dutyPersonPhone)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        DialogUtils.dismissProgressDialog();
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            ToastHelper.showShort("添加成功");
                            EventBus.getDefault().post(new EventMessage(Messages.REFRESH_RECYCLER, ""));
                            finish();
                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        DialogUtils.dismissProgressDialog();
                        ToastHelper.showShort("请求error:" + responeThrowable.getCause().toString());
                    }
                });
    }


    private MaterialDialog dialog;

    private void showSelectDialog(String[] values, String hint) {
        dialog = new MaterialDialog.Builder(this)
                .title(hint)
                .items(values)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (hint) {
                            case TYPE:
                                type = (which + 1);
                                tvType.setText(values[which]);
                                break;

                            case SEX:
                                sex = (which + 1);
                                tvSex.setText(values[which]);
                                break;
                        }
                        return true;
                    }
                })
                .build();
        //点击对话框以外的地方，对话框不消失
//        dialog.setCanceledOnTouchOutside(false);
        //点击对话框以外的地方和返回键，对话框都不消失
        //dialog.setCancelable(false);
        dialog.show();
    }
}
