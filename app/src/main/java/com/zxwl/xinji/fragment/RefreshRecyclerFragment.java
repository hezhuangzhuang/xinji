package com.zxwl.xinji.fragment;


import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.zxwl.commonlibrary.BaseLazyFragment;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.commonlibrary.utils.DisplayUtil;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.commonlibrary.utils.WeekDayUtil;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.bean.response.DepartmentBean;
import com.zxwl.network.bean.response.DocumentBean;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.bean.response.NewsBean;
import com.zxwl.network.bean.response.ThemePartyBean;
import com.zxwl.network.bean.response.TsbcBean;
import com.zxwl.network.bean.response.WxyBean;
import com.zxwl.network.bean.response.XwqlqdBean;
import com.zxwl.network.bean.response.ZtjyBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.activity.ContentDetailsActivity;
import com.zxwl.xinji.activity.DocumentDetailsActivity;
import com.zxwl.xinji.activity.OrganizationDocumentsActivity;
import com.zxwl.xinji.activity.RefreshRecyclerActivity;
import com.zxwl.xinji.activity.WxyDetailsActivity;
import com.zxwl.xinji.adapter.ConfAdapter;
import com.zxwl.xinji.adapter.DocumentAdapter;
import com.zxwl.xinji.adapter.NewsListAdapter;
import com.zxwl.xinji.adapter.ThemePatryCurrencyAdapter;
import com.zxwl.xinji.adapter.TsbcAdapter;
import com.zxwl.xinji.adapter.item.NewsListItem;
import com.zxwl.xinji.bean.ContentDetailsBean;
import com.zxwl.xinji.utils.Constant;
import com.zxwl.xinji.utils.PreferenceUtil;
import com.zxwl.xinji.widget.ScreenCityPopupWindow;
import com.zxwl.xinji.widget.ScreenTimePopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;

/**
 * 主题党日
 */
public class RefreshRecyclerFragment extends BaseLazyFragment implements View.OnClickListener {
    private SmartRefreshLayout refreshLayout;
    private RecyclerView rvList;

    private ThemePatryCurrencyAdapter currencyAdapter;

    private ConfAdapter confAdapter;

    private DocumentAdapter documentAdapter;

    private NewsListAdapter newsListAdapter;

    //图说本村适配器
    private TsbcAdapter tsbcAdapter;

    public static final String TITLE = "TITLE";
    private String title;

    //单位id
    public static final String UNIT_ID = "UNIT_ID";
    private int unitId;//单位id

    //请求年份
    public static final String REQUEST_YEAR = "REQUEST_YEAR";

    public static final String TYPE_ZTDR = "主题党日";
    public static final String TYPE_SHYK = "三会一课";
    public static final String TYPE_MZPY = "民主评议";
    public static final String TYPE_ZZSHH = "组织生活会";
    public static final String TYPE_MORE = "其他";

    public static final String TYPE_DJZX = "组织工作";//之前未党建资讯
    public static final String TYPE_XCDT = "乡村动态";
    public static final String TYPE_DXJY = "先进典型";
    public static final String TYPE_TSZS = "他山之石";

    public static final String TYPE_TJ = "推荐";
    public static final String TYPE_DSBC = "党史博采";

    public static final String TYPE_WXY = "微心愿";

    public static final String TYPE_DWGZL = "党务工作类";

    public static final String TYPE_CWGZL = "村务工作类";
    public static final String TYPE_ZDSXJC = "重大事项决策";
    public static final String TYPE_CWGLSX = "财务管理事项";
    public static final String TYPE_YGCWSX = "阳光村务事项";

    public static final String TYPE_BMFWL = "便民服务类";
    public static final String TYPE_JZJZKWSPSX = "救助救灾款物审批事项";
    public static final String TYPE_SNFWGLSX = "涉农服务管理事项";
    public static final String TYPE_JHSYFWSX = "计划生育服务事项";
    public static final String TYPE_QTSX = "其他事项";

    public static final String TYPE_TSBC = "图说本村";
    public static final String TYPE_RYBZ = "荣誉表彰";
    public static final String TYPE_LDGZ = "亮点工作";


    private int iconRes;

    private int pageNum = 0;
    private int PAGE_SIZE = 10;

    private View emptyView;
    private View errorView;

    //主题教育的类型
    private int eductype = -1;

    private LinearLayout llScreen;
    private TextView tvTime;
    private TextView tvType;
    private TextView tvAddress;

    private LinearLayout llYear;

    private LoginBean.AccountBean accountBean;

    //微心愿的类型
    private int wxyType;

    private RelativeLayout rlScreen;
    private TextView tvCity;
    private TextView tvScreen;

    //是否是管理员
    private boolean checkAdmin = false;

    private int type;
    private int type2;

    //默认id为1
    private int requestUnitId = 1;

    private String requestUrl;

    public RefreshRecyclerFragment() {
    }

