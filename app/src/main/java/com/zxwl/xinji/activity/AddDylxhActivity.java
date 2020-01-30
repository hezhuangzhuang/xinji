package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.utils.Constant;
import com.zxwl.xinji.utils.PreferenceUtil;
import com.zxwl.xinji.utils.StringUtils;
import com.zxwl.xinji.widget.ContainsEmojiEditText;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 添加党员联系户
 */
public class AddDylxhActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvRightOperate;
    private ContainsEmojiEditText etPartyName;
    private ContainsEmojiEditText etPartyPhone;
    private ContainsEmojiEditText etContactName;
    private ContainsEmojiEditText etContactPhone;
    private ContainsEmojiEditText etContactAddress;
    private EditText etContactUnit;

    private String partyName;
    private String partyPhone;
    private String contactName;
    private String contactPhone;
    private String contactAddress;
    private String contactUnit;

    private LoginBean.AccountBean accountBean;

    private TextView tvTime;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AddDylxhActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvRightOperate = (TextView) findViewById(R.id.tv_right_operate);
        etPartyName = (ContainsEmojiEditText) findViewById(R.id.et_party_name);
        etPartyPhone = (ContainsEmojiEditText) findViewById(R.id.et_party_phone);
        etContactName = (ContainsEmojiEditText) findViewById(R.id.et_contact_name);
        etContactPhone = (ContainsEmojiEditText) findViewById(R.id.et_contact_phone);
        etContactAddress = (ContainsEmojiEditText) findViewById(R.id.et_contact_address);
        etContactUnit = (EditText) findViewById(R.id.et_contact_unit);
        tvTime = (TextView) findViewById(R.id.tv_time);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        tvTopTitle.setText("志愿服务");
        tvRightOperate.setText("发布");
        tvRightOperate.setVisibility(View.VISIBLE);

        time = DateUtil.getCurrentTime(DateUtil.FORMAT_DATE);
        tvTime.setText(DateUtil.getCurrentTime(DateUtil.FORMAT_DATE));

        accountBean = PreferenceUtil.getUserInfo(this);
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(this);
        tvRightOperate.setOnClickListener(this);
        tvTime.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_dylxh;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_operate:
                finish();
                break;

            case R.id.tv_right_operate:
                if (checkText()) {
                    return;
                }
                addDylxh();
                break;

            case R.id.tv_time:
                KeyBoardUtil.closeKeybord(etContactAddress, getApplication());
                initLunarPicker();
                pvCustomLunar.show();
                break;
        }
    }

    /**
     * 判断文本是否有输入
     *
     * @return
     */
    private boolean checkText() {
        partyName = etPartyName.getText().toString().trim();
        if (TextUtils.isEmpty(partyName)) {
            ToastHelper.showShort("党员姓名不能为空");
            return true;
        }

        partyPhone = etPartyPhone.getText().toString().trim();
        if (TextUtils.isEmpty(partyPhone) || partyPhone.length() != Constant.PHONE_LENGHT) {
            ToastHelper.showShort("党员联系电话格式不正确");
            return true;
        }

        contactName = etContactName.getText().toString().trim();
        if (TextUtils.isEmpty(contactName)) {
            ToastHelper.showShort("联系户姓名不能为空");
            return true;
        }

        contactPhone = etContactPhone.getText().toString().trim();
        if (TextUtils.isEmpty(contactPhone) || contactPhone.length() != Constant.PHONE_LENGHT) {
            ToastHelper.showShort("联系户电话格式不正确");
            return true;
        }

        contactAddress = etContactAddress.getText().toString().trim();
        if (TextUtils.isEmpty(contactAddress)) {
            ToastHelper.showShort("联系户住址不能为空");
            return true;
        }

        contactUnit = etContactUnit.getText().toString().trim();
        if (TextUtils.isEmpty(contactUnit)) {
            ToastHelper.showShort("联系户工作单位不能为空");
            return true;
        }

        String startTime = tvTime.getText().toString().trim();
        long startTimeLong = DateUtil.stringToLong(startTime, DateUtil.FORMAT_DATE);

        long currentTime = DateUtil.stringToLong(DateUtil.getCurrentTime(DateUtil.FORMAT_DATE), DateUtil.FORMAT_DATE);

        if (startTimeLong - currentTime > 0) {
            ToastHelper.showShort("时间不能大于当前时间");
            return true;
        }

        return false;
    }

    /**
     * 添加党员联系户
     */
    private void addDylxh() {
        DialogUtils.showProgressDialog(this, "正在添加...");
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .addDylxh(
                        StringUtils.encoder(partyName),
                        partyPhone,
                        StringUtils.encoder(contactName),
                        contactPhone,
                        StringUtils.encoder(contactAddress),
                        StringUtils.encoder(contactUnit),
                        String.valueOf(accountBean.unitId),
                        time,
                        accountBean.id
                )
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        DialogUtils.dismissProgressDialog();
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            ToastHelper.showShort("添加成功");
                            EventBus.getDefault().post(new EventMessage(Messages.REFRESH_RECYCLER, ""));
                            finish();
                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        DialogUtils.dismissProgressDialog();
                        ToastHelper.showShort("请求error:" + responeThrowable.getCause().toString());
                    }
                });
    }

    private TimePickerView pvCustomLunar;
    private String time;

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
                time = getTime(date);
                tvTime.setText(time);
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
                .setType(new boolean[]{true, true, true, false, false, false})
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(Color.RED)
                .build();
    }

    /**
     * 可根据需要自行截取数据显示
     * @param date
     * @return
     */
    private String getTime(Date date) {
        Log.d("getTime()", "choice date millis: " + date.getTime());
        SimpleDateFormat format = new SimpleDateFormat(DateUtil.FORMAT_DATE);
        return format.format(date);
    }

}
