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
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.response.DepartmentBean;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.fragmentadapter.BasePagerAdapter;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.fragment.DocumentFragment;
import com.zxwl.xinji.fragment.MapBasicFragment;
import com.zxwl.xinji.fragment.RefreshRecyclerFragment;
import com.zxwl.xinji.fragment.VillageListFragment;
import com.zxwl.xinji.utils.PreferenceUtil;
import com.zxwl.xinji.widget.ScreenCityPopupWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * 党建地图详情
 */
public class MapDetailsActivity extends BaseActivity implements View.OnClickListener {
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

    //当前下标
    private int currentIndex = 0;
    //单位id
    private int unitId;
    private DepartmentBean departmentBean;

    public static final String CURRENT_INDEX = "CURRENT_INDEX";
    public static final String UNIT_ID = "UNIT_ID";
    public static final String CURRENT_DEPARTMENT = "CURRENT_DEPARTMENT";
    public static final String IS_MAP_CLICK = "IS_MAP_CLICK";

    private LoginBean.AccountBean accountBean;

    //是否显示发布按钮
    private boolean showPublish = false;

    //是否点击地图进来
    private boolean isMapClick = false;

    public static void startActivity(Context context,
                                     int index,
                                     int unitId,
                                     DepartmentBean departmentBean) {
        Intent intent = new Intent(context, MapDetailsActivity.class);
        intent.putExtra(CURRENT_INDEX, index);
        intent.putExtra(UNIT_ID, unitId);
        intent.putExtra(CURRENT_DEPARTMENT, departmentBean);
        context.startActivity(intent);
    }

    public static void startActivity(Context context,
                                     int index,
                                     int unitId,
                                     DepartmentBean departmentBean,
                                     boolean isMapClick) {
        Intent intent = new Intent(context, MapDetailsActivity.class);
        intent.putExtra(CURRENT_INDEX, index);
        intent.putExtra(UNIT_ID, unitId);
        intent.putExtra(CURRENT_DEPARTMENT, departmentBean);
        intent.putExtra(IS_MAP_CLICK, isMapClick);
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

        unitId = getIntent().getIntExtra(UNIT_ID, -1);
        currentIndex = getIntent().getIntExtra(CURRENT_INDEX, 0);

        departmentBean = (DepartmentBean) getIntent().getSerializableExtra(CURRENT_DEPARTMENT);

        isMapClick = getIntent().getBooleanExtra(IS_MAP_CLICK, false);

        accountBean = PreferenceUtil.getUserInfo(this);

        if (null == departmentBean) {
            tvTitle.setText(accountBean.unitName);
            tvIntroduction.setText(accountBean.unitName + "党支部");
        } else {
            tvTitle.setText(departmentBean.departmentName);
            tvIntroduction.setText(departmentBean.departmentName + "党支部");
        }

        //设置发布按钮是否显示
        setPublishShow(unitId);

        //判断是否有查看村级组织的权限
        setAuthority(unitId);

        if (isMapClick) {
            level = 3;
        } else {
            //判断级别
            level = accountBean.level;
        }

        //初始化tab
        initTabLayout();
    }

    //是否有查询村级组织的权限
    private boolean isAuthority = false;

    //2代表乡镇，3代表街村
    private int level;

    private void setAuthority(int unitId) {
        boolean isShow = false;
        if (null != departmentBean) {
            isShow = (accountBean.unitId == unitId || accountBean.unitId == townshipId || accountBean.unitId == departmentBean.parentId);
        } else {
            //如果是本级或下级
            isShow = (accountBean.unitId == unitId || accountBean.unitId == townshipId);
        }

        if (isAdmin() && accountBean.level == 1) {
            isAuthority = true;
        } else if (isAdmin() && isShow) {
            isAuthority = true;
        } else {
            isAuthority = false;
        }
    }


    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(this);
        ivPublish.setOnClickListener(this);
        tvTitle.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_map_details;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_operate:
                finish();
                break;

