package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.jaeger.library.StatusBarUtil;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.commonlibrary.utils.DialogUtils;
import com.zxwl.commonlibrary.utils.DisplayUtil;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.commonlibrary.widget.NoScrollViewPager;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.bean.response.DepartmentBean;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.bean.response.WxyCountBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.MyPagerAdapter;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.fragment.StatisticsFragment;
import com.zxwl.xinji.utils.Constant;
import com.zxwl.xinji.utils.PreferenceUtil;
import com.zxwl.xinji.widget.ScreenCityPopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 带筛选条件的列表
 */
public class ScreenViewPagerActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvRightOperate;
    private ImageView ivRightOperate;
    private RelativeLayout rlTopTitle;

    private RelativeLayout rlScreen;
    private TextView tvScreen;
    private TextView tvCity;
    private TextView tvTotal;
    private SlidingTabLayout tbLayout;
    private NoScrollViewPager vpContent;

    private List<String> mTitles;

    private List<Fragment> mFragments = new ArrayList<>();

    //当前下标
    private int currentIndex = 0;

    private LoginBean.AccountBean accountBean;

    private ConstraintLayout clWxyCount;
    private TextView tvAll;
    private TextView tvNoClaim;
    private TextView tvIsClaim;

    public static final String TYPE_SJTJ = "数据统计";
    public static final String TYPE_WXY = "党群微心愿";

    public static final String TITLE = "TITLE";

    private String title;

    /**
     * 排序
     * 默认降序
     * 降序:1
     * 升序：2
     */
    public int total = 1;

    //是否刷新微心愿
    public boolean refreshWxy = false;

    public static void startActivity(Context context, String title) {
        Intent intent = new Intent(context, ScreenViewPagerActivity.class);
        intent.putExtra(TITLE, title);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvRightOperate = (TextView) findViewById(R.id.tv_right_operate);
        rlScreen = (RelativeLayout) findViewById(R.id.rl_screen);
        tvScreen = (TextView) findViewById(R.id.tv_screen);
        tvCity = (TextView) findViewById(R.id.tv_city);
        tbLayout = (SlidingTabLayout) findViewById(R.id.tb_layout);
        vpContent = (NoScrollViewPager) findViewById(R.id.vp_content);
        ivRightOperate = (ImageView) findViewById(R.id.iv_right_operate);
        rlTopTitle = (RelativeLayout) findViewById(R.id.rl_top_title);

        clWxyCount = (ConstraintLayout) findViewById(R.id.cl_wxy_count);
        tvAll = (TextView) findViewById(R.id.tv_all);
        tvNoClaim = (TextView) findViewById(R.id.tv_no_claim);
        tvIsClaim = (TextView) findViewById(R.id.tv_is_claim);

        tvTotal = (TextView) findViewById(R.id.tv_total);

        rlSelect = (RelativeLayout) findViewById(R.id.rl_select);

        tvStartTime = (TextView) findViewById(R.id.tv_start_time);
        tvEndTime = (TextView) findViewById(R.id.tv_end_time);
        tvCancle = (TextView) findViewById(R.id.tv_cancle);
        tvOk = (TextView) findViewById(R.id.tv_ok);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        title = getIntent().getStringExtra(TITLE);

        tvTopTitle.setText(title);

        if (TYPE_SJTJ.equals(title)) {
            ivRightOperate.setVisibility(View.VISIBLE);
            ivRightOperate.setImageResource(R.mipmap.ic_select_time);

            accountBean = PreferenceUtil.getUserInfo(this);

            tvScreen.setText(accountBean.flag);

            currentUnitId = accountBean.unitId;

            initSjtjTab();

            tvTotal.setVisibility(View.VISIBLE);
        } else {
            register();

            ivRightOperate.setImageResource(R.mipmap.ic_wxy_add);

            tvRightOperate.setText("添加");
            tvRightOperate.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.mipmap.ic_wxy_add, 0);
            tvRightOperate.setVisibility(View.VISIBLE);

            tbLayout.setTabSpaceEqual(true);

            tvCity.setVisibility(View.VISIBLE);

            if (isLogin()) {
                accountBean = PreferenceUtil.getUserInfo(this);

                tvCity.setText(accountBean.flag);
            }

            initWxyTab();

            getWxyCount();
        }
    }

    private void getWxyCount() {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryWxyCount()
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<WxyCountBean>() {
                    @Override
                    public void onSuccess(WxyCountBean baseData) {
                        DialogUtils.dismissProgressDialog();
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            clWxyCount.setVisibility(View.VISIBLE);
                            tvAll.setText(baseData.allSum);
                            tvIsClaim.setText(baseData.isClaim);
                            tvNoClaim.setText(baseData.noClaim);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        DialogUtils.dismissProgressDialog();
                        if (responeThrowable.getCause().getMessage().contains("Failed to connect to")) {
                            ToastHelper.showShort("请检查您的网络");
                        } else {
                            ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                        }
                    }
                });
    }

    private boolean isLogin() {
        return PreferenceUtil.getBoolean(Constant.AUTO_LOGIN, false);
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(this);
        tvScreen.setOnClickListener(this);
        tvRightOperate.setOnClickListener(this);
        tvTotal.setOnClickListener(this);

        tvStartTime.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);
        tvCancle.setOnClickListener(this);
        tvOk.setOnClickListener(this);

        ivRightOperate.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_screen_view_pager;
    }

    /**
     * 初始化数据统计tab
     */
    private void initSjtjTab() {
        mTitles = new ArrayList<>();
        mTitles.add(StatisticsFragment.TYPE_XXJY);
        mTitles.add(StatisticsFragment.TYPE_SHYK);
        mTitles.add(StatisticsFragment.TYPE_ZZSH);
        mTitles.add(StatisticsFragment.TYPE_ZYFW);

        for (int i = 0; i < mTitles.size(); i++) {
            mFragments.add(StatisticsFragment.newInstance(mTitles.get(i), i));
        }
        initViewPager();
    }

    /**
     * 初始化微心愿
     */
    private void initWxyTab() {
        if (isLogin()) {
            accountBean = PreferenceUtil.getUserInfo(this);
            currentUnitId = accountBean.unitId;
        } else {
            currentUnitId = 1;
        }

        mTitles = new ArrayList<>();
        mTitles.add(StatisticsFragment.TYPE_NO_CLAIM);
        mTitles.add(StatisticsFragment.TYPE_IS_CLAIM);

        for (int i = 0; i < mTitles.size(); i++) {
            mFragments.add(StatisticsFragment.newInstance(mTitles.get(i), i));
        }

        initViewPager();
    }

    private void initViewPager() {
        MyPagerAdapter mAdapter = new MyPagerAdapter(getSupportFragmentManager(), mFragments, mTitles);
        vpContent.setAdapter(mAdapter);
        tbLayout.setViewPager(vpContent);

        vpContent.setCurrentItem(currentIndex);
        vpContent.setOffscreenPageLimit(mTitles.size());

        vpContent.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tbLayout.setCurrentTab(currentIndex);
        tbLayout.onPageSelected(currentIndex);

        tbLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                currentIndex = position;
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_operate:
                finish();
                break;

            case R.id.tv_screen:
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

            case R.id.tv_right_operate:
                if (TYPE_WXY.equals(title)) {
                    ReleaseWxyActivity.startActivity(this);
                } else {
                    if (!showSelect) {
                        rlSelect.setVisibility(View.VISIBLE);
                        showSelect = true;
                    } else {
                        rlSelect.setVisibility(View.GONE);
                        showSelect = false;
                    }
                }
                break;

            case R.id.iv_right_operate:
                if (!showSelect) {
                    rlSelect.setVisibility(View.VISIBLE);
                    showSelect = true;
                } else {
                    rlSelect.setVisibility(View.GONE);
                    showSelect = false;
                }
                break;

            case R.id.tv_total:
                if (total == 1) {
                    //升序
                    total = 2;
                } else {
                    //降序
                    total = 1;
                }
                tvTotal.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 1 == total ? R.mipmap.ic_order_down : R.mipmap.ic_order_up, 0);
                EventBus.getDefault().post(new EventMessage(Messages.REFRESH_TOTAL, total));
                break;

            case R.id.tv_start_time:
                showLunarPicker(true);
                break;

            case R.id.tv_end_time:
                showLunarPicker(false);
                break;

            case R.id.tv_cancle:
                showSelect = false;
                startTime = "";
                endTime = "";
                tvStartTime.setText("请选择开始时间");
                tvEndTime.setText("请选择结束时间");
                rlSelect.setVisibility(View.GONE);
                break;

            case R.id.tv_ok:
                if (TextUtils.isEmpty(startTime)) {
                    ToastHelper.showShort("请选择开始时间");
                    break;
                }

                if (TextUtils.isEmpty(endTime)) {
                    ToastHelper.showShort("请选择结束时间");
                    break;
                }

                long startTimeLong = DateUtil.stringToLong(startTime, DateUtil.FORMAT_DATE);

                long endTimeLong = DateUtil.stringToLong(endTime, DateUtil.FORMAT_DATE);

                if (endTimeLong - startTimeLong < 0) {
                    ToastHelper.showShort("结束时间不能小于开始时间");
                    break;
                }

                EventBus.getDefault().post(new EventMessage(Messages.REFRESH_TIME, startTime + "," + endTime));

                tvStartTime.setText("请选择开始时间");
                tvEndTime.setText("请选择结束时间");
                rlSelect.setVisibility(View.GONE);

                showSelect = false;

                startTime = "";
                endTime = "";
                break;
        }
    }

    /*********************************************地址的筛选框---start******************************/
    //乡镇ID
    private int townshipId;
    //街村ID
    private int villageId;

    //当前的单位id
    public int currentUnitId = 1;

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
                                    departmentBean = new DepartmentBean();
                                    departmentBean.id = 1;
                                    departmentBean.departmentName = "辛集市";
                                    leftDepartments.add(0, departmentBean);

                                    //如果是用户bean为空
                                    initCityDialog(leftDepartments, new ArrayList<>());
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
    private void initCityDialog(List<DepartmentBean> leftData,
                                List<DepartmentBean> rightData) {
        if (null == cityDialog) {
            cityDialog = new ScreenCityPopupWindow(
                    this,
                    DisplayUtil.getScreenWidth(),
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    leftData,
                    rightData,
                    true
            );

            cityDialog.setOnScreenClick(new ScreenCityPopupWindow.onScreenClick() {
                @Override
                public void onLeftClick(int cityId, String departmentName) {
                    townshipName = departmentName;
                    if (LEFT_ALL_ID == cityId) {
                        if (2 == accountBean.level) {
                            townshipId = accountBean.unitId;
                        } else {
                            townshipId = 0;
                        }
                        villageId = 0;

                        villageName = "";

                        cityDialog.setRightNewData(new ArrayList<>());
                        cityDialog.dismiss();
                    } else {
                        townshipId = cityId;
                        getDepartmentList(cityId);
                    }
                }

                @Override
                public void onRightClick(int cityId, String departmentName, String terUri) {
                    if (RIGHT_ALL_ID == cityId) {
                        villageId = 0;
                        villageName = "";
                        cityDialog.dismiss();
                    } else {
                        //如果没登录，并且是微心愿
                        villageId = cityId;

                        //设置当前的单位id
                        currentUnitId = cityId;

                        villageName = departmentName;

                        cityDialog.dismiss();

                        //置空左边的数据
                        //leftDepartments = null;

                        //重新填充数据
                        currentIndex = 0;

                        villageId = villageId;

                        if (townshipName.equals(villageName)) {
                            tvScreen.setText(villageName);
                        } else {
                            tvScreen.setText(townshipName + villageName);
                        }
                        EventBus.getDefault().post(new EventMessage(Messages.REFRESH_UNIT_ID, currentUnitId));
                    }
                }
            });
        }
        showCityDialog();
    }

    private void showCityDialog() {
        cityDialog.setAlignBackground(false);
        cityDialog.showPopupWindow(rlScreen);
//        screenCityDialog.setPopupGravity(Gravity.BOTTOM);
    }
    /*********************************************地址的筛选框---end******************************/

    /******************************时间对话框-start***********************************/
    private RelativeLayout rlSelect;
    private TextView tvStartTime;
    private TextView tvEndTime;
    private TextView tvCancle;
    private TextView tvOk;

    //是否选择开始时间
    private boolean isStart = false;

    //显示时间选择框
    private boolean showSelect = false;
    /******************************时间对话框-end***********************************/

    /******************************时间选择器-start***********************************/
    public boolean updateTime = false;

    public String startTime;
    public String endTime;

    private TimePickerView pvCustomLunar;

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
                if (isStart) {
                    startTime = DateUtil.dateToString(date, DateUtil.FORMAT_DATE);
                    tvStartTime.setText(startTime);
                } else {
                    endTime = DateUtil.dateToString(date, DateUtil.FORMAT_DATE);
                    tvEndTime.setText(endTime);
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
                .setType(new boolean[]{true, true, true, false, false, false})
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(Color.RED)
                .build();
    }

    private void showLunarPicker(boolean isStart) {
        this.isStart = isStart;
        if (null == pvCustomLunar) {
            initLunarPicker();
        }
        pvCustomLunar.setTitleText(isStart ? "请选择开始时间" : "请选择结束时间");
        pvCustomLunar.show();
    }

    /******************************时间选择器-end***********************************/

    private void register() {
        Log.i("StatisticsFragment", title + "注册了");
        if (!EventBus.getDefault().isRegistered(this)) {
            //注册EventBsus,注意参数是this，传入activity会报错
            EventBus.getDefault().register(this);
        }
    }

    private void unRegister() {
        Log.i("StatisticsFragment", title + "取消注册了");
        if (EventBus.getDefault().isRegistered(this)) {
            //注册EventBus,注意参数是this，传入activity会报错
            EventBus.getDefault().unregister(this);
        }
    }

    //EventBus的处理事件函数  该方法可以随意取名 必须为public 必须添加注解并指定线程模型
    //之后EventBus会自动扫描到此函数，进行数据传递
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshLayout(EventMessage eventMessage) {
        switch (eventMessage.message) {
            //更新微心愿
            case Messages.REFRESH_WXY:
                //刷新微心愿
                refreshWxy = true;
                getWxyCount();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unRegister();
    }
}
