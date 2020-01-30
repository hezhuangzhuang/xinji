package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.jaeger.library.StatusBarUtil;
import com.zxwl.commonlibrary.utils.DisplayUtil;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.commonlibrary.widget.NoScrollViewPager;
import com.zxwl.ecsdk.common.UIConstants;
import com.zxwl.frame.activity.LoadingActivity;
import com.zxwl.frame.utils.sharedpreferences.PreferencesHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.bean.response.DepartmentBean;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.fragmentadapter.BasePagerAdapter;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.fragment.DocumentFragment;
import com.zxwl.xinji.fragment.RefreshRecyclerFragment;
import com.zxwl.xinji.utils.PreferenceUtil;
import com.zxwl.xinji.widget.ScreenCityPopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * 组织详情
 */
public class OrganizingLifeDetailsActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBackOperate;
    private ImageView ivPublish;
    private TextView tvTitle;
    private TextView tvIntroduction;
    private SlidingTabLayout tabLayout;
    private NoScrollViewPager vpContent;
    private RelativeLayout rlContent;
    private Space space;

    private List<String> mTitles;

    private List<Fragment> mFragments;

    private int currentIndex = 0;//当前下标

    public static final String CURRENT_TAB = "CURRENT_TAB";

    //单位id
    public static final String UNIT_ID = "UNIT_ID";

    //村名
    public static final String VILLAGE_NAME = "VILLAGE_NAME";

    //党支部名称
    public static final String PARTY_NAME = "PARTY_NAME";

    //党支部名称
    public static final String CHECK_ADMIN = "CHECK_ADMIN";

    //父结构的id
    public static final String PARENT_ID = "PARENT_ID";

    //父结构的id
    public static final String CALL_NUMBER = "CALL_NUMBER";

    private int unitId;//单位id

    private LoginBean.AccountBean accountBean;

    private boolean showPublish = false;//是否显示发布按钮

    public static void startActivity(Context context,
                                     int index,
                                     int unitId,
                                     String village,
                                     String partyName,
                                     int parentId,
                                     String callNumber) {
        Intent intent = new Intent(context, OrganizingLifeDetailsActivity.class);
        intent.putExtra(CURRENT_TAB, index);
        intent.putExtra(UNIT_ID, unitId);
        intent.putExtra(VILLAGE_NAME, village);
        intent.putExtra(PARTY_NAME, partyName);
        intent.putExtra(PARENT_ID, parentId);
        intent.putExtra(CALL_NUMBER, callNumber);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        ivPublish = (ImageView) findViewById(R.id.iv_publish);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvIntroduction = (TextView) findViewById(R.id.tv_introduction);
        tabLayout = (SlidingTabLayout) findViewById(R.id.tb_layout);
        vpContent = (NoScrollViewPager) findViewById(R.id.vp_content);

        rlContent = (RelativeLayout) findViewById(R.id.rl_content);
        space = (Space) findViewById(R.id.space);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setTranslucentForImageView(this, 0, ivBackOperate);

        townshipId = getIntent().getIntExtra(PARENT_ID, -1);

        currentIndex = getIntent().getIntExtra(CURRENT_TAB, 0);
        unitId = getIntent().getIntExtra(UNIT_ID, -1);
        callNumber = getIntent().getStringExtra(CALL_NUMBER);
        tvTitle.setText(getIntent().getStringExtra(VILLAGE_NAME));
        tvIntroduction.setText(getIntent().getStringExtra(PARTY_NAME) + "党支部");

        accountBean = PreferenceUtil.getUserInfo(this);

        setPublishShow(unitId);

        initTabLayout();

        register();
    }

    private void setPublishShow(int unitId) {
        //是否显示发布按钮
        //是否是
        showPublish = (unitId == accountBean.unitId && 1 == accountBean.checkAdmin);

        //如果不是管理员则都不显示
        if (1 != accountBean.checkAdmin) {
            ivPublish.setVisibility(View.GONE);
        } else {
            //市级账号
            if (1 == accountBean.level) {
                //当前看的是自己
                if (unitId == accountBean.unitId) {
                    ivPublish.setVisibility(View.VISIBLE);
                    ivPublish.setImageResource(R.mipmap.ic_publish);
                } else {
                    ivPublish.setVisibility(View.GONE);
                    ivPublish.setImageResource(R.mipmap.ic_site_call);
                }
            }//乡镇
            else if (2 == accountBean.level) {
                //当前看的是自己
                if (unitId == accountBean.unitId) {
                    ivPublish.setVisibility(View.VISIBLE);
                    ivPublish.setImageResource(R.mipmap.ic_publish);
                } else if (accountBean.unitId == townshipId) {
                    ivPublish.setVisibility(View.GONE);
                    ivPublish.setImageResource(R.mipmap.ic_site_call);
                } else {
                    ivPublish.setVisibility(View.GONE);
                }
            } //街村
            else if (3 == accountBean.level) {
                if (showPublish) {
                    ivPublish.setVisibility(View.VISIBLE);
                    ivPublish.setImageResource(R.mipmap.ic_publish);
                } else {
                    ivPublish.setVisibility(View.GONE);
                }
            }
        }

//        //市级单位
//        if (1 == accountBean.level) {
//            //当前看的是自己
//            if (unitId == accountBean.unitId) {
//                //判断是否是管理员
//                if (1 == accountBean.checkAdmin) {
//                    ivPublish.setVisibility(View.VISIBLE);
//                    ivPublish.setImageResource(R.mipmap.ic_publish);
//                } else {
//                    ivPublish.setVisibility(View.GONE);
//                }
//            }//判断上级是不是自己
//            else if (accountBean.unitId == townshipId) {
//                ivPublish.setVisibility(View.VISIBLE);
//                ivPublish.setImageResource(R.mipmap.ic_site_call);
//            }
//        }//乡镇
//        else if ((2 == accountBean.level)) {
//            if (showPublish) {
//                ivPublish.setVisibility(View.VISIBLE);
//                ivPublish.setImageResource(R.mipmap.ic_publish);
//            } else if (accountBean.unitId == townshipId) {
//                ivPublish.setVisibility(View.VISIBLE);
//                ivPublish.setImageResource(R.mipmap.ic_site_call);
//            } else {
//                ivPublish.setVisibility(View.GONE);
//            }
//        }//街村
//        else {
//            if (showPublish) {
//                ivPublish.setVisibility(View.VISIBLE);
//                ivPublish.setImageResource(R.mipmap.ic_publish);
//            } else {
//                ivPublish.setVisibility(View.GONE);
//            }
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unRegister();
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(this);
        ivPublish.setOnClickListener(this);
        tvTitle.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_organizing_life_details;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_operate:
                finish();
                break;

            case R.id.iv_publish:
                if (showPublish) {
                    ReleaseSelectActivity.startActivity(OrganizingLifeDetailsActivity.this);
                } else {
                    if (TextUtils.isEmpty(callNumber)) {
                        ToastHelper.showShort("暂无呼叫号码");
                        return;
                    }
//                    HuaweiCallImp.getInstance().callSite(callNumber);
                    createConfRequest(callNumber+",");
                }
                break;

            case R.id.tv_title:
                //对话框已经显示
                if (null != screenCityDialog && screenCityDialog.isShowing()) {
                    screenCityDialog.dismiss();
                } else {
                    if (null != screenCityDialog) {
                        showCityDialog();
                    } else {
                        getDepartmentList(1);
                    }
                }
                break;
        }
    }

    /**
     * 创建会议
     */
    private void createConfRequest(String unitIdsAndSiteIds) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
