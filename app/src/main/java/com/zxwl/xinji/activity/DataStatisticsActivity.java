package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.commonlibrary.widget.NoScrollViewPager;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.MyPagerAdapter;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.fragment.StatisticsFragment;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 数据统计
 */
public class DataStatisticsActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private SlidingTabLayout tbLayout;
    private NoScrollViewPager vpContent;

    private ImageView ivRightOperate;

    private List<String> mTitles;

    private List<Fragment> mFragments = new ArrayList<>();

    //当前下标
    private int currentIndex = 0;

    //是否选择开始时间
    private boolean isStart = false;

    //显示时间选择框
    private boolean showSelect = false;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, DataStatisticsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tbLayout = (SlidingTabLayout) findViewById(R.id.tb_layout);
        vpContent = (NoScrollViewPager) findViewById(R.id.vp_content);
        ivRightOperate = (ImageView) findViewById(R.id.iv_right_operate);

        rlSelect = (RelativeLayout) findViewById(R.id.rl_select);

        tvStartTime = (TextView) findViewById(R.id.tv_start_time);
        tvEndTime = (TextView) findViewById(R.id.tv_end_time);
        tvCancle = (TextView) findViewById(R.id.tv_cancle);
        tvOk = (TextView) findViewById(R.id.tv_ok);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        ivRightOperate.setImageResource(R.mipmap.ic_select_time);
        ivRightOperate.setVisibility(View.VISIBLE);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        tvTopTitle.setText( "数据统计");

        initTab();
    }

    private void initTab() {
        mTitles = new ArrayList<>();
        mTitles.add(StatisticsFragment.TYPE_XXJY);
        mTitles.add(StatisticsFragment.TYPE_SHYK);
        mTitles.add(StatisticsFragment.TYPE_ZZSH);
        mTitles.add(StatisticsFragment.TYPE_ZYFW);

        mFragments.add(StatisticsFragment.newInstance(StatisticsFragment.TYPE_XXJY,0));//工作动态
        mFragments.add(StatisticsFragment.newInstance(StatisticsFragment.TYPE_SHYK,1));//三会一课
        mFragments.add(StatisticsFragment.newInstance(StatisticsFragment.TYPE_ZZSH,2));//组织生活
        mFragments.add(StatisticsFragment.newInstance(StatisticsFragment.TYPE_ZYFW,3));//帮扶活动

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
                startTime = "";
                endTime = "";
                currentIndex = position;

                EventBus.getDefault().post(new EventMessage(Messages.REFRESH_RECYCLER, startTime + "," + endTime+","+ currentIndex));
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
                startTime = "";
                endTime = "";
                currentIndex = position;

                EventBus.getDefault().post(new EventMessage(Messages.REFRESH_RECYCLER, startTime + "," + endTime+","+ currentIndex));
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(this);
        ivRightOperate.setOnClickListener(this);

        tvStartTime.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);
        tvCancle.setOnClickListener(this);
        tvOk.setOnClickListener(this);
    }

    private RelativeLayout rlSelect;
    private TextView tvStartTime;
    private TextView tvEndTime;
    private TextView tvCancle;
    private TextView tvOk;

    private String startTime;
    private String endTime;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_data_statistics;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_operate:
                finish();
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

            case R.id.tv_start_time:
                isStart = true;
                initLunarPicker();
                pvCustomLunar.show();
                break;

            case R.id.tv_end_time:
                isStart = false;
                initLunarPicker();
                pvCustomLunar.show();
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

                EventBus.getDefault().post(new EventMessage(Messages.REFRESH_RECYCLER, startTime + "," + endTime+","+ currentIndex));

                tvStartTime.setText("请选择开始时间");
                tvEndTime.setText("请选择结束时间");
                rlSelect.setVisibility(View.GONE);

                showSelect = false;

                startTime = "";
                endTime = "";
                break;
        }
    }

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
                    startTime = getTime(date);
                    tvStartTime.setText(startTime);
                } else {
                    endTime = getTime(date);
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

    //可根据需要自行截取数据显示
    private String getTime(Date date) {
        Log.d("getTime()", "choice date millis: " + date.getTime());
        SimpleDateFormat format = new SimpleDateFormat(DateUtil.FORMAT_DATE);
        return format.format(date);
    }
}