            case R.id.iv_publish:
                if (showPublish) {
                    ReleaseSelectActivity.startActivity(MapDetailsActivity.this, false);
                } else {
                    if (TextUtils.isEmpty(callNumber)) {
                        ToastHelper.showShort("暂无呼叫号码");
                        return;
                    }
                    // HuaweiCallImp.getInstance().callSite(callNumber);
                    //TODO:呼叫功能
                    //  createConfRequest(callNumber+",");
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
                        //管理员看所有,市级普通账户看所有
                        if (isAdmin() || isGeneralCity()) {
                            getDepartmentList(1);
                        } else if (isTownLeavel()) {
                            //添加辛集市在左边
                            DepartmentBean departmentBean = new DepartmentBean();
                            departmentBean.id = accountBean.unitId;
                            departmentBean.departmentName = accountBean.unitName;
                            leftDepartments = new ArrayList<>();
                            leftDepartments.add(0, departmentBean);

                            showScreenDialog(leftDepartments, new ArrayList<>());
                        }
//                        if (isTownLeavel()) {
//
//                        } else {
//                            getDepartmentList(1);
//                        }
                    }
                }
                break;
        }
    }

    /**
     * 是否是乡镇
     *
     * @return
     */
    private boolean isTownLeavel() {
        return accountBean.level == 2;
    }

    /**
     * 是否是管理员
     *
     * @return
     */
    private boolean isAdmin() {
        return accountBean.checkAdmin == 1;
    }

    /**
     * 是否是市级普通账户
     */
    private boolean isGeneralCity() {
        return accountBean.checkAdmin != 1 && accountBean.level == 1;
    }


    /**
     * 设置发布按钮是否显示
     *
     * @param unitId
     */
    private void setPublishShow(int unitId) {
        //是否显示发布按钮
        showPublish = (unitId == accountBean.unitId && 1 == accountBean.checkAdmin);

        //如果不是管理员则都不显示
        if (1 != accountBean.checkAdmin) {
            ivPublish.setVisibility(View.GONE);
        } else {
            if (unitId == accountBean.unitId) {
                ivPublish.setVisibility(View.VISIBLE);
                ivPublish.setImageResource(R.mipmap.ic_publish);
            } else {
                ivPublish.setVisibility(View.GONE);
            }
        }
    }

    private void initTabLayout() {
        mFragments = new ArrayList<>();
        mTitles = new ArrayList<>();
        //村情概况
        mTitles.add(MapBasicFragment.TYPE_CQGK);
        //村级组织
        mTitles.add(VillageListFragment.TYPE_CJZZ);
        //党建阵地
        mTitles.add(MapBasicFragment.TYPE_DJZD);
        //集体经济
        mTitles.add(MapBasicFragment.TYPE_JTJJ);
        //图说本村
        mTitles.add(RefreshRecyclerFragment.TYPE_TSBC);
        //荣誉表彰
        mTitles.add(RefreshRecyclerFragment.TYPE_RYBZ);
        //亮点工作
        mTitles.add(RefreshRecyclerFragment.TYPE_LDGZ);

        //基本信息
        mFragments.add(MapBasicFragment.newInstance(unitId, showPublish, MapBasicFragment.TYPE_CQGK));
        //村级组织
        mFragments.add(VillageListFragment.newInstance(unitId, isAuthority, level));
        //党建阵地
        mFragments.add(MapBasicFragment.newInstance(unitId, showPublish, MapBasicFragment.TYPE_DJZD));
        //集体经济
        mFragments.add(MapBasicFragment.newInstance(unitId, showPublish, MapBasicFragment.TYPE_JTJJ));
        //图说本村
        mFragments.add(DocumentFragment.newInstance(RefreshRecyclerFragment.TYPE_TSBC, unitId));
        //荣誉表彰
        mFragments.add(DocumentFragment.newInstance(RefreshRecyclerFragment.TYPE_RYBZ, unitId));
        //亮点工作
        mFragments.add(DocumentFragment.newInstance(RefreshRecyclerFragment.TYPE_LDGZ, unitId));

        BasePagerAdapter mAdapter = new BasePagerAdapter(getSupportFragmentManager(), mFragments, mTitles);
        vpContent.setAdapter(mAdapter);
        tabLayout.setViewPager(vpContent);

        vpContent.setCurrentItem(currentIndex);
        vpContent.setOffscreenPageLimit(mTitles.size());

        tabLayout.setCurrentTab(currentIndex);
        tabLayout.onPageSelected(currentIndex);
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

                        departmentBean = null;
                        //判断是否有查看村级组织的权限
                        setAuthority(villageId);

                        //如果左边选中的为辛集市则是乡镇账号
                        if ("辛集市".equals(townshipName)) {
                            level = 2;
                        } else {
                            level = 3;
                        }

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
