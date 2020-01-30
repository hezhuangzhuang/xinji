package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.zxwl.commonlibrary.utils.PackageUtil;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;

public class AboutNewActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvVersion;
    private TextView tvAbout;
    private TextView tvUser;
    private TextView tvPrivacy;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AboutNewActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvVersion = (TextView) findViewById(R.id.tv_version);
        tvAbout = (TextView) findViewById(R.id.tv_about);
        tvUser = (TextView) findViewById(R.id.tv_user);
        tvPrivacy = (TextView) findViewById(R.id.tv_privacy);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        tvTopTitle.setText("关于我们");

        tvVersion.setText(String.format(getResources().getString(R.string.version), PackageUtil.getVersionName(this)));

    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(this);
        tvAbout.setOnClickListener(this);
        tvUser.setOnClickListener(this);
        tvPrivacy.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about_new;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_operate:
                finish();
                break;

            case R.id.tv_about:
                AboutActivity.startActivity(AboutNewActivity.this);
                break;

            case R.id.tv_user:
                PrivacyActivity.startActivity(AboutNewActivity.this, PrivacyActivity.USER);
                break;

            case R.id.tv_privacy:
                PrivacyActivity.startActivity(AboutNewActivity.this, PrivacyActivity.PRIVACY);
                break;
        }
    }
}
