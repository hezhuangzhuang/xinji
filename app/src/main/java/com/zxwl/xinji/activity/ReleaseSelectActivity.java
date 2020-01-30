package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;

/**
 * 发布选择
 */
public class ReleaseSelectActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvClose;
    private TextView tvZtdr;
    private TextView tvShyk;
    private TextView tvMzpy;
    private TextView tvZzshh;
    private TextView tvMore;

    public static final String IS_LEFT = "TYPE";

    /**
     * 是否是发布生活类信息，默认为是
     * 或者是发布集体经济
     */
    private boolean isLeft = true;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ReleaseSelectActivity.class);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, boolean isLeft) {
        Intent intent = new Intent(context, ReleaseSelectActivity.class);
        intent.putExtra(IS_LEFT, isLeft);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        tvClose = (TextView) findViewById(R.id.tv_close);
        tvZtdr = (TextView) findViewById(R.id.tv_ztdr);
        tvShyk = (TextView) findViewById(R.id.tv_shyk);
        tvMzpy = (TextView) findViewById(R.id.tv_mzpy);
        tvZzshh = (TextView) findViewById(R.id.tv_zzshh);
        tvMore = (TextView) findViewById(R.id.tv_more);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.white), 0);

        isLeft = getIntent().getBooleanExtra(IS_LEFT, true);

        if (!isLeft) {
            tvZtdr.setText("图说本村");
            tvShyk.setText("荣誉表彰");
            tvMzpy.setText("亮点工作");

            tvZtdr.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.ic_release_select_tsbc, 0, 0);
            tvShyk.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.ic_release_select_rybz, 0, 0);
            tvMzpy.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.ic_release_select_ldgz, 0, 0);

            tvZzshh.setVisibility(View.INVISIBLE);
            tvMore.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void setListener() {
        tvClose.setOnClickListener(this);
        tvZtdr.setOnClickListener(this);
        tvShyk.setOnClickListener(this);
        tvMzpy.setOnClickListener(this);
        tvZzshh.setOnClickListener(this);
        tvMore.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_release_select;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_close:
                finish();
                break;

            case R.id.tv_ztdr:
                if (isLeft) {
                    ReleaseConfActivity.startActivity(ReleaseSelectActivity.this, ReleaseConfActivity.TYPE_ZTDR);
                } else {
                    AddTsbcActivity.startActivity(ReleaseSelectActivity.this, AddTsbcActivity.TYPE_TSBC);
                }
                finish();
                break;

            case R.id.tv_shyk:
                if (isLeft) {
                    ReleaseConfActivity.startActivity(ReleaseSelectActivity.this, ReleaseConfActivity.TYPE_SHYK);
                } else {
                    AddTsbcActivity.startActivity(ReleaseSelectActivity.this, AddTsbcActivity.TYPE_RYBZ);
                }
                finish();
                break;

            case R.id.tv_mzpy:
                if (isLeft) {
                    ReleaseConfActivity.startActivity(ReleaseSelectActivity.this, ReleaseConfActivity.TYPE_MZPY);
                } else {
                    AddTsbcActivity.startActivity(ReleaseSelectActivity.this, AddTsbcActivity.TYPE_LDGZ);
                }
                finish();
                break;

            case R.id.tv_zzshh:
                ReleaseConfActivity.startActivity(ReleaseSelectActivity.this, ReleaseConfActivity.TYPE_ZZSHH);
                finish();
                break;

            case R.id.tv_more:
                ReleaseConfActivity.startActivity(ReleaseSelectActivity.this, ReleaseConfActivity.TYPE_MORE);
                finish();
                break;

        }
    }
}
