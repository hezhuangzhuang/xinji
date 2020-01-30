package com.zxwl.xinji.fragment;


import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zxwl.commonlibrary.BaseFragment;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.bean.response.StatisticsBean;
import com.zxwl.network.bean.response.TsbcBean;
import com.zxwl.network.bean.response.WxyBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.func.RetryWithDelay;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.activity.ScreenViewPagerActivity;
import com.zxwl.xinji.activity.VillageListActivity;
import com.zxwl.xinji.activity.WxyDetailsActivity;
import com.zxwl.xinji.adapter.StatisticsConfAdapter;
import com.zxwl.xinji.adapter.ThemePatryCurrencyAdapter;
import com.zxwl.xinji.adapter.TsbcAdapter;
import com.zxwl.xinji.utils.Constant;
import com.zxwl.xinji.utils.PreferenceUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static com.zxwl.xinji.activity.RefreshRecyclerActivity.TYPE_WXY;

/**
 * 2019年统计
 */
public class StatisticsFragment extends BaseFragment implements View.OnClickListener {
    private SmartRefreshLayout refreshLayout;
    private RecyclerView rvList;

    public static final String TITLE = "TITLE";
    public static final String INDEX = "INDEX";

    private String title;
    private int index = -1;

    public static final String TYPE_XXJY = "工作动态";//

    public static final String TYPE_SHYK = "三会一课";//

    public static final String TYPE_ZZSH = "其他组织生活";//

    public static final String TYPE_ZYFW = "志愿服务";

    public static final String TYPE_NO_CLAIM = "待认领";

    public static final String TYPE_IS_CLAIM = "已认领";//

    public static final String TYPE_JTJJ = "集体经济";
    public static final String TYPE_RYBZ = "荣誉表彰";
    public static final String TYPE_LDGZ = "亮点工作";

    private View emptyView;
    private View errorView;

    private String requestUrl;

    private int pageNum = 0;
    private int PAGE_SIZE = 10;

    //组织生活-查询类型
    private int lifeType = 1;

    /**
     * 数据统计的适配器
     */
    private StatisticsConfAdapter statisticsConfAdapter;

    /**
     * 微心愿
     */
    private ThemePatryCurrencyAdapter currencyAdapter;

    /*图说本村*/
    private TsbcAdapter tsbcAdapter;

    private LoginBean.AccountBean accountBean;

    private String startTime;

    private String endTime;

    private LinearLayout llType;
    private TextView tvZtdr;
    private TextView tvZzshh;
    private TextView tvMzpy;
    private TextView tvMore;

    private List<View> typeViews;

    //请求的单位id
    private int requestUnitId = 1;

    //微心愿类型,1:已认领，2:待认领
    private int wxyType;

    /**
     * 排序
     * 默认降序
     * 降序:1
     * 升序：2
     */
    private int total = 1;

    public StatisticsFragment() {
    }

    public static StatisticsFragment newInstance(String title, int index) {
        StatisticsFragment fragment = new StatisticsFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putInt(INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View inflateContentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    protected void findViews(View view) {
        refreshLayout = (SmartRefreshLayout) view.findViewById(R.id.refreshLayout);
        rvList = (RecyclerView) view.findViewById(R.id.rv_list);

        emptyView = getLayoutInflater().inflate(R.layout.include_empty, (ViewGroup) rvList.getParent(), false);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.autoRefresh();
            }
        });

