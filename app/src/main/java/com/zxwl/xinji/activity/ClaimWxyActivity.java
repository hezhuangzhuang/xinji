package com.zxwl.xinji.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.zxwl.commonlibrary.utils.DialogUtils;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.widget.ContainsEmojiEditText;

import org.greenrobot.eventbus.EventBus;

/**
 * 认领微心愿
 */
public class ClaimWxyActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBackOperate;
    private TextView tvLeftOperate;
    private TextView tvTopTitle;
    private TextView tvRightOperate;
    private ContainsEmojiEditText etName;
    private ContainsEmojiEditText etPartyBranch;
    private ContainsEmojiEditText etPhone;

    public static final String WXY_ID = "WXY_ID";

    private int wxyId;

    public static void startActivity(Activity context, int wxyId) {
        Intent intent = new Intent(context, ClaimWxyActivity.class);
        intent.putExtra(WXY_ID, wxyId);
        context.startActivityForResult(intent, WxyDetailsActivity.TYPE_CLAIM);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvLeftOperate = (TextView) findViewById(R.id.tv_left_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvRightOperate = (TextView) findViewById(R.id.tv_right_operate);
        etName = (ContainsEmojiEditText) findViewById(R.id.et_name);
        etPartyBranch = (ContainsEmojiEditText) findViewById(R.id.et_party_branch);
        etPhone = (ContainsEmojiEditText) findViewById(R.id.et_phone);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        wxyId = getIntent().getIntExtra(WXY_ID, -1);

        tvTopTitle.setText("认领党群微心愿");
        ivBackOperate.setVisibility(View.GONE);

        tvRightOperate.setText("确认");
        tvRightOperate.setVisibility(View.VISIBLE);

        tvLeftOperate.setText("取消");
        tvLeftOperate.setVisibility(View.VISIBLE);
    }

    @Override
    protected void setListener() {
        tvLeftOperate.setOnClickListener(this);
        tvRightOperate.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_claim_wxy;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_left_operate:
                finish();
                break;

            case R.id.tv_right_operate:
                String name = etName.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    ToastHelper.showShort("请输入姓名");
                    break;
                }

                String phone = etPhone.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastHelper.showShort("请输入联系方式");
                    break;
                }

                claimWxyRequest(name, phone);
                break;
        }
    }

    private void claimWxyRequest(String name,
                                 String phone) {
        DialogUtils.showProgressDialog(this, "正在认领...");
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .claimWxy(wxyId, name, phone)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        DialogUtils.dismissProgressDialog();
                        if (BaseData.SUCCESS.equals(baseData.result)) {

                            Intent intent = new Intent();
                            intent.putExtra(WxyDetailsActivity.NAME, name);
                            intent.putExtra(WxyDetailsActivity.PHONE, phone);
                            // 设置返回码和返回携带的数据
                            setResult(Activity.RESULT_OK, intent);

                            EventBus.getDefault().post(new EventMessage(Messages.REFRESH_WXY, ""));

                            ToastHelper.showShort("认领成功");

                            finish();
                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        DialogUtils.dismissProgressDialog();
                        ToastHelper.showShort("认领失败,请重新认领");
                    }
                });
    }
}
