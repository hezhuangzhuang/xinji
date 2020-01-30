package com.zxwl.xinji.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.zxwl.commonlibrary.BaseLazyFragment;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.commonlibrary.utils.DisplayUtil;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.commonlibrary.utils.WeekDayUtil;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.response.CurrencyBean;
import com.zxwl.network.bean.response.DepartmentBean;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.bean.response.ThemePartyBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.xinji.R;
import com.zxwl.xinji.activity.ContentDetailsActivity;
import com.zxwl.xinji.activity.MineActivity;
import com.zxwl.xinji.activity.OrganizingLifeDetailsActivity;
import com.zxwl.xinji.adapter.OrganizingLifeAdapter;
import com.zxwl.xinji.adapter.item.StudyListItem;
import com.zxwl.xinji.bean.ContentDetailsBean;
import com.zxwl.xinji.bean.StudyHeadBean;
import com.zxwl.xinji.utils.Constant;
import com.zxwl.xinji.utils.PreferenceUtil;
import com.zxwl.xinji.widget.ScreenCityPopupWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.functions.Func5;

/**
 * 组织生活
 */
public class OrganizingLifeMaterialFragment extends BaseLazyFragment implements View.OnClickListener {
    private TextView tvTopTitle;
    private TextView tvRightOperate;
    private TextView tvZtdr;
    private TextView tvShyk;
    private TextView tvMzpy;
    private TextView tvZzshh;
    private TextView tvMore;
    private RelativeLayout rlTopTitle;
    private LinearLayout llDown;
    private ImageView ivLeftDown;
    private ImageView ivBackOperate;
    private TextView tvTopCity;

    private FrameLayout flTop;

    private RecyclerView rvList;
    private OrganizingLifeAdapter organizingLifeAdapter;
    private LinearLayoutManager linearLayoutManager;

    private LoginBean.AccountBean accountBean;

    private View errorView;

    private int requestYear;

    public OrganizingLifeMaterialFragment() {
    }

    private String TAG = "OrganizingLifeFragment";

    private CollapsingToolbarLayout ctl_test_bar;
    private AppBarLayout abl_test_bar;

    public static OrganizingLifeMaterialFragment newInstance() {
        OrganizingLifeMaterialFragment fragment = new OrganizingLifeMaterialFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View inflateContentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_organizing_life_material, container, false);
    }

    @Override
    protected void findViews(View view) {
        Log.i(TAG, TAG + "-->findViews");

        tvTopTitle = (TextView) view.findViewById(R.id.tv_top_title);
        tvZtdr = (TextView) view.findViewById(R.id.tv_ztdr);
        tvShyk = (TextView) view.findViewById(R.id.tv_shyk);
        tvMzpy = (TextView) view.findViewById(R.id.tv_mzpy);
        tvZzshh = (TextView) view.findViewById(R.id.tv_zzshh);
        tvMore = (TextView) view.findViewById(R.id.tv_more);
        rlTopTitle = (RelativeLayout) view.findViewById(R.id.rl_top_title);
        llDown = (LinearLayout) view.findViewById(R.id.ll_down);
//        flTop = (FrameLayout) view.findViewById(R.id.fl_top);
        ivLeftDown = (ImageView) view.findViewById(R.id.iv_left_down);
        ivBackOperate = (ImageView) view.findViewById(R.id.iv_back_operate);
        tvTopCity = (TextView) view.findViewById(R.id.tv_top_city);

        rvList = (RecyclerView) view.findViewById(R.id.rv_list);

        tvRightOperate = (TextView) view.findViewById(R.id.tv_right_operate);

        flTop = (FrameLayout) view.findViewById(R.id.fl_top);

        ctl_test_bar = (CollapsingToolbarLayout) view.findViewById(R.id.ctl_test_bar);
        abl_test_bar = (AppBarLayout) view.findViewById(R.id.abl_test_bar);

        errorView = getLayoutInflater().inflate(R.layout.include_error, (ViewGroup) rvList.getParent(), false);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void addListeners() {
        tvZtdr.setOnClickListener(this);
        tvShyk.setOnClickListener(this);
        tvMzpy.setOnClickListener(this);
        tvZzshh.setOnClickListener(this);
        tvMore.setOnClickListener(this);
//        llDown.setOnClickListener(this);

        tvRightOperate.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        tvTopTitle.setText("组织生活");
        rlTopTitle.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tran));

