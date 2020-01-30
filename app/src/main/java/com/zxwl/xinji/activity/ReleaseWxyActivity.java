package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
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
import com.zxwl.commonlibrary.utils.DisplayUtil;
import com.zxwl.commonlibrary.utils.KeyBoardUtil;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.bean.response.DepartmentBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.utils.StringUtils;
import com.zxwl.xinji.widget.ContainsEmojiEditText;
import com.zxwl.xinji.widget.ScreenCityPopupWindow;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 发布微心愿
 */
public class ReleaseWxyActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvRightOperate;

    private ContainsEmojiEditText etName;
    private EditText etPhone;
    private TextView tvTime;
    private TextView tvAddress;
    private ContainsEmojiEditText etContent;
    private ContainsEmojiEditText etWishinfo;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ReleaseWxyActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvRightOperate = (TextView) findViewById(R.id.tv_right_operate);
        etName = (ContainsEmojiEditText) findViewById(R.id.et_name);
        etPhone = (EditText) findViewById(R.id.et_phone);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        etContent = (ContainsEmojiEditText) findViewById(R.id.et_content);
        etWishinfo = (ContainsEmojiEditText) findViewById(R.id.et_wishinfo);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

//        accountBean = PreferenceUtil.getUserInfo(this);

        tvTopTitle.setText("微心愿");

        tvRightOperate.setText("发布");
        tvRightOperate.setVisibility(View.VISIBLE);

