package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.zxwl.network.bean.response.SgdzBean;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;

/**
 * 设岗定责
 */
public class SgdzDetailsActivity extends BaseActivity {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvName;
    private TextView tvGwsdLable;
    private TextView tvGwsd;
    private View viewTwo;
    private TextView tvLxhLable;
    private TextView tvLxh;
    private TextView tvRemarksLable;
    private TextView tvRemarks;
    private TextView tvAddress;

    public static final String TITLE = "TITLE";
    public static final String CONTENT_BEAN = "CONTENT_BEAN";
    private String title;

    private SgdzBean sgdzBean;

    public static final String TYPE_SGDZ = "设岗定责";
    public static final String TYPE_DYLXH = "党员联系户";


    public static void startActivity(Context context, String title, SgdzBean sgdzBean) {
        Intent intent = new Intent(context, SgdzDetailsActivity.class);
        intent.putExtra(TITLE, title);
        intent.putExtra(CONTENT_BEAN, sgdzBean);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = findViewById(R.id.iv_back_operate);
        tvTopTitle = findViewById(R.id.tv_top_title);
        tvName = findViewById(R.id.et_name);
        tvGwsdLable = findViewById(R.id.tv_gwsd_lable);
        tvGwsd = findViewById(R.id.tv_gwsd);
        viewTwo = findViewById(R.id.view_two);
        tvLxhLable = findViewById(R.id.tv_lxh_lable);
        tvLxh = findViewById(R.id.et_lxh);
        tvRemarksLable = findViewById(R.id.tv_remarks_lable);
        tvRemarks = findViewById(R.id.et_remarks);
        tvAddress = findViewById(R.id.et_address);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        sgdzBean = (SgdzBean) getIntent().getSerializableExtra(CONTENT_BEAN);
        title = getIntent().getStringExtra(TITLE);

        tvTopTitle.setText(title);

        tvName.setText(sgdzBean.name);
        tvRemarks.setText(sgdzBean.remark);

        switch (title) {
            //党员联系户
            case TYPE_DYLXH:
                tvGwsdLable.setVisibility(View.GONE);
                tvGwsd.setVisibility(View.GONE);
                viewTwo.setVisibility(View.GONE);

                tvAddress.setText( sgdzBean.vtownsname + sgdzBean.villagename);
                tvLxh.setText(sgdzBean.cname);
                break;

            //设岗定责
            case TYPE_SGDZ:
                tvAddress.setText( sgdzBean.township + sgdzBean.village);

                tvLxhLable.setText("优势特长");
                tvLxh.setText(sgdzBean.specialty);
                tvGwsd.setText(sgdzBean.jobVal);
                break;
        }
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
        return R.layout.activity_sgdz_details;
    }
}