//                .setBaseUrl(!TextUtils.isEmpty(Urls.CREATE_BASE_URL)?Urls.BASE_URL:"")
                .setBaseUrl(Urls.CREATE_BASE_URL)
                .builder(StudyApi.class)
                .createConf(unitIdsAndSiteIds, accountBean.id)
                .compose(this.bindToLifecycle())
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            EventBus.getDefault().post(new EventMessage(Messages.REFRESH_RECYCLER, ""));

                            LoadingActivity.startActivty(OrganizingLifeDetailsActivity.this, "");

                            //是否自己创建的会议
                            PreferencesHelper.saveData(UIConstants.IS_CREATE, true);

                            //是否需要自动接听
                            PreferencesHelper.saveData(UIConstants.IS_AUTO_ANSWER, true);

                            ToastHelper.showShort(baseData.message);

                            finish();
                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        ToastHelper.showShort("error" + responeThrowable.message);
                    }
                });
    }

    private void initTabLayout() {
        mFragments = new ArrayList<>();
        mTitles = new ArrayList<>();
//        mTitles.add(UnitProfileFragment.TYPE_JBJJ);
//        mTitles.add(UnitProfileFragment.TYPE_DJZD);
        mTitles.add(RefreshRecyclerFragment.TYPE_ZTDR);
        mTitles.add(RefreshRecyclerFragment.TYPE_SHYK);
        mTitles.add(RefreshRecyclerFragment.TYPE_MZPY);
        mTitles.add(RefreshRecyclerFragment.TYPE_ZZSHH);
        mTitles.add(RefreshRecyclerFragment.TYPE_MORE);

//        mFragments.add(UnitProfileFragment.newInstance(unitId, showPublish, true));//单位简介
//        mFragments.add(UnitProfileFragment.newInstance(unitId, showPublish, false));//党建阵地
        mFragments.add(DocumentFragment.newInstance(RefreshRecyclerFragment.TYPE_ZTDR, unitId));//主题党日
        mFragments.add(DocumentFragment.newInstance(RefreshRecyclerFragment.TYPE_SHYK, unitId));//三会一课
        mFragments.add(DocumentFragment.newInstance(RefreshRecyclerFragment.TYPE_MZPY, unitId));//民主评议
        mFragments.add(DocumentFragment.newInstance(RefreshRecyclerFragment.TYPE_ZZSHH, unitId));//组织生活会
        mFragments.add(DocumentFragment.newInstance(RefreshRecyclerFragment.TYPE_MORE, unitId));//其他

        BasePagerAdapter mAdapter = new BasePagerAdapter(getSupportFragmentManager(), mFragments, mTitles);
        vpContent.setAdapter(mAdapter);
        tabLayout.setViewPager(vpContent);

        vpContent.setCurrentItem(currentIndex);
        vpContent.setOffscreenPageLimit(mTitles.size());

        tabLayout.setCurrentTab(currentIndex);
        tabLayout.onPageSelected(currentIndex);
    }

    private void register() {
        if (isRegisterEventBus) {
            if (!EventBus.getDefault().isRegistered(this)) {
                //注册EventBsus,注意参数是this，传入activity会报错
                EventBus.getDefault().register(this);
            }
        }
    }

    private void unRegister() {
        if (isRegisterEventBus) {
            if (EventBus.getDefault().isRegistered(this)) {
                //注册EventBus,注意参数是this，传入activity会报错
                EventBus.getDefault().unregister(this);
            }
        }
    }

    //EventBus的处理事件函数  该方法可以随意取名 必须为public 必须添加注解并指定线程模型
    //之后EventBus会自动扫描到此函数，进行数据传递
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshRecycler(EventMessage eventMessage) {
        switch (eventMessage.message) {
            case Messages.REFRESH_RECYCLER:
                tabLayout.setCurrentTab(getIndex(eventMessage.succeed));
                break;
        }
    }

    private int getIndex(String title) {
        switch (title) {
            //主题党日
            case ReleaseConfActivity.TYPE_ZTDR:
                return 1;
            case ReleaseConfActivity.TYPE_SHYK:
                return 2;
            case ReleaseConfActivity.TYPE_MZPY:
                return 3;
            case ReleaseConfActivity.TYPE_ZZSHH:
                return 4;
            case ReleaseConfActivity.TYPE_MORE:
                return 5;
        }
        return 0;
    }

    /*********************************************地址的筛选框---start******************************/
    //乡镇ID
    private int townshipId;
    //街村ID
    private int villageId;

    //乡镇名称
    private String townshipName;
    //街村名称
    private String villageName;

    //呼叫的号码
    private String callNumber;

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
                                    screenCityDialog.setRightNewData(dataList);
                                }
                            } else {
                                if (null != leftDepartments) {
                                    screenCityDialog.setRightNewData(new ArrayList<>());
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

    private ScreenCityPopupWindow screenCityDialog;

    /**
     * 显示筛选对话框
     */
    private void showScreenDialog(List<DepartmentBean> leftData,
                                  List<DepartmentBean> rightData) {
        if (null == screenCityDialog) {
            screenCityDialog = new ScreenCityPopupWindow(
                    this,
                    DisplayUtil.getScreenWidth(),
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    leftData,
                    rightData,
                    true
            );

            screenCityDialog.setOnScreenClick(new ScreenCityPopupWindow.onScreenClick() {
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

                        screenCityDialog.setRightNewData(new ArrayList<>());
                        screenCityDialog.dismiss();
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
                        screenCityDialog.dismiss();
                    } else {
                        //如果没登录，并且是微心愿
                        villageId = cityId;

                        villageName = departmentName;

                        screenCityDialog.dismiss();

                        //置空左边的数据
//                        leftDepartments = null;

                        //重新填充数据
                        currentIndex = 0;

                        unitId = villageId;
                        tvTitle.setText(villageName);
                        tvIntroduction.setText(villageName + "党支部");
                        callNumber = terUri;

                        //设置显示按钮
                        setPublishShow(unitId);

                        initTabLayout();
                    }
                }
            });
        }
        showCityDialog();
    }

    private void showCityDialog() {
        screenCityDialog.setAlignBackground(false);
        screenCityDialog.showPopupWindow(space);
//        screenCityDialog.setPopupGravity(Gravity.BOTTOM);
    }
    /*********************************************地址的筛选框---end******************************/

}
