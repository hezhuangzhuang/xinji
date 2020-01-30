package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.network.bean.response.NotifBean;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;

/**
 * 通知详情
 */
public class NotifDetailsActivity extends BaseActivity {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvTime;
    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvLable;

    private NotifBean notifBean;
    public static final String NOTIF_BEAN ="NOTIF_BEAN";

    public static void startActivity(Context context, NotifBean notifBean) {
        Intent intent = new Intent(context, NotifDetailsActivity.class);
        intent.putExtra(NOTIF_BEAN, notifBean);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvContent = (TextView) findViewById(R.id.tv_content);
        tvLable = (TextView) findViewById(R.id.tv_lable);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        notifBean = (NotifBean) getIntent().getSerializableExtra(NOTIF_BEAN);

        tvTopTitle.setText("通知详情");
        tvLable.setText(notifBean.data.senderName);
        tvContent.setText(notifBean.data.content);
        tvTitle.setText(notifBean.data.title);
        tvTime.setText(DateUtil.longToString(notifBean.data.beginDate, DateUtil.FORMAT_MONTH_DAY_TIME));
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
        return R.layout.activity_notif_details;
    }

}