//        ImmersionBar.with(this)
//                // 默认状态栏字体颜色为黑色
////                .statusBarDarkFont(statusBarDarkFont())
//                // 解决软键盘与底部输入框冲突问题，默认为false，还有一个重载方法，可以指定软键盘mode
//                .keyboardEnable(true)
//                .init();
//
//        //设置沉浸式
//        ImmersionBar.setTitleBar(this, rlTopTitle);

        requestYear = Calendar.getInstance().get(Calendar.YEAR);

        //如果用户信息不为空则请求数据
        if (isLogin()) {
            Log.i(TAG, TAG + "-->initData");

            accountBean = PreferenceUtil.getUserInfo(getActivity());

            //初始化unitId和name
            unitId = accountBean.unitId;
            villageName = accountBean.unitName;

            initAdapter();

            //设置强制更新
            setForceLoad(true);

            //获取前三条数据
            getHotDataRequest();
        }
    }

    /**
     * 是否有登录
     *
     * @return true：登录，false：没登录
     */
    private boolean isLogin() {
        return PreferenceUtil.getBoolean(Constant.AUTO_LOGIN, false);
    }

    @NonNull
    private void initAdapter() {
        organizingLifeAdapter = new OrganizingLifeAdapter(R.layout.item_study_list, new ArrayList<>());
        organizingLifeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                CurrencyBean currencyBean = organizingLifeAdapter.getItem(position).newsBean;

                if (currencyBean instanceof ThemePartyBean) {
                    ThemePartyBean themePartyBean = (ThemePartyBean) currencyBean;
                    switch (themePartyBean.itemTyep) {
                        //"主题党日"
                        case RefreshRecyclerFragment.TYPE_ZTDR:
                            //"三会一课"
                        case RefreshRecyclerFragment.TYPE_SHYK:
                            startZtdrDetailsActivity(themePartyBean);
                            break;

                        //"民主评议"
                        case RefreshRecyclerFragment.TYPE_MZPY:
                            //"组织生活会"
                        case RefreshRecyclerFragment.TYPE_ZZSHH:
                            //"其他"
                        case RefreshRecyclerFragment.TYPE_MORE:
                            startContentActivity(themePartyBean);
                            break;
                    }
                } else {
                    StudyHeadBean studyHeadBean = (StudyHeadBean) currencyBean;
                    switch (studyHeadBean.title) {
                        //"主题党日"
                        case RefreshRecyclerFragment.TYPE_ZTDR:
                            startOrganizingLifeDetailsActivity(0);
                            break;

                        //"三会一课"
                        case RefreshRecyclerFragment.TYPE_SHYK:
                            startOrganizingLifeDetailsActivity(1);
                            break;

                        //"民主评议"
                        case RefreshRecyclerFragment.TYPE_MZPY:
                            startOrganizingLifeDetailsActivity(2);
                            break;

                        //"组织生活会"
                        case RefreshRecyclerFragment.TYPE_ZZSHH:
                            startOrganizingLifeDetailsActivity(3);
                            break;

                        //"其他"
                        case RefreshRecyclerFragment.TYPE_MORE:
                            startOrganizingLifeDetailsActivity(4);
                            break;
                    }
                }
            }
        });
        linearLayoutManager = new LinearLayoutManager(getActivity());
        rvList.setLayoutManager(linearLayoutManager);
        rvList.setAdapter(organizingLifeAdapter);

        showSkeletonSceen(organizingLifeAdapter);
    }

    /**
     * 跳到主题党日和三会一课
     *
     * @param themePartyBean
     */
    public void startZtdrDetailsActivity(ThemePartyBean themePartyBean) {
        List<String> imageUrls = Arrays.asList(
                themePartyBean.pic1,
                themePartyBean.pic2,
                themePartyBean.pic3,
                themePartyBean.pic4,
                themePartyBean.pic5,
                themePartyBean.pic6,
                themePartyBean.pic7,
                themePartyBean.pic8,
                themePartyBean.pic9
        );

        ContentDetailsBean contentDetailsBean = ContentDetailsBean.ContentDetailsBeanFactory(
                themePartyBean.title,
                themePartyBean.context,
                imageUrls,
                themePartyBean.videoUrl,
                themePartyBean.videoThumbnailUrl,
                themePartyBean.createDate,
                themePartyBean.creatorName
        );

        contentDetailsBean.oneLable = "一、时间";
        contentDetailsBean.one = DateUtil.longToString(themePartyBean.activityDate, DateUtil.FORMAT_DATE_CHINA) + " (" + WeekDayUtil.getWeek(DateUtil.longToString(themePartyBean.activityDate, DateUtil.FORMAT_DATE), WeekDayUtil.TYPE_XQ) + ")";

        contentDetailsBean.twoLable = "二、地点";
        contentDetailsBean.two = TextUtils.isEmpty(themePartyBean.address) ? "辛集市" : themePartyBean.address;

        contentDetailsBean.threeLable = "三、主持人";
        contentDetailsBean.three = themePartyBean.host;

        contentDetailsBean.fourLable = "四、参会人员";
        contentDetailsBean.four = "应到人数：" + themePartyBean.users + ", 实到人数：" + themePartyBean.attendUsers;

        contentDetailsBean.fiveLable = "五、形式";
        contentDetailsBean.five = RefreshRecyclerFragment.TYPE_SHYK.equals(themePartyBean.itemTyep) ? themePartyBean.typeValue : themePartyBean.typeVal;

        ContentDetailsActivity.startActivity(getActivity(), contentDetailsBean);
    }

    /**
     * "组织生活会"
     * "民主评议"
     * "其他"
     *
     * @param themePartyBean
     */
    public void startContentActivity(ThemePartyBean themePartyBean) {
        List<String> imageUrls = Arrays.asList(
                themePartyBean.pic1,
                themePartyBean.pic2,
                themePartyBean.pic3,
                themePartyBean.pic4,
                themePartyBean.pic5,
                themePartyBean.pic6,
                themePartyBean.pic7,
                themePartyBean.pic8,
                themePartyBean.pic9
        );

        ContentDetailsBean contentDetailsBean = ContentDetailsBean.ContentDetailsBeanFactory(
                themePartyBean.title,
                themePartyBean.context,
                imageUrls,
                themePartyBean.videoUrl,
                themePartyBean.videoThumbnailUrl,
                themePartyBean.createDate,
                themePartyBean.creatorName
        );
        contentDetailsBean.oneLable = "一、时间";
        contentDetailsBean.twoLable = "二、地点";
        contentDetailsBean.threeLable = "三、主持人";
        contentDetailsBean.fourLable = "四、人数";

        contentDetailsBean.one = DateUtil.longToString(themePartyBean.activityDate, DateUtil.FORMAT_DATE_CHINA) + " (" + WeekDayUtil.getWeek(DateUtil.longToString(themePartyBean.activityDate, DateUtil.FORMAT_DATE), WeekDayUtil.TYPE_XQ) + ")";
        contentDetailsBean.two = TextUtils.isEmpty(themePartyBean.address) ? "辛集市" : themePartyBean.address;
        contentDetailsBean.three = themePartyBean.host;
        contentDetailsBean.four = "应到人数：" + themePartyBean.users + ", 实到人数：" + themePartyBean.attendUsers;

        if (RefreshRecyclerFragment.TYPE_MZPY.equals(themePartyBean.itemTyep)) {
            contentDetailsBean.fiveLable = "五、被评议人数";
            contentDetailsBean.five = themePartyBean.reviewedNum;
        }
        ContentDetailsActivity.startActivity(getActivity(), contentDetailsBean);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //主题党日
            case R.id.tv_ztdr:
                startOrganizingLifeDetailsActivity(0);
                break;

            //三会一课
            case R.id.tv_shyk:
                startOrganizingLifeDetailsActivity(1);
                break;

            //民主评议
            case R.id.tv_mzpy:
                startOrganizingLifeDetailsActivity(2);
                break;

            //组织生活会
            case R.id.tv_zzshh:
                startOrganizingLifeDetailsActivity(3);
                break;

            //其他
            case R.id.tv_more:
                startOrganizingLifeDetailsActivity(4);
                break;

            //我的
            case R.id.tv_right_operate:
                MineActivity.startActivity(getActivity());
                break;

            //筛选框
            case R.id.ll_down:
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

    public void getAccountBean() {
        if (null == accountBean) {
            accountBean = PreferenceUtil.getUserInfo(getActivity());
        }
    }

    public void startOrganizingLifeDetailsActivity(int index) {
        OrganizingLifeDetailsActivity.startActivity(
                getActivity(),
                index,
                unitId,
                villageName,
                villageName,
                townshipId,
                callNumber);
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

    private int unitId;//单位id

    /**
     * 获取组织信息
     */
    private void getDepartmentList(int currentUnitId) {
        HttpUtils.getInstance(getActivity())
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
                    getActivity(),
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

                        tvTopCity.setText(villageName);

                        screenCityDialog.dismiss();

                        //置空左边的数据
                        // leftDepartments = null;

                        unitId = villageId;
                        callNumber = terUri;

                        getHotDataRequest();
                    }
                }
            });
        }
        showCityDialog();
    }

    private void showCityDialog() {
        screenCityDialog.setAlignBackground(false);
        screenCityDialog.showPopupWindow(flTop);
//        screenCityDialog.setPopupGravity(Gravity.BOTTOM);
//        screenCityDialog.showPopupWindow();
    }
    /*********************************************地址的筛选框---end******************************/

    /**
     * 获取热门学习
     */
    private void getHotDataRequest() {
        Observable.zip(
                getBaseDataObservable(Urls.QUERY_THEME_DAY),
                getBaseDataObservable(Urls.QUERY_THREE_SESSIONS),
                getBaseDataObservable(Urls.QUERY_DEMOCRATIC),
                getBaseDataObservable(Urls.QUERY_LIFE_MEETING),
                getBaseDataObservable(Urls.QUERY_OTHERS),
                new Func5<BaseData<ThemePartyBean>,
                        BaseData<ThemePartyBean>,
                        BaseData<ThemePartyBean>,
                        BaseData<ThemePartyBean>,
                        BaseData<ThemePartyBean>,
                        List<StudyListItem>>() {
                    @Override
                    public List<StudyListItem> call(
                            BaseData<ThemePartyBean> ztdtList,
                            BaseData<ThemePartyBean> shykList,
                            BaseData<ThemePartyBean> mzpyList,
                            BaseData<ThemePartyBean> zzshhList,
                            BaseData<ThemePartyBean> moreList) {
                        List<StudyListItem> newList = new ArrayList<>();
                        StudyListItem studyListItem = null;

                        getStudyItemList(ztdtList, newList, RefreshRecyclerFragment.TYPE_ZTDR);
                        getStudyItemList(shykList, newList, RefreshRecyclerFragment.TYPE_SHYK);
                        getStudyItemList(mzpyList, newList, RefreshRecyclerFragment.TYPE_MZPY);
                        getStudyItemList(zzshhList, newList, RefreshRecyclerFragment.TYPE_ZZSHH);
                        getStudyItemList(moreList, newList, RefreshRecyclerFragment.TYPE_MORE);
                        return newList;
                    }
                })
                .subscribe(new RxSubscriber<List<StudyListItem>>() {
                    @Override
                    public void onSuccess(List<StudyListItem> newsBeans) {
                        hideSkeletonScreen();
                        organizingLifeAdapter.replaceData(newsBeans);
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        hideSkeletonScreen();
                        ToastHelper.showShort("onError:" + responeThrowable.getCause().getMessage());
                    }
                });
    }

    public void getStudyItemList(BaseData<ThemePartyBean> ztdtList, List<StudyListItem> newList, String title) {
        StudyListItem studyListItem = null;
        if (BaseData.SUCCESS.equals(ztdtList.result) && ztdtList.dataList.size() > 0) {
            studyListItem = new StudyListItem(new StudyHeadBean(title, R.mipmap.ic_study_hot));
            newList.add(studyListItem);
            for (int i = 0; i < ztdtList.dataList.size(); i++) {
                ThemePartyBean themePartyBean = ztdtList.dataList.get(i);
                themePartyBean.itemTyep = title;
                newList.add(new StudyListItem(themePartyBean));
            }
        }
    }

    /**
     * @Query("pageNum") int pageNum,
     * @Query("pageSize") int pageSize,
     * @Query("accountId") int accountId,
     * @Query("unitId") int unitId,
     * @Query("year") int year
     */
    private Observable<BaseData<ThemePartyBean>> getBaseDataObservable(String url) {
        Map<String, Integer> queryMap = new HashMap<>();
        queryMap.put("pageNum", 1);
        queryMap.put("pageSize", 3);
        queryMap.put("accountId", Integer.valueOf(accountBean.id));
        queryMap.put("unitId", unitId);
        queryMap.put("year", requestYear);
        queryMap.put("state", 1);

        return HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryThemeDays(url, queryMap)
                .compose(this.bindToLifecycle()).compose(this.bindToLifecycle())
                .compose(new CustomCompose());
    }

    private SkeletonScreen skeletonScreen;

    /**
     * 显示骨架图
     */
    private void showSkeletonSceen(RecyclerView.Adapter adapter) {
        skeletonScreen = Skeleton.bind(rvList)
                .adapter(adapter)
                .shimmer(true)
                .angle(20)
                .frozen(false)
                .duration(1200)
                .count(10)
                .load(R.layout.item_skeleton_news)
                .show(); //default count is 10
    }

    /**
     * 隐藏骨架图
     */
    private void hideSkeletonScreen() {
        if (null != skeletonScreen) {
            skeletonScreen.hide();
            skeletonScreen = null;
        }
    }

}
