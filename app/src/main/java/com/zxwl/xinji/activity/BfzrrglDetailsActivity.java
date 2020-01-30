package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.zxwl.network.bean.response.BfzrrglBean;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;

/**
 * 帮扶责任人管理详情
 */
public class BfzrrglDetailsActivity extends BaseActivity {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;

    private TextView tvPoorName;
    private TextView tvType;
    private TextView tvDutyUnit;
    private TextView tvDutyPersonName;
    private TextView tvSex;
    private TextView tvDutyPhone;
    private TextView tvAddress;

    public static final String BFZRR_BEAN = "BFZRR_BEAN";

    private BfzrrglBean bfzrrglBean;

    public static void startActivity(Context context, BfzrrglBean bfzrrglBean) {
        Intent intent = new Intent(context, BfzrrglDetailsActivity.class);
        intent.putExtra(BFZRR_BEAN, bfzrrglBean);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvPoorName = (TextView) findViewById(R.id.tv_poor_name);
        tvType = (TextView) findViewById(R.id.tv_type);
        tvDutyUnit = (TextView) findViewById(R.id.tv_duty_unit);
        tvDutyPersonName = (TextView) findViewById(R.id.tv_duty_person_name);
        tvSex = (TextView) findViewById(R.id.tv_sex);
        tvDutyPhone = (TextView) findViewById(R.id.tv_duty_phone);
        tvAddress = (TextView) findViewById(R.id.tv_address);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        tvTopTitle.setText("帮扶责任人管理详情");

        bfzrrglBean = (BfzrrglBean) getIntent().getSerializableExtra(BFZRR_BEAN);

        tvPoorName.setText(bfzrrglBean.name);
        tvType.setText(bfzrrglBean.typeValue);
        tvDutyUnit.setText(bfzrrglBean.dutyUnit);
        tvDutyPersonName.setText(bfzrrglBean.dutyPerson);
        tvSex.setText(1 == bfzrrglBean.dutySex ? "男" : "女");
        tvDutyPhone.setText(bfzrrglBean.dutyTel);
        tvAddress.setText(
                (TextUtils.isEmpty(bfzrrglBean.boroughName) ? "" : bfzrrglBean.boroughName) + (TextUtils.isEmpty(bfzrrglBean.villageName) ? "" : bfzrrglBean.villageName));
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bfzrrgl_details;
    }

}
