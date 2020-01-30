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
 * 用户隐私
 */
public class PrivacyActivity extends BaseActivity {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvContent;

    public static final String TITLE = "TITLE";

    public static final String PRIVACY = "隐私声明";

    public static final String USER = "用户协议";

    private String title;

    public static void startActivity(Context context, String title) {
        Intent intent = new Intent(context, PrivacyActivity.class);
        intent.putExtra(TITLE, title);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvContent = (TextView) findViewById(R.id.tv_content);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);
        title = getIntent().getStringExtra(TITLE);
        tvTopTitle.setText(title);

        tvContent.setText(getTextContent());
    }

    private String getTextContent() {
        if(PRIVACY.equals(title)){
            return getString(R.string.privacy);
        }else {
            return getString(R.string.user);
        }
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_privacy;
    }

}
