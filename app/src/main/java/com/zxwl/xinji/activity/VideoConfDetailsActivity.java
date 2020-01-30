package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.frame.inter.HuaweiCallImp;
import com.zxwl.network.bean.response.ConfBean;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;

/**
 * 视频会议详情
 */
public class VideoConfDetailsActivity extends BaseActivity {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvConfName;
    private TextView tvName;
    private TextView tvDate;
    private TextView tvTime;
    private TextView tvPersonnel;
    private TextView tvJoinConf;

    public static final String CONF_BEAN = "CONF_BEAN";

    private ConfBean confBean;

    public static void startActivity(Context context, ConfBean confBean) {
        Intent intent = new Intent(context, VideoConfDetailsActivity.class);
        intent.putExtra(CONF_BEAN, confBean);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvConfName = (TextView) findViewById(R.id.tv_conf_name);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvDate = (TextView) findViewById(R.id.tv_date);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvPersonnel = (TextView) findViewById(R.id.tv_personnel);
        tvJoinConf = (TextView) findViewById(R.id.tv_join_conf);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        tvTopTitle.setText("视频会议");

        confBean = (ConfBean) getIntent().getSerializableExtra(CONF_BEAN);
        long createTimeLong = DateUtil.stringToLong(confBean.createTime, DateUtil.FORMAT_DATE_TIME_SECOND);
        long beginTimeLong = DateUtil.stringToLong(confBean.beginTime, DateUtil.FORMAT_DATE_TIME_SECOND);
        long endTimeLong = DateUtil.stringToLong(confBean.endTime, DateUtil.FORMAT_DATE_TIME_SECOND);

        tvConfName.setText(confBean.name);
        tvName.setText(confBean.applyPeopleName);

        tvDate.setText(DateUtil.longToString(createTimeLong, DateUtil.FORMAT_DATE));
        tvTime.setText(DateUtil.longToString(beginTimeLong, DateUtil.FORMAT_DATE_TIME) + "-" + DateUtil.longToString(endTimeLong, DateUtil.FORMAT_TIME));
        tvPersonnel.setText(confBean.atendee);
        tvJoinConf.setVisibility(confBean.confState == 3 ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvJoinConf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HuaweiCallImp.getInstance().joinConf(confBean.accessCode);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_conf_details;
    }
}
