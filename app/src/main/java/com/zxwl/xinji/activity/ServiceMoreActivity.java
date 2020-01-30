package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.zxwl.network.Urls;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;

public class ServiceMoreActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTopTitle;
    private ImageView ivBackOperate;
    private TextView tvOne;
    private TextView tvTwo;
    private TextView tvThree;
    private TextView tvFour;
    private TextView tvSeven;
    private TextView tvEight;
    private TextView tvNine;
    private TextView tvXxcx;
    private TextView tvGjcx;
    private TextView tvGsk;
    private TextView tvTgb;
    private TextView tvSpdb;

    public static final String TYPE_HCPYD = "火车票预订";
    public static final String TYPE_HBYD = "航班预订";
    public static final String TYPE_GSLK = "高速路况";
    public static final String TYPE_WZCX = "违章查询";
    public static final String TYPE_TQYB = "天气预报";
    public static final String TYPE_KDCX = "快递查询";
    public static final String TYPE_DSZB = "电视直播";

    public static final String TYPE_XXCX = "限行查询";
    public static final String TYPE_GJCX = "公交查询";
    public static final String TYPE_GSK = "高速口";
    public static final String TYPE_TGB = "听广播";
    public static final String TYPE_SPDB = "视频点播";


    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ServiceMoreActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvOne = (TextView) findViewById(R.id.tv_one);
        tvTwo = (TextView) findViewById(R.id.tv_two);
        tvThree = (TextView) findViewById(R.id.tv_three);
        tvFour = (TextView) findViewById(R.id.tv_four);
        tvSeven = (TextView) findViewById(R.id.tv_seven);
        tvEight = (TextView) findViewById(R.id.tv_eight);
        tvNine = (TextView) findViewById(R.id.tv_nine);
        tvXxcx = (TextView) findViewById(R.id.tv_xscx);
        tvGjcx = (TextView) findViewById(R.id.tv_gjcx);
        tvGsk = (TextView) findViewById(R.id.tv_gsk);
        tvTgb = (TextView) findViewById(R.id.tv_tgb);
        tvSpdb = (TextView) findViewById(R.id.tv_spdb);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        tvTopTitle.setText("生活服务");
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvOne.setOnClickListener(this);
        tvTwo.setOnClickListener(this);
        tvThree.setOnClickListener(this);
        tvFour.setOnClickListener(this);
        tvSeven.setOnClickListener(this);
        tvEight.setOnClickListener(this);
        tvNine.setOnClickListener(this);

        tvXxcx.setOnClickListener(this);
        tvGjcx.setOnClickListener(this);
        tvGsk .setOnClickListener(this);
        tvTgb .setOnClickListener(this);
        tvSpdb .setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_service_more;
    }

    @Override
    public void onClick(View v) {
        TextView textView = (TextView) v;

        switch (textView.getText().toString().trim()) {
            case TYPE_HCPYD:
                startWebActivity(Urls.HCPYD_URL);
                break;

            case TYPE_HBYD:
                startWebActivity(Urls.HBYD_URL);
                break;

            case TYPE_GSLK:
                startWebActivity(Urls.GSLK_URL);
                break;

            case TYPE_WZCX:
                startWebActivity(Urls.WZCX_URL);
                break;

            case TYPE_TQYB:
                startWebActivity(Urls.TQYB_URL);
                break;

            case TYPE_KDCX:
                startWebActivity(Urls.KDCX_URL);
                break;

            case TYPE_DSZB:
                startWebActivity(Urls.DSZB_URL);
                break;

            case TYPE_XXCX:
                startWebActivity(Urls.XXCX_URL);
                break;

            case TYPE_GJCX:
                startWebActivity(Urls.GJCX_URL);
                break;

            case TYPE_GSK:
                VideoPlayActivity.startActivity(ServiceMoreActivity.this);
//                startWebActivity(Urls.GSK_URL);
                break;

            case TYPE_TGB:
//                startWebActivity(Urls.TGB_URL);
                WebActivity.startActivity(ServiceMoreActivity.this,Urls.TGB_URL);
                break;

            case TYPE_SPDB:
                startWebActivity(Urls.TYPE_SPDB);
//                WebActivity.startActivity(ServiceMoreActivity.this,Urls.TYPE_SPDB);
                break;
        }
    }

    /**
     * 跳转到webactivity
     *
     * @param url
     */
    private void startWebActivity(String url) {
        TBXActivity.startActivity(this, url);
    }
}
