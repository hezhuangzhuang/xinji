package com.zxwl.xinji.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.zxwl.commonlibrary.utils.DialogUtils;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.utils.Constant;
import com.zxwl.xinji.utils.StringUtils;
import com.zxwl.xinji.widget.ContainsEmojiEditText;

/**
 * 建议反馈
 */
public class ProposalActivity extends BaseActivity {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private ContainsEmojiEditText etContent;
    private Button btSave;

    private EditText etPhone;
    private ContainsEmojiEditText etOther;
    private ContainsEmojiEditText etName;

    private TextView tvHint;

    public static final String TITLE = "TITLE";
    public static final String TITLE_JYFK = "建议反馈";
    public static final String TITLE_YJJY = "意见建议";

    private String title;

    private String requestUrl;

    public static void startActivity(Context context, String title) {
        Intent intent = new Intent(context, ProposalActivity.class);
        intent.putExtra(TITLE, title);
        context.startActivity(intent);
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        etContent = (ContainsEmojiEditText) findViewById(R.id.et_content);
        btSave = (Button) findViewById(R.id.bt_save);

        etPhone = (EditText) findViewById(R.id.et_phone);
        etOther = (ContainsEmojiEditText) findViewById(R.id.et_other);
        etName = (ContainsEmojiEditText) findViewById(R.id.et_name);

        tvHint = (TextView) findViewById(R.id.tv_hint);
    }

    @Override

    protected void initData() {
        title = getIntent().getStringExtra(TITLE);

        requestUrl = TITLE_JYFK.equals(title) ? Urls.ADD_FEEDBACK_ACTION : Urls.ADD_ENTITY_ACTION;

        if (TITLE_YJJY.equals(title)) {
            tvHint.setText("请对党建工作提出宝贵意见:");
        } else {
            tvHint.setText("请对我们的软件提出宝贵意见:");
        }
        tvTopTitle.setText(title);

        etName.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        etOther.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etContent.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastHelper.showShort("反馈内容不能为空");
                    return;
                }

                String name = etName.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    ToastHelper.showShort("姓名不能为空");
                    return;
                }

                String phone = etPhone.getText().toString().trim();
                if (TextUtils.isEmpty(phone) || Constant.PHONE_LENGHT != phone.length()) {
                    ToastHelper.showShort("手机号格式不正确");
                    return;
                }

                String other = etOther.getText().toString().trim();

                addFeedbackRequest(phone, content, other, name);
            }
        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_proposal;
    }

    private void addFeedbackRequest(String phone, String content, String other, String name) {
        DialogUtils.showProgressDialog(this, "正在提交...");

        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .addFeedback(requestUrl,
                        phone,
                        StringUtils.encoder(content),
                        StringUtils.encoder(other),
                        StringUtils.encoder(name)
                )
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        DialogUtils.dismissProgressDialog();

                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            Toast.makeText(ProposalActivity.this.getApplicationContext(), "反馈成功", Toast.LENGTH_SHORT).show();
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
}
