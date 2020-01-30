package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.commonlibrary.utils.DialogUtils;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.bean.response.UrgeDetail;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.utils.PreferenceUtil;
import com.zxwl.xinji.utils.StringUtils;
import com.zxwl.xinji.widget.CustomInputDialog;

import org.greenrobot.eventbus.EventBus;

/**
 * 事件督办详情界面
 */
public class EventSupervisorActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;

    private TextView tvTilte;
    private TextView tvTime;
    private TextView tvWebsite;
    private TextView tvEndTime;
    private TextView tvContent;
    private RelativeLayout rlResult;
    private TextView tvResultTime;
    private TextView tvResultWebsite;
    private TextView tvResultContent;
    private Button btReply;
    private LoginBean.AccountBean accountBean;

    public static final String SJDB_BEAN_ID = "SJDB_BEAN_ID";//事件督办的id

    private int id;

    public static void startActivity(Context context, int id) {
        Intent intent = new Intent(context, EventSupervisorActivity.class);
        intent.putExtra(SJDB_BEAN_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);

        tvTilte = (TextView) findViewById(R.id.tv_tilte);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvWebsite = (TextView) findViewById(R.id.tv_website);
        tvEndTime = (TextView) findViewById(R.id.tv_end_time);
        tvContent = (TextView) findViewById(R.id.tv_content);
        rlResult = (RelativeLayout) findViewById(R.id.rl_result);
        tvResultTime = (TextView) findViewById(R.id.tv_result_time);
        tvResultWebsite = (TextView) findViewById(R.id.tv_result_website);
        tvResultContent = (TextView) findViewById(R.id.tv_result_content);
        btReply = (Button) findViewById(R.id.bt_reply);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        tvTopTitle.setText("事件督办详情");

        accountBean = PreferenceUtil.getUserInfo(this);

        id = getIntent().getIntExtra(SJDB_BEAN_ID, -1);

        querySjdbDetail();
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(this);
        btReply.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_event_supervisor;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_operate:
                finish();
                break;

            case R.id.bt_reply:
                showReplyDialog();
                break;
        }
    }

    /**
     * 查询时间督办的详情
     */
    private void querySjdbDetail() {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryUrgeDetailById(id)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<UrgeDetail>() {
                    @Override
                    public void onSuccess(UrgeDetail urgeDetail) {
                        if (BaseData.SUCCESS.equals(urgeDetail.result)) {
                            UrgeDetail.DataBean dataBean = urgeDetail.data;

                            tvTilte.setText(dataBean.title);
                            tvTime.setText(DateUtil.longToString(dataBean.sendDate, DateUtil.FORMAT_DATE_TIME));
                            tvWebsite.setText("发布方:" + dataBean.senderName);

                            tvContent.setText(dataBean.content);

                            tvEndTime.setText("时间:" + DateUtil.longToString(dataBean.beginDate, DateUtil.FORMAT_DATE_TIME_SECOND) + " 至 " + DateUtil.longToString(dataBean.endDate, DateUtil.FORMAT_DATE_TIME_SECOND));

                            if (TextUtils.isEmpty(dataBean.dealContent)) {
                                rlResult.setVisibility(View.GONE);
                                btReply.setVisibility(View.VISIBLE);
                            } else {
                                rlResult.setVisibility(View.VISIBLE);

                                tvResultTime.setText(DateUtil.longToString(dataBean.dealDate, DateUtil.FORMAT_DATE_TIME));
                                tvResultWebsite.setText(dataBean.receiveName);
                                tvResultContent.setText(dataBean.dealContent);

                                btReply.setVisibility(View.GONE);
                            }
                        } else {
                            ToastHelper.showShort(urgeDetail.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        ToastHelper.showShort("onError:" + responeThrowable.getCause().toString());
                    }
                });
    }

    private CustomInputDialog customInputDialog;

    private void showReplyDialog() {
        customInputDialog = new CustomInputDialog(this, R.style.inputDialogStyle, "请输入回复内容");
        customInputDialog.setClickListener(new CustomInputDialog.onSendClickListener() {
            @Override
            public void sendComment(String comment) {
                replyUrge(comment);
            }
        });
        customInputDialog.show();
    }

    /**
     * 回复督办事件
     */
    private void replyUrge(String comment) {
        DialogUtils.showProgressDialog(this, "正在回复...");

        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .replyUrge(id, Integer.valueOf(accountBean.id), StringUtils.encoder(comment))
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        DialogUtils.dismissProgressDialog();
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            ToastHelper.showShort(baseData.message);
                            customInputDialog.dismiss();

                            btReply.setVisibility(View.GONE);

                            rlResult.setVisibility(View.VISIBLE);
                            tvResultTime.setText(DateUtil.longToString(System.currentTimeMillis(), DateUtil.FORMAT_DATE_TIME));
                            tvResultWebsite.setText(accountBean.name);
                            tvResultContent.setText(comment);
                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        ToastHelper.showShort("onError" + responeThrowable.getCause().getMessage());
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new EventMessage(Messages.REFRESH_RECYCLER, ""));
    }
}
