package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.network.bean.response.DylxhBean;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;

/**
 * 党员联系户详情
 */
public class PartyContactDetailsActivity extends BaseActivity {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvPartyName;
    private TextView tvPartyPhone;
    private TextView tvContactName;
    private TextView tvContactPhone;
    private TextView tvTime;
    private TextView tvContactAddress;
    private TextView tvContactUnit;

    private DylxhBean dylxhBean;

    public static final String DYLXH_BEAN = "DYLXH_BEAN";

    public static void startActivity(Context context, DylxhBean dylxhBean) {
        Intent intent = new Intent(context, PartyContactDetailsActivity.class);
        intent.putExtra(DYLXH_BEAN, dylxhBean);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvPartyName = (TextView) findViewById(R.id.tv_party_name);
        tvPartyPhone = (TextView) findViewById(R.id.tv_party_phone);
        tvContactName = (TextView) findViewById(R.id.tv_contact_name);
        tvContactPhone = (TextView) findViewById(R.id.tv_contact_phone);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvContactAddress = (TextView) findViewById(R.id.tv_contact_address);
        tvContactUnit = (TextView) findViewById(R.id.tv_contact_unit);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);
        tvTopTitle.setText("志愿服务");

        dylxhBean = (DylxhBean) getIntent().getSerializableExtra(DYLXH_BEAN);

        tvPartyName.setText(dylxhBean.name);
        tvPartyPhone.setText(dylxhBean.phone);
        tvContactName.setText(dylxhBean.cname);
        tvContactPhone.setText(dylxhBean.cphone);
        tvTime.setText(DateUtil.longToString(dylxhBean.createtime,DateUtil.FORMAT_DATE));
        tvContactAddress.setText(dylxhBean.caddress);
        tvContactUnit.setText(dylxhBean.workunit);
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
        return R.layout.activity_party_contact_details;
    }
}
