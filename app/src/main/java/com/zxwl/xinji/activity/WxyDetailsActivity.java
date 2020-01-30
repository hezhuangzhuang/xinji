package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.bean.response.WxyBean;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;

import org.greenrobot.eventbus.EventBus;

/**
 * 微心愿详情
 */
public class WxyDetailsActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;

    private TextView tvTitle;
    private TextView tvTime;
    private TextView tvWebsite;

    private TextView tvName;
    private TextView tvPhone;
    private TextView tvActivityTime;
    private TextView tvAddress;
    private ImageView tvClaimStatus;
    private LinearLayout llClaimInfo;
    private TextView tvClaimName;
    private TextView tvPartyBranch;
    private TextView tvClaimPhone;
    private WebView webView;
    private TextView tvClaim;

    private WxyBean wxyBean;

    public static final String WXY_BEAN = "WXY_BEAN";

    public static void startActivity(Context context, WxyBean wxyBean) {
        Intent intent = new Intent(context, WxyDetailsActivity.class);
        intent.putExtra(WXY_BEAN, wxyBean);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvWebsite = (TextView) findViewById(R.id.tv_website);

        tvName = (TextView) findViewById(R.id.tv_name);
        tvPhone = (TextView) findViewById(R.id.tv_phone);
        tvActivityTime = (TextView) findViewById(R.id.tv_activity_time);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        tvClaimStatus = (ImageView) findViewById(R.id.tv_claim_status);

        llClaimInfo = (LinearLayout) findViewById(R.id.ll_claim_info);

        tvClaimName = (TextView) findViewById(R.id.tv_claim_name);
        tvPartyBranch = (TextView) findViewById(R.id.tv_party_branch);
        tvClaimPhone = (TextView) findViewById(R.id.tv_claim_phone);
        webView = (WebView) findViewById(R.id.tv_content);

        tvClaim = (TextView) findViewById(R.id.tv_claim);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        tvTopTitle.setText("党群微心愿详情");

        wxyBean = (WxyBean) getIntent().getSerializableExtra(WXY_BEAN);

        tvTitle.setText(wxyBean.content);
        tvTime.setText(wxyBean.createtime.substring(0, 10));
        tvWebsite.setText(wxyBean.name);

        tvName.setText(wxyBean.name);
        tvPhone.setText(wxyBean.telephone);
        tvActivityTime.setText(wxyBean.tinytime.substring(0, 10));
        tvAddress.setText(wxyBean.unitname);

        setWebContent();

        if (!"1".equals(wxyBean.reviewState)) {
            tvClaim.setVisibility(View.GONE);
        }

        //已认领
        if (1 == wxyBean.claim) {
            setClaimInfo(wxyBean.claimName, wxyBean.claimTel);
        }
    }

    private void setClaimInfo(String name, String phone) {
        llClaimInfo.setVisibility(View.VISIBLE);
        tvClaimStatus.setVisibility(View.VISIBLE);
        tvClaimName.setText(name);
        tvClaimPhone.setText(phone);
        tvClaim.setVisibility(View.GONE);
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(this);
        tvClaim.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wxy_details;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_operate:
                finish();
                break;

            case R.id.tv_claim:
                ClaimWxyActivity.startActivity(WxyDetailsActivity.this, wxyBean.id);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        webView.clearCache(true);
        webView = null;

        if (isRefresh) {
            EventBus.getDefault().post(new EventMessage(Messages.REFRESH_RECYCLER, ""));
        }
        super.onDestroy();
    }

    /**
     * 设置webview的内容
     */
    private void setWebContent() {
        StringBuilder sb = new StringBuilder();
        //TODO:设置传递过来的参数
        sb.append(getHtmlData(wxyBean.wishinfo));
        webView.loadDataWithBaseURL(null, sb.toString(), "text/html", "utf-8", null);
    }

    private String getHtmlData(String bodyHTML) {
        String head = "<head>"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> "
                + "<style>img{max-width: 100%; width:auto; height:auto;}</style>"
                + "</head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }

    public static final int TYPE_CLAIM = 0x111;

    public static final String NAME = "NAME";

    public static final String PHONE = "PHONE";

    //是否刷新界面
    private boolean isRefresh = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TYPE_CLAIM:
                    isRefresh = true;
                    String name = data.getStringExtra(NAME);
                    String phone = data.getStringExtra(PHONE);

                    setClaimInfo(name, phone);
                    break;
            }
        }
    }

}