    public static RefreshRecyclerFragment newInstance(String title, int unitId) {
        RefreshRecyclerFragment fragment = new RefreshRecyclerFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putInt(UNIT_ID, unitId);
        fragment.setArguments(args);
        return fragment;
    }

    public static RefreshRecyclerFragment newInstance(String title, int unitId, int requestYear) {
        RefreshRecyclerFragment fragment = new RefreshRecyclerFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putInt(UNIT_ID, unitId);
        args.putInt(REQUEST_YEAR, requestYear);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View inflateContentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.refresh_layout, container, false);
    }

    @Override
    protected void findViews(View view) {
        refreshLayout = (SmartRefreshLayout) view.findViewById(R.id.refreshLayout);
        rvList = (RecyclerView) view.findViewById(R.id.rv_list);

        llScreen = (LinearLayout) view.findViewById(R.id.ll_screen);
        tvTime = (TextView) view.findViewById(R.id.tv_time);
        tvType = (TextView) view.findViewById(R.id.tv_type);
        tvAddress = (TextView) view.findViewById(R.id.tv_address);

        rlScreen = (RelativeLayout) view.findViewById(R.id.rl_screen);
        tvCity = (TextView) view.findViewById(R.id.tv_city);
        tvScreen = (TextView) view.findViewById(R.id.tv_screen);

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

        tvScreen.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        Integer year = null;
        try {
            unitId = getArguments().getInt(UNIT_ID);
            title = getArguments().getString(TITLE);
            requestYear = getArguments().getInt(REQUEST_YEAR);
            if (title.contains("年")) {
                title = title.substring(0, title.length() - 1);
                year = Integer.valueOf(title);
            }
        } catch (Exception e) {
            if (isLogin()) {
                if (OrganizationDocumentsActivity.NEWS_UNIT_ID != unitId && null == year) {
                    accountBean = PreferenceUtil.getUserInfo(getActivity());
                }
            }
        } finally {
            if (isLogin()) {
                if (OrganizationDocumentsActivity.NEWS_UNIT_ID != unitId && null == year) {
                    accountBean = PreferenceUtil.getUserInfo(getActivity());
                }
            }
        }

        initAdapter();

        //判断是否需要查询年份
        if (isReuqestYear()) {
            if (3 == accountBean.level) {
                tvAddress.setVisibility(View.GONE);
            }
            getData(1);
        } else {
            getData(1);
        }

        register();
    }

    private boolean isLogin() {
        return PreferenceUtil.getBoolean(Constant.AUTO_LOGIN, false);
    }

    private boolean isGzzt() {
        return TYPE_DJZX.equals(title) ||
                TYPE_XCDT.equals(title) ||
                TYPE_DXJY.equals(title) ||
                TYPE_TSZS.equals(title);
    }

    private int getCurrentYear() {
        Calendar cd = Calendar.getInstance();
        return cd.get(Calendar.YEAR);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegister();
    }

    private String requetYearUrl = "";

    private void initAdapter() {
        switch (title) {
            case TYPE_ZTDR:
                requetYearUrl = Urls.QUERY_THEME_DAY_YEAR;

                initConfAdapter();
                confAdapter.setZtdr(true);
                break;

            case TYPE_SHYK:
                requetYearUrl = Urls.QUERY_THREE_SESSIONS_YEAR;

                initConfAdapter();
                confAdapter.setZtdr(false);
                break;

            case TYPE_MZPY:
                requetYearUrl = Urls.QUERY_DEMOCRATIC_YEAR;

                initCurrencyAdapter(TYPE_MZPY);
                iconRes = R.mipmap.ic_home_mzpy;
                break;

            case TYPE_ZZSHH:
                requetYearUrl = Urls.QUERY_LIFE_MEETING_YEAR;
                initCurrencyAdapter(TYPE_ZZSHH);
                iconRes = R.mipmap.ic_item_zzshh;
                break;

            case TYPE_MORE:
                requetYearUrl = Urls.QUERY_OTHERS_YEAR;

                initCurrencyAdapter(TYPE_MORE);
                iconRes = R.mipmap.ic_item_more;
                break;

            //党建资讯
            case TYPE_DJZX:
                eductype = 1;
                initCurrencyAdapter(TYPE_DJZX);
                break;

            //乡村动态
            case TYPE_XCDT:
                eductype = 2;
                initCurrencyAdapter(TYPE_XCDT);
                break;

            //典型经验
            case TYPE_DXJY:
                eductype = 3;
                initCurrencyAdapter(TYPE_DXJY);
                break;

            //他山之石
            case TYPE_TSZS:
                eductype = 4;
                initCurrencyAdapter(TYPE_TSZS);
                break;

            //推荐
            case TYPE_TJ:
                initNewsListAdapter();
                break;

            //党史博采
            case TYPE_DSBC:
                initNewsListAdapter();
                break;

            //微心愿
            case TYPE_WXY:
                wxyType = unitId;
                rlScreen.setVisibility(View.VISIBLE);
                initAccountBean();

                rlScreen.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));

                initCurrencyAdapter(TYPE_WXY);
                break;

            //党务工作类
            case TYPE_DWGZL:
                type = 1;
                initCurrencyAdapter(RefreshRecyclerActivity.TYPE_XWQLQD);
                break;

            case TYPE_ZDSXJC:
                type = 2;
                type2 = 4;
                initCurrencyAdapter(RefreshRecyclerActivity.TYPE_XWQLQD);
                break;

            case TYPE_CWGLSX:
                type = 2;
                type2 = 5;
                initCurrencyAdapter(RefreshRecyclerActivity.TYPE_XWQLQD);
                break;

            case TYPE_YGCWSX:
                type = 2;
                type2 = 6;
                initCurrencyAdapter(RefreshRecyclerActivity.TYPE_XWQLQD);
                break;

            case TYPE_JZJZKWSPSX:
                type = 3;
                type2 = 7;
                initCurrencyAdapter(RefreshRecyclerActivity.TYPE_XWQLQD);
                break;

            case TYPE_SNFWGLSX:
                type = 3;
                type2 = 8;
                initCurrencyAdapter(RefreshRecyclerActivity.TYPE_XWQLQD);
                break;

            case TYPE_JHSYFWSX:
                type = 3;
                type2 = 9;
                initCurrencyAdapter(RefreshRecyclerActivity.TYPE_XWQLQD);
                break;

            //其它事项
            case TYPE_QTSX:
                type = 3;
                type2 = 10;
                initCurrencyAdapter(RefreshRecyclerActivity.TYPE_XWQLQD);
                break;

            //亮点工作
            case TYPE_LDGZ:
                requestUrl = Urls.QUERY_LDGZ_LIST;
                initTsbcAdapter();
                break;

            case TYPE_RYBZ:
                requestUrl = Urls.QUERY_RYBZ_LIST;
                initTsbcAdapter();
                break;

            case TYPE_TSBC:
                requestUrl = Urls.QUERY_TSBC_LIST;
                initTsbcAdapter();
                break;

            default:
                //组工文件
                initDocumentAdapter();
                break;
        }
    }

    private void initAccountBean() {
        accountBean = PreferenceUtil.getUserInfo(getActivity());
        if (null != accountBean) {
            checkAdmin = (1 == accountBean.checkAdmin);

            tvCity.setText(accountBean.flag);

            //不等于微心愿才这样取值
            if (!TYPE_WXY.equals(title)) {
                //行政级别,1：市级,2：乡镇,3：村级
                if (2 == accountBean.level) {
                    townshipId = Integer.valueOf(accountBean.townId);
                } else if (3 == accountBean.level) {
                    townshipId = Integer.valueOf(accountBean.townId);
                    villageId = Integer.valueOf(accountBean.villageId);
                }
            }
        }
    }

    private void showScreenLayout(boolean showType) {
        llScreen.setVisibility(View.VISIBLE);
        tvTime.setOnClickListener(this);
        tvAddress.setOnClickListener(this);

        if (showType) {
            tvType.setVisibility(View.VISIBLE);
            tvType.setOnClickListener(this);
        }
    }

    /**
     * 如果是主题党日就查询年份
     *
     * @return
     */
    private boolean isReuqestYear() {
        switch (title) {
            case TYPE_ZTDR:
            case TYPE_SHYK:
            case TYPE_MZPY:
            case TYPE_ZZSHH:
            case TYPE_MORE:
                return true;

            default:
                return false;
        }
    }

    private List<String> yearList;

    private void getData(int pageNum) {
        switch (title) {
            case TYPE_ZTDR:
                getZtdrData(pageNum, Urls.QUERY_THEME_DAY);
                break;

            case TYPE_SHYK:
                getZtdrData(pageNum, Urls.QUERY_THREE_SESSIONS);
                break;

            case TYPE_MZPY:
                getZtdrData(pageNum, Urls.QUERY_DEMOCRATIC);
                break;

            case TYPE_ZZSHH:
                getZtdrData(pageNum, Urls.QUERY_LIFE_MEETING);
                break;

            case TYPE_MORE:
                getZtdrData(pageNum, Urls.QUERY_OTHERS);
                break;

            case TYPE_TSZS:
            case TYPE_XCDT:
            case TYPE_DXJY:
            case TYPE_DJZX:
                getZtjyData(pageNum);
                break;

            case TYPE_TJ:
                columnId = 1;
                getStudyNews(pageNum);
                break;

            case TYPE_DSBC:
                columnId = 3;
                getStudyNews(pageNum);
                break;

            case TYPE_WXY:
                getWxyData(pageNum);
                break;

            case TYPE_LDGZ:
            case TYPE_RYBZ:
            case TYPE_TSBC:
                getTsbcData(pageNum);
                break;

            case TYPE_DWGZL:
            case TYPE_ZDSXJC:
            case TYPE_CWGLSX:
            case TYPE_YGCWSX:
            case TYPE_JZJZKWSPSX:
            case TYPE_SNFWGLSX:
            case TYPE_JHSYFWSX:
            case TYPE_QTSX:
                getXwqlqdData(pageNum);
                break;

            default:
                getDocumentData(pageNum);
                break;
        }
    }

    //    获取理论学习接口,1：推荐，3:党史博采
    private int columnId = 1;

    /**
     * 查询理论学习列表
     *
     * @param pageNum
     */
    private void getStudyNews(int pageNum) {
        HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryStudys(pageNum, PAGE_SIZE, columnId)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<NewsBean>>() {
                    @Override
                    public void onSuccess(BaseData<NewsBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<NewsBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                } else {
                                    setPageNum(pageNum);
                                }

                                List<NewsListItem> newsListItems = new ArrayList<>();

                                for (int i = 0; i < dataList.size(); i++) {
                                    newsListItems.add(new NewsListItem(dataList.get(i)));
                                }

                                //刷新数据
                                initListBeans(newsListItems, newsListAdapter);
                            } else {
                                if (1 == pageNum) {
                                    newsListAdapter.replaceData(new ArrayList<>());
                                    newsListAdapter.setEmptyView(emptyView);
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
                            newsListAdapter.setEmptyView(errorView);
                        }
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    /**
     * 查询组工文件
     *
     * @param pageNum
     */
    private void getDocumentData(int pageNum) {
        HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryDocuments(pageNum, PAGE_SIZE, unitId, Integer.valueOf(title))
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<DocumentBean>>() {
                    @Override
                    public void onSuccess(BaseData<DocumentBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<DocumentBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                } else {
                                    setPageNum(pageNum);
                                }
                                //刷新数据
                                initListBeans(dataList, documentAdapter);
                            } else {
                                if (1 == pageNum) {
                                    documentAdapter.replaceData(new ArrayList<>());
                                    documentAdapter.setEmptyView(emptyView);
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
                            documentAdapter.setEmptyView(errorView);
                        }
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    private int requestYear;//年份

    private int shykType = 1;//三会一课的类型

    /**
     * 主题党日
     */
    private void getZtdrData(int pageNum, String url) {
        getBaseDataObservable(pageNum, url)
                .compose(this.bindToLifecycle())
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<ThemePartyBean>>() {
                    @Override
                    public void onSuccess(BaseData<ThemePartyBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<ThemePartyBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                } else {
                                    setPageNum(pageNum);
                                }
                                //如果是三会一课
                                if (TYPE_SHYK.equals(title)) {
                                    //刷新数据
                                    initListBeans(dataList, confAdapter);
                                } else if (TYPE_ZTDR.equals(title)) {
                                    //刷新数据
                                    initListBeans(dataList, confAdapter);
                                } else {
                                    for (int i = 0; i < dataList.size(); i++) {
                                        dataList.get(i).iconRes = iconRes;
                                    }
                                    //刷新数据
                                    initListBeans(dataList, currencyAdapter);
                                }
                            } else {
                                if (1 == pageNum) {
                                    //如果是三会一课
                                    if (TYPE_SHYK.equals(title)) {
                                        confAdapter.replaceData(new ArrayList<>());
                                        confAdapter.setEmptyView(emptyView);
                                    } else if (TYPE_ZTDR.equals(title)) {
                                        confAdapter.replaceData(new ArrayList<>());
                                        confAdapter.setEmptyView(emptyView);
                                    } else {
                                        currencyAdapter.replaceData(new ArrayList<>());
                                        currencyAdapter.setEmptyView(emptyView);
                                    }
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
                            //如果是三会一课
                            if (TYPE_SHYK.equals(title)) {
                                confAdapter.setEmptyView(errorView);
                            } else if (TYPE_ZTDR.equals(title)) {
                                confAdapter.setEmptyView(errorView);
                            } else {
                                currencyAdapter.setEmptyView(errorView);
                            }
                        }
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    /**
     * @Query("pageNum") int pageNum,
     * @Query("pageSize") int pageSize,
     * @Query("accountId") int accountId,
     * @Query("unitId") int unitId,
     * @Query("year") int year
     */
    private Observable<BaseData<ThemePartyBean>> getBaseDataObservable(int pageNum, String url) {
        Map<String, Integer> queryMap = new HashMap<>();
        queryMap.put("pageNum", pageNum);
        queryMap.put("pageSize", PAGE_SIZE);
        queryMap.put("accountId", Integer.valueOf(accountBean.id));
        queryMap.put("unitId", unitId);
        queryMap.put("year", requestYear);

        return HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryThemeDays(
                        url,
                        queryMap);
    }

    /**
     * 获取主题教育
     *
     * @param pageNum
     */
    private void getZtjyData(int pageNum) {
        HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryZtjys(pageNum,
                        PAGE_SIZE,
                        isLogin() ? Integer.valueOf(accountBean.unitId) : requestUnitId,
                        eductype
                )
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<ZtjyBean>>() {
                    @Override
                    public void onSuccess(BaseData<ZtjyBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<ZtjyBean> dataList = baseData.dataList;
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
                            currencyAdapter.setEmptyView(errorView);
                        }
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    /**
     * 获取微心愿的数据
     */
    private void getWxyData(int pageNum) {
        HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryWxys(pageNum, PAGE_SIZE, townshipId, wxyType)
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

    /**
     * 获取小微权力清单的数据
     *
     * @param pageNum
     */
    private void getXwqlqdData(int pageNum) {
        Map<String, Integer> hashMap = new HashMap<>();
        hashMap.put("pageNum", pageNum);
        hashMap.put("pageSize", PAGE_SIZE);
        hashMap.put("type", type);
        if (1 != type) {
            hashMap.put("type2", type2);
        }

        HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryXwqlqds(hashMap)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<XwqlqdBean>>() {
                    @Override
                    public void onSuccess(BaseData<XwqlqdBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<XwqlqdBean> dataList = baseData.dataList;
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

    /**
     * 图说本村
     */
    private void getTsbcData(int pageNum) {
        requestUnitId = (1 == unitId) ? requestUnitId : unitId;
        HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryTsbcList(requestUrl, pageNum, PAGE_SIZE, requestUnitId, requestYear)
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
     * 设置数据z
     */
    private void initListBeans(List newsList, BaseQuickAdapter adapter) {
        //隐藏骨架图
        hideSkeletonScreen();

        if (1 == pageNum) {
            //刷新加载更多状态
            refreshLayout.resetNoMoreData();
            adapter.replaceData(newsList);
        } else {
            adapter.addData(newsList);
        }
        refreshLayout.setEnableLoadMore(true);
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

        if (1 == pageNum && !success) {
            refreshLayout.setEnableLoadMore(false);
        }
    }

    /**
     * 初始化主题党日适配器
     */
    private void initZtdrAdapter(String type) {
        currencyAdapter = new ThemePatryCurrencyAdapter(R.layout.item_theme_party_day, new ArrayList<>());
        currencyAdapter.setType(type);
        currencyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (title) {
                    //微心愿
                    case TYPE_WXY:
                        WxyBean wxyBean = (WxyBean) currencyAdapter.getItem(position);

                        WxyDetailsActivity.startActivity(getActivity(), wxyBean);
                        break;

                    default:
                        //跳转到内容界面
                        startContentActivity(position);
                        break;
                }

            }
        });

        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvList.setAdapter(currencyAdapter);

        showSkeletonSceen(currencyAdapter);
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
                List<String> urls = new ArrayList<>();
                ContentDetailsBean contentDetailsBean = null;
                StringBuffer stringBuffer = null;
                List<String> strings = null;

                String unitName = "辛集市";

                switch (title) {
                    //党建资讯
                    case TYPE_DJZX:
                        //乡村动态
                    case TYPE_XCDT:
                        //典型经验
                    case TYPE_DXJY:
                        //他山之石
                    case TYPE_TSZS:
                        //跳转到工作动态
                        startGzdtActivity(position);
                        break;

                    //微心愿
                    case TYPE_WXY:
                        WxyBean wxyBean = (WxyBean) currencyAdapter.getItem(position);
                        WxyDetailsActivity.startActivity(getActivity(), wxyBean);
                        break;

                    case TYPE_MZPY:
                    case TYPE_ZZSHH:
                    case TYPE_MORE:
                        //跳转到内容界面
                        startContentActivity(position);
                        break;

                    //小微权力清单
                    case RefreshRecyclerActivity.TYPE_XWQLQD:
                        startXwqlqdActivity(position, urls, unitName);
                        break;


                    default:
                        //跳转到内容界面
//                        startContentActivity(position);
                        break;
                }
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvList.setAdapter(currencyAdapter);

        showSkeletonSceen(currencyAdapter);
    }

    /**
     * 跳转到小微权力清单
     *
     * @param position
     * @param urls
     * @param unitName
     */
    public void startXwqlqdActivity(int position, List<String> urls, String unitName) {
        ContentDetailsBean contentDetailsBean;
        StringBuffer stringBuffer;
        List<String> strings;
        XwqlqdBean xwqlqdBean = (XwqlqdBean) currencyAdapter.getItem(position);

        if (!TextUtils.isEmpty(xwqlqdBean.villagename)) {
            unitName = xwqlqdBean.villagename;
        } else if (!TextUtils.isEmpty(xwqlqdBean.vtownsname)) {
            unitName = xwqlqdBean.vtownsname;
        }

        urls.addAll(Arrays.asList(
                xwqlqdBean.pic1,
                xwqlqdBean.pic2,
                xwqlqdBean.pic3,
                xwqlqdBean.pic4,
                xwqlqdBean.pic5,
                xwqlqdBean.pic6,
                xwqlqdBean.pic7,
                xwqlqdBean.pic8,
                xwqlqdBean.pic9
        ));

        contentDetailsBean = ContentDetailsBean.ContentDetailsBeanFactory(
                xwqlqdBean.theme,
                xwqlqdBean.content,
                urls,
                xwqlqdBean.videoUrl,
                xwqlqdBean.videoThumbnailUrl,
                xwqlqdBean.smalltime,
                unitName,
                xwqlqdBean.pdfUrl,
                xwqlqdBean.pdfrealname);


        stringBuffer = new StringBuffer();
        strings = new ArrayList<>();

        if (!TextUtils.isEmpty(xwqlqdBean.remark1)) {
            strings.add(xwqlqdBean.remark1);
        }

        if (!TextUtils.isEmpty(xwqlqdBean.remark2)) {
            strings.add(xwqlqdBean.remark2);
        }

        for (int i = 0; i < strings.size(); i++) {
            if (i == strings.size() - 1) {
                stringBuffer.append((i + 1) + "." + strings.get(i));
            } else {
                stringBuffer.append((i + 1) + "." + strings.get(i) + "\n");
            }
        }

        contentDetailsBean.oneLable = ContentDetailsActivity.NOT_SHOW;
        contentDetailsBean.twoLable = "二、备注";
        contentDetailsBean.threeLable = "三、类型";

        contentDetailsBean.one = DateUtil.longToString(xwqlqdBean.smalltime, DateUtil.FORMAT_DATE);
        contentDetailsBean.two = TextUtils.isEmpty(stringBuffer.toString()) ? "暂无" : stringBuffer.toString();
        contentDetailsBean.three = xwqlqdBean.typeVal + " " + xwqlqdBean.typeVal2;

        ContentDetailsActivity.startActivity(getActivity(), contentDetailsBean);
    }

    /**
     * 跳转到工作动态
     *
     * @param position
     */
    public void startGzdtActivity(int position) {
        List<String> urls = new ArrayList<>();
        ContentDetailsBean contentDetailsBean = null;
        StringBuffer stringBuffer = null;
        List<String> strings = null;

        String unitName = "辛集市";

        ZtjyBean ztjyBean = (ZtjyBean) currencyAdapter.getItem(position);

        urls.addAll(Arrays.asList(
                ztjyBean.pic1,
                ztjyBean.pic2,
                ztjyBean.pic3,
                ztjyBean.pic4,
                ztjyBean.pic5,
                ztjyBean.pic6,
                ztjyBean.pic7,
                ztjyBean.pic8,
                ztjyBean.pic9
        ));

        if (!TextUtils.isEmpty(ztjyBean.villagename)) {
            unitName = ztjyBean.villagename;
        } else if (!TextUtils.isEmpty(ztjyBean.vtownsname)) {
            unitName = ztjyBean.vtownsname;
        }

        contentDetailsBean = ContentDetailsBean.ContentDetailsBeanFactory(
                ztjyBean.theme,
                ztjyBean.content,
                urls,
                ztjyBean.videoUrl,
                ztjyBean.videoThumbnailUrl,
                ztjyBean.eduDate,
                unitName
        );

        contentDetailsBean.oneLable = ContentDetailsActivity.NOT_SHOW;

        ContentDetailsActivity.startActivity(getActivity(), contentDetailsBean);
    }

    public void startContentActivity(int position) {
        ThemePartyBean themePartyBean = (ThemePartyBean) currencyAdapter.getItem(position);

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

        if (TYPE_MZPY.equals(title)) {
            contentDetailsBean.fiveLable = "五、形式";
            contentDetailsBean.five = themePartyBean.typeVal;

            contentDetailsBean.sixLable = "六、被评议人数";
            contentDetailsBean.six = themePartyBean.reviewedNum;
        } else if (TYPE_ZZSHH.equals(title)) {
            contentDetailsBean.fiveLable = "五、形式";
            contentDetailsBean.five = themePartyBean.typeVal;
        }
        ContentDetailsActivity.startActivity(getActivity(), contentDetailsBean);
    }

    /**
     * 初始化三会一课适配器
     */
    private void initConfAdapter() {
        confAdapter = new ConfAdapter(R.layout.item_conf, new ArrayList<>());
        confAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ThemePartyBean themePartyBean = (ThemePartyBean) confAdapter.getItem(position);

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
                contentDetailsBean.five = TYPE_SHYK.equals(title) ? themePartyBean.typeValue : themePartyBean.typeVal;

                ContentDetailsActivity.startActivity(getActivity(), contentDetailsBean);
            }
        });

        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvList.setAdapter(confAdapter);

        showSkeletonSceen(confAdapter);
    }

    /**
     * 初始化组工文件适配器
     */
    private void initDocumentAdapter() {
        documentAdapter = new DocumentAdapter(R.layout.item_document, new ArrayList<>());
        documentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DocumentBean item = documentAdapter.getItem(position);
                DocumentDetailsActivity.startActivity(getActivity(), item);
            }
        });

        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvList.setAdapter(documentAdapter);

        showSkeletonSceen(documentAdapter);
    }

    /**
     * 初始化推荐适配器
     */
    private void initNewsListAdapter() {
        newsListAdapter = new NewsListAdapter(R.layout.item_banner, new ArrayList<>());
        newsListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                NewsBean newsBean = newsListAdapter.getItem(position).newsBean;

                ContentDetailsBean contentDetailsBean = ContentDetailsBean.ContentDetailsBeanFactory(
                        newsBean.title,
                        newsBean.context,
                        new ArrayList<>(),
                        newsBean.videoUrl,
                        newsBean.videoThumbnailUrl,
                        newsBean.createDate,
                        newsBean.announcer);

                contentDetailsBean.oneLable = ContentDetailsActivity.NOT_SHOW;

                //TODO:是否有评论
                contentDetailsBean.isComment = true;

                contentDetailsBean.id = newsBean.id;

                ContentDetailsActivity.startActivity(getActivity(), contentDetailsBean);
            }
        });

        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvList.setAdapter(newsListAdapter);

        showSkeletonSceen(newsListAdapter);
    }

    private List<LocalMedia> selectListImage = new ArrayList<>();

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

        showSkeletonSceen(tsbcAdapter);
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

    private void register() {
        if (!EventBus.getDefault().isRegistered(this)) {
            //注册EventBsus,注意参数是this，传入activity会报错
            EventBus.getDefault().register(this);
        }
    }

    private void unRegister() {
        if (EventBus.getDefault().isRegistered(this)) {
            //注册EventBus,注意参数是this，传入activity会报错
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * EventBus的处理事件函数  该方法可以随意取名 必须为public 必须添加注解并指定线程模型
     * 之后EventBus会自动扫描到此函数，进行数据传递
     *
     * @param eventMessage
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshRecycler(EventMessage eventMessage) {
        switch (eventMessage.message) {
            case Messages.REFRESH_RECYCLER:
                Log.i(Messages.REFRESH_RECYCLER, Messages.REFRESH_RECYCLER + "-->" + eventMessage.succeed + "-->" + title);
                refreshLayout.autoRefresh();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_time:
                if (null == yearList || yearList.size() <= 0) {
                    ToastHelper.showShort("没有年份选择");
                    return;
                }
                showTimePopupWindow();
                break;

            case R.id.tv_type:
                if (null == typeList) {
                    typeList = new ArrayList<>();
                    if (TYPE_SHYK.equals(title)) {
                        typeList.add("全部");
                        typeList.add("党员大会");
                        typeList.add("党支部会");
                        typeList.add("党小组会");
                        typeList.add("党课");
                    } else {
                        typeList.add("全部");
                        typeList.add("党员大会");
                        typeList.add("党支部会");
                        typeList.add("党小组会");
                        typeList.add("党课");
                        typeList.add("志愿服务");
                        typeList.add("参观学习");
                        typeList.add("其他");
                    }
                }
                showTypePopupWindow(typeList);
                break;

            case R.id.tv_address:
                //如果是村镇
                if (null != accountBean && 2 == accountBean.level) {
                    if (null == screenCityDialog) {
                        //如果左边的列表等于null，代表第一次请求
                        leftDepartments = new ArrayList<>();
                        DepartmentBean departmentBean = null;

                        departmentBean = new DepartmentBean();
                        departmentBean.departmentName = "全" + accountBean.unitName;
                        departmentBean.id = LEFT_ALL_ID;
                        leftDepartments.add(departmentBean);

                        departmentBean = new DepartmentBean();
                        departmentBean.departmentName = accountBean.unitName;
                        departmentBean.id = accountBean.unitId;
                        leftDepartments.add(departmentBean);

                        showScreenDialog(leftDepartments, new ArrayList<>());
                    } else {
                        screenCityDialog.showPopupWindow(rlScreen);
                    }
                } //如果是市级账号
                else if (null != accountBean && 1 == accountBean.level) {
                    if (null == screenCityDialog) {
                        getDepartmentList(1);
                    } else {
                        screenCityDialog.showPopupWindow(llScreen);
                    }
                }//没有登录
                else {
                    if (null == screenCityDialog) {
                        getDepartmentList(1);
                    } else {
                        screenCityDialog.showPopupWindow(llScreen);
                    }
                }
                break;

            case R.id.tv_screen:
                //如果是村镇
                if (null != accountBean && 2 == accountBean.level) {
                    if (null == screenCityDialog) {
                        //如果左边的列表等于null，代表第一次请求
                        leftDepartments = new ArrayList<>();
                        DepartmentBean departmentBean = null;

                        departmentBean = new DepartmentBean();
                        departmentBean.departmentName = "全" + accountBean.unitName;
                        departmentBean.id = LEFT_ALL_ID;
                        leftDepartments.add(departmentBean);

                        departmentBean = new DepartmentBean();
                        departmentBean.departmentName = accountBean.unitName;
                        departmentBean.id = accountBean.unitId;
                        leftDepartments.add(departmentBean);

                        showScreenDialog(leftDepartments, new ArrayList<>());
                    } else {
                        screenCityDialog.showPopupWindow(rlScreen);
                    }
                } //如果是市级账号
                else if (null != accountBean && 1 == accountBean.level) {
                    if (null == screenCityDialog) {
                        getDepartmentList(1);
                    } else {
                        screenCityDialog.showPopupWindow(rlScreen);
                    }
                }//没有登录
                else {
                    if (null == screenCityDialog) {
                        getDepartmentList(1);
                    } else {
                        screenCityDialog.showPopupWindow(rlScreen);
                    }
                }
                break;

        }
    }

    /*********************************************时间的筛选框---start******************************/
    private ScreenTimePopupWindow timePopupWindow;

    /**
     * 显示时间的筛选框
     */
    private void showTimePopupWindow() {
        if (null == timePopupWindow) {
            timePopupWindow = new ScreenTimePopupWindow(
                    getActivity(),
                    DisplayUtil.getScreenWidth(),
                    DisplayUtil.getScreenHeight() / 2,
                    yearList
            );

            timePopupWindow.setOnScreenClick(new ScreenTimePopupWindow.onScreenClick() {
                @Override
                public void onItemClick(int position) {
                    String year = yearList.get(position);
                    tvTime.setText(year);

                    if (0 == position) {
                        requestYear = 0;
                    } else {
                        requestYear = Integer.valueOf(year.substring(0, year.length() - 1));
                    }
                    refreshLayout.autoRefresh();
                    timePopupWindow.dismiss();
                }
            });
        }
        timePopupWindow.setAlignBackground(true);
        timePopupWindow.showPopupWindow(llScreen);
    }
    /*********************************************时间的筛选框---end******************************/

    /*********************************************类型的筛选框---start******************************/
    private ScreenTimePopupWindow typePopupWindow;
    private List<String> typeList;

    /**
     * 显示类型筛选的对话框
     *
     * @param typeList
     */
    private void showTypePopupWindow(List<String> typeList) {
        if (null == typePopupWindow) {
            typePopupWindow = new ScreenTimePopupWindow(
                    getActivity(),
                    DisplayUtil.getScreenWidth(),
                    DisplayUtil.getScreenHeight() / 2,
                    typeList
            );

            typePopupWindow.setOnScreenClick(new ScreenTimePopupWindow.onScreenClick() {
                @Override
                public void onItemClick(int position) {
                    //类型
                    shykType = position;

                    String type = typeList.get(position);
                    tvType.setText(type);

                    refreshLayout.autoRefresh();
                    typePopupWindow.dismiss();
                }
            });
        }
        typePopupWindow.setAlignBackground(true);
        typePopupWindow.showPopupWindow(llScreen);
    }
    /*********************************************类型的筛选框---end******************************/

    /*********************************************地址的筛选框---start******************************/
    //乡镇ID
    private int townshipId;
    //街村ID
    private int villageId;

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
        HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryDepartment(currentUnitId)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<DepartmentBean>>() {
                    @Override
                    public void onSuccess(BaseData<DepartmentBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<DepartmentBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                DepartmentBean departmentBean = new DepartmentBean();

                                //如果左边的列表等于null，代表第一次请求
                                if (null == leftDepartments) {
                                    leftDepartments = dataList;
                                    //如果是用户bean为空
                                    departmentBean.departmentName = "全辛集市";
                                    departmentBean.id = LEFT_ALL_ID;
                                    leftDepartments.add(0, departmentBean);
                                    showScreenDialog(leftDepartments, new ArrayList<>());
                                } else {
                                    departmentBean.departmentName = "全部";
                                    departmentBean.id = RIGHT_ALL_ID;
                                    dataList.add(0, departmentBean);
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
                    DisplayUtil.getScreenHeight() / 2,
                    leftData,
                    rightData
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


                        if (townshipName.equals(villageName)) {
                            tvScreen.setText(villageName);
                        } else {
                            tvScreen.setText(townshipName + villageName);
                        }

                        screenCityDialog.setRightNewData(new ArrayList<>());
                        screenCityDialog.dismiss();
                        refreshLayout.autoRefresh();
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
                        refreshLayout.autoRefresh();
                    } else {
                        //如果没登录，并且是微心愿
                        villageId = cityId;

                        villageName = departmentName;

                        screenCityDialog.dismiss();
                        refreshLayout.autoRefresh();
                    }
                    if (townshipName.equals(villageName)) {
                        tvScreen.setText(villageName);
                    } else {
                        tvScreen.setText(townshipName + villageName);
                    }
                }
            });
        }
        screenCityDialog.setAlignBackground(true);
        screenCityDialog.showPopupWindow(rlScreen);
    }
    /*********************************************地址的筛选框---end******************************/

}