//        if (3 == accountBean.level) {
//            tvAddress.setText(accountBean.flag.substring(3));
//            townshipId = Integer.valueOf(accountBean.townId);
//            villageId = Integer.valueOf(accountBean.villageId);
//        }
        tvTime.setText(DateUtil.longToString(System.currentTimeMillis(), DateUtil.FORMAT_DATE));
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(this);
        tvRightOperate.setOnClickListener(this);
        tvTime.setOnClickListener(this);
        tvAddress.setOnClickListener(this);

        etContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //canScrollVertically()方法为判断指定方向上是否可以滚动,参数为正数或负数,负数检查向上是否可以滚动,正数为检查向下是否可以滚动
                if (etContent.canScrollVertically(1) || etContent.canScrollVertically(-1)) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);//requestDisallowInterceptTouchEvent();要求父类布局不在拦截触摸事件
                    if (event.getAction() == MotionEvent.ACTION_UP) { //判断是否松开
                        v.getParent().requestDisallowInterceptTouchEvent(false); //requestDisallowInterceptTouchEvent();让父类布局继续拦截触摸事件
                    }
                }
                return false;
            }
        });

        etWishinfo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //canScrollVertically()方法为判断指定方向上是否可以滚动,参数为正数或负数,负数检查向上是否可以滚动,正数为检查向下是否可以滚动
                if (etContent.canScrollVertically(1) || etContent.canScrollVertically(-1)) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);//requestDisallowInterceptTouchEvent();要求父类布局不在拦截触摸事件
                    if (event.getAction() == MotionEvent.ACTION_UP) { //判断是否松开
                        v.getParent().requestDisallowInterceptTouchEvent(false); //requestDisallowInterceptTouchEvent();让父类布局继续拦截触摸事件
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_release_wxy;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_operate:
                finish();
                break;

            case R.id.tv_right_operate:
                if (checkContent()) {
                    return;
                }
                addWxyRequest();
                break;

            case R.id.tv_time:
                KeyBoardUtil.closeKeybord(etContent, getApplication());
                initLunarPicker();
                pvCustomLunar.show();
                break;

            case R.id.tv_address:
                KeyBoardUtil.closeKeybord(etContent, getApplication());

                //对话框已经显示
                if (null != cityDialog && cityDialog.isShowing()) {
                    cityDialog.dismiss();
                } else {
                    if (null != cityDialog) {
                        showCityDialog();
                    } else {
                        getDepartmentList(1);
                    }
                }
                break;
        }
    }

    private String name;
    private String phone;
    private String startTime;
    private String address;
    private String content;
    private String wishinfo;

    private boolean checkContent() {
        name = etName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ToastHelper.showShort("姓名不能为空");
            return true;
        }

        phone = etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastHelper.showShort("联系方式不能为空");
            return true;
        }

        startTime = tvTime.getText().toString().trim();
        long startTimeLong = DateUtil.stringToLong(startTime, DateUtil.FORMAT_DATE);
        long currentTime = DateUtil.stringToLong(DateUtil.getCurrentTime(DateUtil.FORMAT_DATE), DateUtil.FORMAT_DATE);

        if (startTimeLong - currentTime > 0) {
            ToastHelper.showShort("时间不能大于当前时间");
            return true;
        }

        address = tvAddress.getText().toString().trim();
        if (TextUtils.isEmpty(address)) {
            ToastHelper.showShort("地区不能为空");
            return true;
        }

        content = etContent.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastHelper.showShort("心愿内容不能为空");
            return true;
        }

        wishinfo = etWishinfo.getText().toString().trim();
        if (TextUtils.isEmpty(wishinfo)) {
            ToastHelper.showShort("具体诉求不能为空");
            return true;
        }
        return false;
    }

    private void addWxyRequest() {
        String startTime = tvTime.getText().toString().trim();
        long startTimeLong = DateUtil.stringToLong(startTime, DateUtil.FORMAT_DATE);

        long currentTime = DateUtil.stringToLong(DateUtil.getCurrentTime(DateUtil.FORMAT_DATE), DateUtil.FORMAT_DATE);

        if (startTimeLong - currentTime > 0) {
            ToastHelper.showShort("时间不能大于当前时间");
            return;
        }

        DialogUtils.showProgressDialog(this, "正在上传...");

        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .addWxy(
                        StringUtils.encoder(name.replaceAll("\n", "<br>")),
                        StringUtils.encoder(content),
                        StringUtils.encoder(wishinfo.replaceAll("\n", "<br>")),
                        phone,
                        startTime,
                        townshipId,
                        villageId
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

                            EventBus.getDefault().post(new EventMessage(Messages.REFRESH_WXY, ""));

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
                time = DateUtil.dateToString(date, DateUtil.FORMAT_DATE);
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


    /*********************************************地址的筛选框---start******************************/
    //乡镇ID
    private int townshipId;
    //街村ID
    private int villageId;

    //当前的单位id
    public int currentUnitId;

    //乡镇名称
    private String townshipName;
    //街村名称
    private String villageName;

    //左边全辛集市的id
    private int LEFT_ALL_ID = 0x1111;

    //右边全部的id
    private int RIGHT_ALL_ID = 0x1112;

    private List<DepartmentBean> leftDepartments;

    /**
     * 获取组织信息
     */
    private void getDepartmentList(int currentUnitId) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryDepartment(currentUnitId)
                .compose(this.bindToLifecycle())
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<DepartmentBean>>() {
                    @Override
                    public void onSuccess(BaseData<DepartmentBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<DepartmentBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                DepartmentBean departmentBean = null;

                                //如果左边的列表等于null，代表第一次请求
                                if (null == leftDepartments) {
                                    leftDepartments = dataList;

                                    //添加辛集市在左边
                                    //departmentBean = new DepartmentBean();
                                    //departmentBean.id = 1;
                                    //departmentBean.departmentName = "辛集市";
                                    //leftDepartments.add(0, departmentBean);

                                    //如果是用户bean为空
                                    showScreenDialog(leftDepartments, new ArrayList<>());
                                } else {
                                    //代表点击的辛集市
                                    if (1 == townshipId) {
                                        //添加辛集市在左边
                                        departmentBean = new DepartmentBean();
                                        departmentBean.id = 1;
                                        departmentBean.departmentName = "辛集市";
                                        dataList.add(0, departmentBean);
                                    }
                                    cityDialog.setRightNewData(dataList);
                                }
                            } else {
                                if (null != leftDepartments) {
                                    cityDialog.setRightNewData(new ArrayList<>());
                                }
                            }
                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    private ScreenCityPopupWindow cityDialog;

    /**
     * 显示筛选对话框
     */
    private void showScreenDialog(List<DepartmentBean> leftData,
                                  List<DepartmentBean> rightData) {
        if (null == cityDialog) {
            cityDialog = new ScreenCityPopupWindow(
                    this,
                    DisplayUtil.getScreenWidth(),
                    DisplayUtil.getScreenHeight() * 3 / 5,
                    leftData,
                    rightData,
                    true
            );

            cityDialog.setOnScreenClick(new ScreenCityPopupWindow.onScreenClick() {
                @Override
                public void onLeftClick(int cityId, String departmentName) {
                    townshipId = cityId;
                    townshipName = departmentName;

                    getDepartmentList(cityId);
                }

                @Override
                public void onRightClick(int cityId, String departmentName, String terUri) {
                    villageId = cityId;
                    villageName = departmentName;

                    tvAddress.setText(townshipName + villageName);
                    cityDialog.dismiss();
                }
            });
        }
        showCityDialog();
    }

    private void showCityDialog() {
        cityDialog.setAlignBackground(false);
        cityDialog.setPopupGravity(Gravity.BOTTOM);
        cityDialog.showPopupWindow();
    }
    /*********************************************地址的筛选框---end******************************/


}
