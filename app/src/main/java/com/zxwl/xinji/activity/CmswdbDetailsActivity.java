package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.zxwl.network.bean.response.CmswdbBean;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;

/**
 * 村民事务代办
 */
public class CmswdbDetailsActivity extends BaseActivity {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvStartTime;
    private TextView tvApply;
    private TextView tvReceiver;
    private TextView tvOpinion;
    private TextView tvCloseTime;
    private TextView tvResult;
    private TextView tvContent;

    public static final String CONTENT_BEAN = "CONTENT_BEAN";
    private CmswdbBean cmswdbBean;

    public static void startActivity(Context context, CmswdbBean cmswdbBean) {
        Intent intent = new Intent(context, CmswdbDetailsActivity.class);
        intent.putExtra(CONTENT_BEAN, cmswdbBean);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvStartTime = (TextView) findViewById(R.id.tv_start_time);
        tvApply = (TextView) findViewById(R.id.tv_apply);
        tvReceiver = (TextView) findViewById(R.id.tv_receiver);
        tvOpinion = (TextView) findViewById(R.id.tv_opinion);
        tvCloseTime = (TextView) findViewById(R.id.tv_close_time);
        tvResult = (TextView) findViewById(R.id.tv_result);
        tvContent = (TextView) findViewById(R.id.tv_content);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);
        tvTopTitle.setText("村民事务代办详情");

        cmswdbBean = (CmswdbBean) getIntent().getSerializableExtra(CONTENT_BEAN);
        tvStartTime.setText(cmswdbBean.receiveTime);
        tvApply.setText(cmswdbBean.applicant);
        tvReceiver.setText(cmswdbBean.assignee);
        tvOpinion.setText(cmswdbBean.replyOpinion);
        tvCloseTime.setText(cmswdbBean.completeTime);
        tvResult.setText(cmswdbBean.result);
        tvContent.setText(cmswdbBean.content);
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
        return R.layout.activity_cmswdb_details;
    }

}