        errorView = getLayoutInflater().inflate(R.layout.include_error, (ViewGroup) rvList.getParent(), false);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.autoRefresh();
            }
        });

        llType = (LinearLayout) view.findViewById(R.id.ll_type);
        tvZtdr = (TextView) view.findViewById(R.id.tv_ztdr);
        tvZzshh = (TextView) view.findViewById(R.id.tv_zzshh);
        tvMzpy = (TextView) view.findViewById(R.id.tv_mzpy);
        tvMore = (TextView) view.findViewById(R.id.tv_more);
    }

    @Override
    protected void addListeners() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData(1);
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                getData(pageNum + 1);
            }
        });

        refreshLayout.setEnableLoadMore(false);

        tvZtdr.setOnClickListener(this);
        tvZzshh.setOnClickListener(this);
        tvMzpy.setOnClickListener(this);
        tvMore.setOnClickListener(this);
    }

    @Override
    protected void init() {
        title = getArguments().getString(TITLE);
        index = getArguments().getInt(INDEX);

        if (isLogin()) {
            accountBean = PreferenceUtil.getUserInfo(getActivity());
            requestUnitId = accountBean.unitId;
        } else {
            if (TYPE_IS_CLAIM.equals(title) || TYPE_NO_CLAIM.equals(title)) {
                if (isLogin()) {
                    accountBean = PreferenceUtil.getUserInfo(getActivity());
                    requestUnitId = accountBean.unitId;
                } else {
                    requestUnitId = 1;
                }
            }
        }

        initUrl();

        initAdapter();

        getData(1);
    }

    /**
     * 是否登录
     *
     * @return
     */
    private boolean isLogin() {
        return PreferenceUtil.getBoolean(Constant.AUTO_LOGIN, false);
    }

    private void initUrl() {
        switch (title) {
            //三会一课
            case TYPE_SHYK:
                llType.setVisibility(View.GONE);
                requestUrl = "threeLessonAction_queryThreelessonStatis.action";
                break;

            //两学一做
            //学习教育
            case TYPE_XXJY:
                llType.setVisibility(View.GONE);
                requestUrl = "educStatisAction_queryEducstatis.action";
                break;

            //组织生活
            case TYPE_ZZSH:
                llType.setVisibility(View.VISIBLE);
                typeViews = new ArrayList<>();
                typeViews.add(tvZtdr);
                typeViews.add(tvZzshh);
                typeViews.add(tvMzpy);
                typeViews.add(tvMore);
                initTypeSelect();
                requestUrl = "lifeStatisAction_queryLifestatison.action";
                break;

            //帮扶活动
            case TYPE_ZYFW:
                llType.setVisibility(View.GONE);
                requestUrl = "activityStatisAction_queryActivityStatis.action";
                break;

            //已认领
            case TYPE_IS_CLAIM:
                wxyType = 1;
                requestUrl = "tinywishAction_queryTinywishList.action";
                break;

            //未认领
            case TYPE_NO_CLAIM:
                wxyType = 0;
                requestUrl = "tinywishAction_queryTinywishList.action";
                break;
        }
    }

    /**
     * 初始化组织生活选择
     */
    private void initTypeSelect() {
        for (int i = 0; i < typeViews.size(); i++) {
            TextView view = (TextView) typeViews.get(i);
            view.setBackgroundResource(R.color.tran);
            view.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_999));
            TextPaint tp = view.getPaint();
            tp.setFakeBoldText(false);
        }

        TextView view = (TextView) typeViews.get(lifeType - 1);
        view.setBackgroundResource(R.drawable.shape_bg_fff5e5_20);
        view.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_ff9d04));

        TextPaint tp = view.getPaint();
        tp.setFakeBoldText(true);
    }

    /**
     * 获得数据的网络请求
     */
    private void getData(int pageNum) {
        switch (title) {
            //三会一课
            case TYPE_SHYK:
                //两学一做
                //学习教育
            case TYPE_XXJY:
                //组织生活
            case TYPE_ZZSH:
                //帮扶活动
            case TYPE_ZYFW:
                getSjtjData(pageNum);
                break;

            //微心愿
            case TYPE_IS_CLAIM:
            case TYPE_NO_CLAIM:
                getWxyData(pageNum);
                break;

            case TYPE_JTJJ:
            case TYPE_RYBZ:
            case TYPE_LDGZ:
                getTsbcData(pageNum);
                break;
        }
    }

    /**
     * 获取微心愿的数据
     */
    private void getWxyData(int pageNum) {
        HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryWxys(pageNum,
                        PAGE_SIZE,
                        requestUnitId,
                        wxyType)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<WxyBean>>() {
                    @Override
                    public void onSuccess(BaseData<WxyBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<WxyBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                } else {
                                    setPageNum(pageNum);
                                }

                                //刷新数据
                                initListBeans(dataList, currencyAdapter);
                            } else {
                                if (1 == pageNum) {
                                    currencyAdapter.replaceData(new ArrayList<>());
                                    currencyAdapter.setEmptyView(emptyView);
                                } else {
                                    refreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
                                }
                                hideSkeletonScreen();
                            }

                            //结束刷新
                            finishRefresh(pageNum, true);
                        } else {
                            //隐藏骨架图
                            hideSkeletonScreen();

                            //结束刷新
                            finishRefresh(pageNum, false);

                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        //隐藏骨架图
                        hideSkeletonScreen();
                        finishRefresh(pageNum, false);
                        if (1 == pageNum) {
                            currencyAdapter.setEmptyView(errorView);
                        }
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    private void getSjtjData(int pageNum) {
        getObservable(pageNum)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<StatisticsBean>>() {
                    @Override
                    public void onSuccess(BaseData<StatisticsBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<StatisticsBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                } else {
                                    setPageNum(pageNum);
                                }

                                //刷新数据
                                initListBeans(dataList, statisticsConfAdapter);
                            } else {
                                if (1 == pageNum) {
                                    statisticsConfAdapter.replaceData(new ArrayList<>());
                                    statisticsConfAdapter.setEmptyView(emptyView);
                                } else {
                                    refreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
                                }
                                hideSkeletonScreen();
                            }

                            //结束刷新
                            finishRefresh(pageNum, true);
                        } else {
                            if (1 == pageNum) {
                                statisticsConfAdapter.setEmptyView(errorView);
                            }

                            //隐藏骨架图
                            hideSkeletonScreen();

                            //结束刷新
                            finishRefresh(pageNum, false);

                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        //隐藏骨架图
                        hideSkeletonScreen();

                        finishRefresh(pageNum, false);

                        if (1 == pageNum) {
                            statisticsConfAdapter.setEmptyView(errorView);
                        }
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    /**
     * 图说本村
     */
    private void getTsbcData(int pageNum) {
        HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryTsbcList(requestUrl, pageNum, PAGE_SIZE, requestUnitId)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<TsbcBean>>() {
                    @Override
                    public void onSuccess(BaseData<TsbcBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<TsbcBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                } else {
                                    setPageNum(pageNum);
                                }

                                //刷新数据
                                initListBeans(dataList, tsbcAdapter);
                            } else {
                                if (1 == pageNum) {
                                    tsbcAdapter.replaceData(new ArrayList<>());
                                    tsbcAdapter.setEmptyView(emptyView);
                                } else {
                                    //设置之后，将不会再触发加载事件
                                    refreshLayout.finishLoadMoreWithNoMoreData();
                                }
                                hideSkeletonScreen();
                            }

                            //结束刷新
                            finishRefresh(pageNum, true);
                        } else {
                            //隐藏骨架图
                            hideSkeletonScreen();

                            //结束刷新
                            finishRefresh(pageNum, false);
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        //隐藏骨架图
                        hideSkeletonScreen();

                        finishRefresh(pageNum, false);

                        if (1 == pageNum) {
                            tsbcAdapter.setEmptyView(errorView);
                        }
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    private Observable<BaseData<StatisticsBean>> getObservable(int pageNum) {
        if (TYPE_XXJY.equals(title)) {
            return getXxjy(pageNum);
        } else if (TYPE_ZZSH.equals(title)) {
            return getZzsh(pageNum);
        } else {
            return getXxjy(pageNum);
        }
    }

    /**
     * 获取学习教育数据
     *
     * @param pageNum
     * @return
     */
    private Observable<BaseData<StatisticsBean>> getXxjy(int pageNum) {
        return HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryThreelessonStatis(
                        requestUrl,
                        pageNum,
                        PAGE_SIZE,
                        requestUnitId,
                        startTime,
                        endTime,
                        total)
                .retryWhen(new RetryWithDelay(3, 300));
    }

    /**
     * 获取组织生活数据
     *
     * @param pageNum
     * @return
     */
    private Observable<BaseData<StatisticsBean>> getZzsh(int pageNum) {
        return HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryLifestatison(pageNum,
                        PAGE_SIZE,
                        requestUnitId,
                        lifeType,
                        startTime,
                        endTime, total)
                .retryWhen(new RetryWithDelay(3, 300));
    }

    /**
     * 三会一课
     */
    private void initAdapter() {
        switch (title) {
            //三会一课
            case TYPE_SHYK:
                //两学一做
                //学习教育
            case TYPE_XXJY:
                //组织生活
            case TYPE_ZZSH:
                //帮扶活动
            case TYPE_ZYFW:
                initSjtjAdapter();
                break;

            //微心愿
            case TYPE_IS_CLAIM:
            case TYPE_NO_CLAIM:
                initCurrencyAdapter(TYPE_WXY);
                break;

            //集体经济
            case TYPE_JTJJ:
                requestUrl = Urls.QUERY_JTJJ_LIST;

                initTsbcAdapter();
                showSkeletonSceen(tsbcAdapter);
                break;

            case TYPE_LDGZ:
                requestUrl = Urls.QUERY_LDGZ_LIST;

                initTsbcAdapter();
                showSkeletonSceen(tsbcAdapter);
                break;

            case TYPE_RYBZ:
                requestUrl = Urls.QUERY_RYBZ_LIST;

                initTsbcAdapter();
                showSkeletonSceen(tsbcAdapter);
                break;
        }
    }

    private void initSjtjAdapter() {
        statisticsConfAdapter = new StatisticsConfAdapter(R.layout.item_statistics_conf, new ArrayList<>());
        statisticsConfAdapter.setType(title);
        statisticsConfAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvList.setAdapter(statisticsConfAdapter);

        showSkeletonSceen(statisticsConfAdapter);
    }

    /**
     * 通用适配器
     */
    private void initCurrencyAdapter(String title) {
        currencyAdapter = new ThemePatryCurrencyAdapter(R.layout.item_theme_party_day, new ArrayList<>());
        currencyAdapter.setType(title);
        currencyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (title) {
                    //微心愿
                    case TYPE_WXY:
                        WxyBean wxyBean = (WxyBean) currencyAdapter.getItem(position);
                        WxyDetailsActivity.startActivity(getActivity(), wxyBean);
                        break;
                }
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvList.setAdapter(currencyAdapter);

        showSkeletonSceen(currencyAdapter);
    }

    private List<LocalMedia> selectListImage = new ArrayList<>();
    ;

    /**
     * 初始化图说本村适配器
     */
    public void initTsbcAdapter() {
        tsbcAdapter = new TsbcAdapter(R.layout.item_tsbc, new ArrayList<>());
        tsbcAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                selectListImage.clear();
                TsbcBean item = tsbcAdapter.getItem(position);

                addLocalMedia(item.pic1);
                addLocalMedia(item.pic2);
                addLocalMedia(item.pic3);

                switch (view.getId()) {
                    case R.id.iv_one:
                        // 预览图片 可自定长按保存路径
                        PictureSelector.create(getActivity()).themeStyle(R.style.picture_default_style).openExternalPreview(0, selectListImage);
                        break;

                    case R.id.iv_two:
                        // 预览图片 可自定长按保存路径
                        PictureSelector.create(getActivity()).themeStyle(R.style.picture_default_style).openExternalPreview(1, selectListImage);
                        break;

                    case R.id.iv_three:
                        // 预览图片 可自定长按保存路径
                        PictureSelector.create(getActivity()).themeStyle(R.style.picture_default_style).openExternalPreview(2, selectListImage);
                        break;
                }
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvList.setAdapter(tsbcAdapter);
    }

    /**
     * 添加图片
     *
     * @param url
     */
    private void addLocalMedia(String url) {
        LocalMedia localMedia = null;
        if (!TextUtils.isEmpty(url)) {
            localMedia = new LocalMedia();
            localMedia.setPath(url);
            selectListImage.add(localMedia);
        }
    }


    /**
     * 设置数据
     */
    private void initListBeans(List newsList, BaseQuickAdapter adapter) {
        //隐藏骨架图
        hideSkeletonScreen();

        refreshLayout.setEnableLoadMore(true);

        if (1 == pageNum) {
            //刷新加载更多状态
            refreshLayout.resetNoMoreData();
            adapter.replaceData(newsList);
        } else {
            adapter.addData(newsList);
        }
    }

    /**
     * 设置页数
     *
     * @param pageNum
     */
    private void setPageNum(int pageNum) {
        this.pageNum = pageNum;
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

    /**
     * 关闭动画
     *
     * @param pageNum
     */
    private void finishRefresh(int pageNum, boolean success) {
        if (1 == pageNum) {
            refreshLayout.finishRefresh(success);
            refreshLayout.setEnableFooterFollowWhenNoMoreData(true);
        } else {
            refreshLayout.finishLoadMore(success);
        }
    }

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
            //更新单位
            case Messages.REFRESH_UNIT_ID:
                requestUnitId = (int) eventMessage.t;

                refreshLayout.autoRefresh();
                break;

            //更新排序
            case Messages.REFRESH_TOTAL:
                total = (int) eventMessage.t;

                refreshLayout.autoRefresh();
                break;

            //更新时间
            case Messages.REFRESH_TIME:
                String[] times = eventMessage.succeed.split(",");

                startTime = times[0];
                endTime = times[1];

                refreshLayout.autoRefresh();
                break;

            //更新微心愿
            case Messages.REFRESH_WXY:
                refreshLayout.autoRefresh();
                break;

            //更新党建地图其他
            case Messages.REFRESH_MAP_MORE:
                refreshTitle = (String) eventMessage.succeed;
                if (refreshTitle.equals(title)) {
                    refreshLayout.autoRefresh();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ztdr:
                lifeType = 1;
                break;

            case R.id.tv_zzshh:
                lifeType = 2;
                break;

            case R.id.tv_mzpy:
                lifeType = 3;
                break;

            case R.id.tv_more:
                lifeType = 4;
                break;
        }
        initTypeSelect();
        refreshLayout.autoRefresh();
    }

    //刷新的标题
    private String refreshTitle;

    /**
     * 如果是与ViewPager一起使用，调用的是setUserVisibleHint
     *
     * @param isVisibleToUser 是否显示出来了
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //返回true代表可见
        if (getUserVisibleHint()) {
            register();

            if (mContext instanceof ScreenViewPagerActivity) {
                ScreenViewPagerActivity activity = (ScreenViewPagerActivity) mContext;
                if (requestUnitId != activity.currentUnitId || total != activity.total || activity.updateTime || activity.refreshWxy) {
                    requestUnitId = activity.currentUnitId;

                    total = activity.total;

                    startTime = activity.startTime;
                    endTime = activity.endTime;

                    activity.refreshWxy = false;
                    refreshLayout.autoRefresh();
                }
            } else if (mContext instanceof VillageListActivity) {
                VillageListActivity activity = (VillageListActivity) mContext;
                if (requestUnitId != activity.requestUnitId || title.equals(refreshTitle)) {
                    requestUnitId = activity.requestUnitId;

                    if (title.equals(refreshTitle)) {
                        refreshTitle = "";
                    }

                    refreshLayout.autoRefresh();
                }
            }
        } else {
            unRegister();
        }
    }

}
