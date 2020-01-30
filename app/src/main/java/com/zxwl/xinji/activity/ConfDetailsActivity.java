package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.commonlibrary.utils.WeekDayUtil;
import com.zxwl.network.bean.response.ThemePartyBean;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;

/**
 * 三会一课详情
 */
public class ConfDetailsActivity extends BaseActivity {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvTilte;
    private TextView tvTime;
    private TextView tvWebsite;
    private TextView tvActivityTime;
    private TextView tvActivityLocal;
    private TextView tvHost;
    private TextView tvPersonnel;
    private WebView webview;

    private ThemePartyBean themePartyBean;

    public static final String CONF_BEAN = "CONF_BEAN";

    public static void startActivity(Context context, ThemePartyBean themePartyBean) {
        Intent intent = new Intent(context, ConfDetailsActivity.class);
        intent.putExtra(CONF_BEAN, themePartyBean);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvTilte = (TextView) findViewById(R.id.tv_tilte);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvWebsite = (TextView) findViewById(R.id.tv_website);
        tvActivityTime = (TextView) findViewById(R.id.tv_activity_time);
        tvActivityLocal = (TextView) findViewById(R.id.tv_activity_local);
        tvHost = (TextView) findViewById(R.id.tv_host);
        tvPersonnel = (TextView) findViewById(R.id.tv_personnel);
        webview = (WebView) findViewById(R.id.webview);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        themePartyBean = (ThemePartyBean) getIntent().getSerializableExtra(CONF_BEAN);

        tvTopTitle.setText("三会一课详情");

        tvTilte.setText(themePartyBean.title);

        tvWebsite.setText(themePartyBean.creatorName);

        tvTime.setText(DateUtil.longToString(themePartyBean.createDate, DateUtil.FORMAT_DATE));

        tvActivityTime.setText(DateUtil.longToString(themePartyBean.activityDate, DateUtil.FORMAT_DATE_CHINA) + " (" + WeekDayUtil.getWeek(DateUtil.longToString(themePartyBean.activityDate, DateUtil.FORMAT_DATE), WeekDayUtil.TYPE_XQ) + ")");

        tvActivityLocal.setText(themePartyBean.address);

        tvHost.setText(TextUtils.isEmpty(themePartyBean.host) ? "暂无" : themePartyBean.host);

        String attendNames = themePartyBean.attendNames.replaceAll("!\\*!", ",");

        tvPersonnel.setText(TextUtils.isEmpty(attendNames) ? "暂无" : attendNames);

        setWebContent();
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
        return R.layout.activity_conf_details;
    }

    /**
     * 设置webview的内容
     */
    private void setWebContent() {
        StringBuilder sb = new StringBuilder();
        //TODO:设置传递过来的参数
        sb.append(getHtmlData(themePartyBean.context));
        webview.loadDataWithBaseURL(null, sb.toString(), "text/html", "utf-8", null);
    }

    private String getHtmlData(String bodyHTML) {
        String head = "<head>"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> "
                + "<style>img{max-width: 100%; width:auto; height:auto;}</style>"
                + "</head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }
}
