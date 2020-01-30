package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.jaeger.library.StatusBarUtil;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.commonlibrary.utils.DialogUtils;
import com.zxwl.commonlibrary.utils.KeyBoardUtil;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.widget.ContainsEmojiEditText;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import okhttp3.RequestBody;

/**
 * 添加村民事务代表
 */
public class AddCmswdbActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvRightOperate;

    private TextView tvStartTime;
    private ContainsEmojiEditText etApply;
    private ContainsEmojiEditText etReceiver;
    private ContainsEmojiEditText etOpinion;
    private TextView tvCloseTime;
    private ContainsEmojiEditText etResult;
    private ContainsEmojiEditText etContent;

    //选择受理时间
    private boolean isSelectStart = false;

    //申请人
    private String applyName;
    //受理人
    private String receiverName;
    //答复意见
    private String opinion;
    //答复意见
    private String result;
    //内容
    private String content;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AddCmswdbActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvRightOperate = (TextView) findViewById(R.id.tv_right_operate);
        tvStartTime = (TextView) findViewById(R.id.tv_start_time);
        etApply = (ContainsEmojiEditText) findViewById(R.id.et_apply);
        etReceiver = (ContainsEmojiEditText) findViewById(R.id.et_receiver);
        etOpinion = (ContainsEmojiEditText) findViewById(R.id.et_opinion);
        tvCloseTime = (TextView) findViewById(R.id.tv_close_time);
        etResult = (ContainsEmojiEditText) findViewById(R.id.et_result);
        etContent = (ContainsEmojiEditText) findViewById(R.id.et_content);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);
        tvTopTitle.setText("村民事务代办");

        tvRightOperate.setText("发布");
        tvRightOperate.setVisibility(View.VISIBLE);

        startTime = DateUtil.getCurrentTime(DateUtil.FORMAT_DATE_TIME);
        tvStartTime.setText(startTime);
        tvCloseTime.setText("请输入办结时间");
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(this);
        tvRightOperate.setOnClickListener(this);
        tvStartTime.setOnClickListener(this);
        tvCloseTime.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_cmswdb;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_operate:
                finish();
                break;

            case R.id.tv_right_operate:
                long startLong = DateUtil.stringToLong(startTime, DateUtil.FORMAT_DATE_TIME);
                long currentLong = System.currentTimeMillis();

                if (startLong > currentLong) {
                    ToastHelper.showShort("受理时间不能大于当前时间");
                    return;
                }

                if (!TextUtils.isEmpty(closeTime)) {
                    long endLong = DateUtil.stringToLong(closeTime, DateUtil.FORMAT_DATE_TIME);

                    if (startLong > endLong) {
                        ToastHelper.showShort("受理时间不能大于办结时间");
                        return;
                    }
                }

                applyName = etApply.getText().toString().trim();
                if (TextUtils.isEmpty(applyName)) {
                    ToastHelper.showShort("请输入申请人");
                    return;
                }

                receiverName = etReceiver.getText().toString().trim();
                if (TextUtils.isEmpty(receiverName)) {
                    ToastHelper.showShort("请输入受理人");
                    return;
                }

//                opinion = etOpinion.getText().toString().trim();
//                if (TextUtils.isEmpty(opinion)) {
//                    ToastHelper.showShort("请输入答复意见");
//                    return;
//                }

                result = etResult.getText().toString().trim();
//                if (TextUtils.isEmpty(result)) {
//                    ToastHelper.showShort("请输入办结结果");
//                    return;
//                }

                content = etContent.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastHelper.showShort("请输入事项内容");
                    return;
                }

                addCmswdbRequest();
                break;

            case R.id.tv_start_time:
                isSelectStart = true;

                showSelectTime();
                break;

            case R.id.tv_close_time:
                isSelectStart = false;

                showSelectTime();
                break;
        }
    }

    /**
     * 添加村民事务代表
     */
    private void addCmswdbRequest() {
        DialogUtils.showProgressDialog(this,"正在保存...");
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .addCmswdb(
                        getRequestBody()
                )
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        DialogUtils.dismissProgressDialog();
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            ToastHelper.showShort("上传成功");
                            EventBus.getDefault().post(new EventMessage(Messages.REFRESH_RECYCLER, ""));
                            finish();
                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        DialogUtils.dismissProgressDialog();
                        ToastHelper.showShort("onError:" + responeThrowable.getCause().getMessage());
                    }
                });
    }

    public void showSelectTime() {
        KeyBoardUtil.closeKeybord(etApply, getApplication());

        if (null!=pvCustomLunar&&pvCustomLunar.isShowing()) {
            return;
        }
        initLunarPicker();
        pvCustomLunar.show();
    }

    private TimePickerView pvCustomLunar;
    private String startTime;
    private String closeTime;

    /**
     * 农历时间已扩展至 ： 1900 - 2100年
     */
    private void initLunarPicker() {
        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        selectedDate.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH), selectedDate.get(Calendar.HOUR_OF_DAY), selectedDate.get(Calendar.MINUTE));

        Calendar startDate = Calendar.getInstance();
        startDate.set(1900, 1, 23);

        Calendar endDate = Calendar.getInstance();
        endDate.set(2100, 1, 23);

        //时间选择器 ，自定义布局
        pvCustomLunar = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                if (isSelectStart) {
                    startTime = DateUtil.dateToString(date, DateUtil.FORMAT_DATE_TIME);
                    tvStartTime.setText(startTime);
                } else {
                    closeTime = DateUtil.dateToString(date, DateUtil.FORMAT_DATE_TIME);
                    tvCloseTime.setText(closeTime);
                }
            }
        })
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLayoutRes(R.layout.pickerview_custom_lunar, new CustomListener() {
                    @Override
                    public void customLayout(final View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        TextView tvCancle = (TextView) v.findViewById(R.id.tv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomLunar.returnData();
                                pvCustomLunar.dismiss();
                            }
                        });
                        tvCancle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomLunar.dismiss();
                            }
                        });
                    }
                })
                .setType(new boolean[]{true, true, true, true, true, false})
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(Color.RED)
                .build();
    }


    /**
     * 获取请求体
     *
     * @param imgUrls
     * @return
     */
    @NonNull
    private RequestBody getRequestBody() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("receiveTime", startTime);
            jsonObject.put("applicant", applyName);
            jsonObject.put("content", content);
            jsonObject.put("assignee", receiverName);
            jsonObject.put("replyOpinion", opinion);
            jsonObject.put("completeTime", closeTime);
            jsonObject.put("result", result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return RequestBody.create(Urls.MEDIA_TYPE, jsonObject.toString());
    }

}
