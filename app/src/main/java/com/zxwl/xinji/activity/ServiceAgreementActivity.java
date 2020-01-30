package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;

/**
 * 服务协议
 */
public class ServiceAgreementActivity extends BaseActivity {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvContent;
    private TextView tv_title;

    public static final String NAME = "NAME";

    private String name;

    public static void startActivity(Context context, String name) {
        Intent intent = new Intent(context, ServiceAgreementActivity.class);
        intent.putExtra(NAME, name);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvContent = (TextView) findViewById(R.id.tv_content);
        tv_title = (TextView) findViewById(R.id.tv_title);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        name = getIntent().getStringExtra(NAME);

        tvTopTitle.setText(name);

        tvContent.setText("服务协议".equals(name) ? getString(R.string.service_content) : getString(R.string.baohu));
        tv_title.setVisibility("服务协议".equals(name) ? View.VISIBLE : View.GONE);
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
        return R.layout.activity_service_agreement;
    }
}
