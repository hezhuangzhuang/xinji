package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.jaeger.library.StatusBarUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zxwl.commonlibrary.utils.DisplayUtil;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.commonlibrary.widget.NoScrollViewPager;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.bean.response.CurrencyBean;
import com.zxwl.network.bean.response.DepartmentBean;
import com.zxwl.network.bean.response.DzzcyBean;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.bean.response.OrgInfoCountBean;
import com.zxwl.network.bean.response.TsbcBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.CdzzcyAdapter;
import com.zxwl.xinji.adapter.MyPagerAdapter;
import com.zxwl.xinji.adapter.TsbcAdapter;
import com.zxwl.xinji.adapter.VillageOrganAdapter;
import com.zxwl.xinji.adapter.VillagePeopleAdapter;
import com.zxwl.xinji.adapter.item.StudyListItem;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.bean.StudyHeadBean;
import com.zxwl.xinji.bean.VillageOrgan;
import com.zxwl.xinji.fragment.StatisticsFragment;
import com.zxwl.xinji.utils.PreferenceUtil;
import com.zxwl.xinji.widget.ScreenCityPopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func4;

/**
 * 村级组织
 * <p>
 * 图说本村
 * <p>
 * 其他
 */
public class VillageListActivity extends BaseActivity implements View.OnClickListener {
    private int pageNum;
    private static final int PAGE_SIZE = 10;

    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvRightOperate;
    private FrameLayout flScreen;
    private TextView tvCity;
    private TextView tvCityUnit;
    private TextView tvScreen;

    private LinearLayout llTop;
    private TextView tvDzzcy;
    private TextView tvCwhcy;
    private TextView tvCjhcy;
    private TextView tvHzhbcy;

    private SmartRefreshLayout refreshLayout;
    private RecyclerView rvList;

    private SlidingTabLayout tbLayout;
    private NoScrollViewPager vpContent;

    private List<String> mTitles;
    private List<Fragment> mFragments = new ArrayList<>();
    private int currentIndex;

    public static final String TYPE_TSBC = "图说本村";

    public static final String TYPE_MORE = "其他";

    public static final String TYPE_CJZZ = "村级组织";

    public static final String TYPE_DZZCY = "村党组织成员";
    public static final String TYPE_CWHCY = "村委会成员";
    public static final String TYPE_CJHCY = "村监会成员";
    public static final String TYPE_HZZZCY = "合作组织成员";

    public static final String TITLE = "TITLE";
    public static final String UNIT_ID = "UNIT_ID";
    private String title;

    //图说本村适配器
    private TsbcAdapter tsbcAdapter;

    //村级组织适配器
    private VillageOrganAdapter villageOrganAdapter;

    //村级人员适配器
    private VillagePeopleAdapter villagePeopleAdapter;

    //村党组织成员
    //村委会成员
    //村监会成员
    //合作组织成员
    private CdzzcyAdapter cdzzcyAdapter;

    /**
     * 请求的url
     */
    private String requestUrl;

    //请求的单位id
    public int requestUnitId;

    private View emptyView;
    private View errorView;

    private LoginBean.AccountBean accountBean;

    public static void startActivity(Context context, String title) {
        Intent intent = new Intent(context, VillageListActivity.class);
        intent.putExtra(TITLE, title);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, String title, int unitId) {
        Intent intent = new Intent(context, VillageListActivity.class);
        intent.putExtra(TITLE, title);
        intent.putExtra(UNIT_ID, unitId);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvRightOperate = (TextView) findViewById(R.id.tv_right_operate);
        flScreen = (FrameLayout) findViewById(R.id.fl_screen);
        tvCity = (TextView) findViewById(R.id.tv_city);
        tvCityUnit = (TextView) findViewById(R.id.tv_city_unit);
        tvScreen = (TextView) findViewById(R.id.tv_screen);
        refreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        rvList = (RecyclerView) findViewById(R.id.rv_list);

        tbLayout = (SlidingTabLayout) findViewById(R.id.tb_layout);
        vpContent = (NoScrollViewPager) findViewById(R.id.vp_content);

        llTop = findViewById(R.id.ll_top);
        tvDzzcy = (TextView) findViewById(R.id.tv_dzzcy);
        tvCwhcy = (TextView) findViewById(R.id.tv_cwhcy);
        tvCjhcy = (TextView) findViewById(R.id.tv_cjhcy);
        tvHzhbcy = (TextView) findViewById(R.id.tv_hzhbcy);

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
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        title = getIntent().getStringExtra(TITLE);
        tvTopTitle.setText(title);

        accountBean = PreferenceUtil.getUserInfo(this);

        requestUnitId = accountBean.unitId;

        tvCity.setText(accountBean.unitName);
        tvCityUnit.setText(accountBean.unitName + "党支部");

        initAdapter();

        //如果是村级
        if (isVillageLeavel()) {
            flScreen.setVisibility(View.GONE);
        }

        //管理员
        if (1 == accountBean.checkAdmin) {
            if (TYPE_TSBC.equals(title) || TYPE_MORE.equals(title)) {
                tvRightOperate.setText("添加");
                tvRightOperate.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.mipmap.ic_wxy_add, 0);
                tvRightOperate.setVisibility(View.VISIBLE);
            }

            if (TYPE_DZZCY.equals(title) ||
                    TYPE_CWHCY.equals(title) ||
                    TYPE_CJHCY.equals(title) ||
                    TYPE_HZZZCY.equals(title)) {
                flScreen.setVisibility(View.GONE);
                requestUnitId = getIntent().getIntExtra(UNIT_ID, -1);
            }

            //获取list数据
            getListData(1);

            register();
        } //非管理员
        else {
            //非管理员查看村级组织是缺省页
            if (TYPE_CJZZ.equals(title)) {
                llTop.setVisibility(View.GONE);
                flScreen.setVisibility(View.GONE);

                ImageView ivEmpty = emptyView.findViewById(R.id.iv_empty_img);
                ivEmpty.setImageResource(R.mipmap.ic_no_authority);
                ((TextView) emptyView.findViewById(R.id.tv_empty_content)).setText("");
                emptyView.findViewById(R.id.tv_retry).setVisibility(View.GONE);

                hideSkeletonScreen();

                //级别3为村镇
                if (isVillageLeavel()) {
                    villagePeopleAdapter.setEmptyView(emptyView);
                } else {
                    villageOrganAdapter.setEmptyView(emptyView);
                }
            }else {
                //获取list数据
                getListData(1);
            }
        }
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(this);
        tvScreen.setOnClickListener(this);
        tvRightOperate.setOnClickListener(this);

        tvDzzcy.setOnClickListener(this);
        tvCwhcy.setOnClickListener(this);
        tvCjhcy.setOnClickListener(this);
        tvHzhbcy.setOnClickListener(this);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getListData(1);
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                getListData(pageNum + 1);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_village_list;
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        switch (title) {
            //图说本村
            case TYPE_TSBC:
                requestUrl = Urls.QUERY_TSBC_LIST;

                initTsbcAdapter();

                refreshLayout.setVisibility(View.VISIBLE);
                refreshLayout.setEnableLoadMore(true);
                refreshLayout.setEnableRefresh(true);

                showSkeletonSceen(tsbcAdapter);
                break;

            //村级组织
            case TYPE_CJZZ:
                refreshLayout.setVisibility(View.VISIBLE);
                refreshLayout.setEnableLoadMore(false);
                refreshLayout.setEnableRefresh(false);

                //级别3为村镇
                if (isVillageLeavel()) {
                    llTop.setVisibility(View.VISIBLE);

                    initVillagePeopleAdapter();

                    showSkeletonSceen(villagePeopleAdapter);
                } else {
                    requestUrl = Urls.QUERY_TSBC_LIST;

                    initVillageOrganAdapter();

                    showSkeletonSceen(villageOrganAdapter);
                }
                break;

            //其他
            case TYPE_MORE:
                tbLayout.setVisibility(View.VISIBLE);
                vpContent.setVisibility(View.VISIBLE);
                initMoreTab();
                break;

            case TYPE_DZZCY:
            case TYPE_CWHCY:
            case TYPE_CJHCY:
            case TYPE_HZZZCY:
                refreshLayout.setVisibility(View.VISIBLE);
                refreshLayout.setEnableLoadMore(true);
                refreshLayout.setEnableRefresh(true);
                initCdzzcyAdapter();
                showSkeletonSceen(cdzzcyAdapter);
                break;
        }
    }

    /**
     * 是否是村级别
     *
     * @return
     */
    private boolean isVillageLeavel() {
        return accountBean.level == 3;
    }

    /**
     * 是否是乡镇
     *
     * @return
     */
    private boolean isTownLeavel() {
        return accountBean.level == 2;
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
                        PictureSelector.create(VillageListActivity.this).themeStyle(R.style.picture_default_style).openExternalPreview(0, selectListImage);
                        break;

                    case R.id.iv_two:
                        // 预览图片 可自定长按保存路径
                        PictureSelector.create(VillageListActivity.this).themeStyle(R.style.picture_default_style).openExternalPreview(1, selectListImage);
                        break;

                    case R.id.iv_three:
                        // 预览图片 可自定长按保存路径
                        PictureSelector.create(VillageListActivity.this).themeStyle(R.style.picture_default_style).openExternalPreview(2, selectListImage);
                        break;
                }
            }
        });

        rvList.setLayoutManager(new LinearLayoutManager(this));
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
     * 初始化村级组织适配器
     */
    public void initVillageOrganAdapter() {
        villageOrganAdapter = new VillageOrganAdapter(R.layout.item_village_organ, new ArrayList<>());
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(villageOrganAdapter);
    }

    /**
     * 初始化村级人员适配器
     */
    public void initVillagePeopleAdapter() {
        rvList.setBackgroundColor(ContextCompat.getColor(this, R.color.color_f8f8f8));
        villagePeopleAdapter = new VillagePeopleAdapter(R.layout.item_village_people_head, new ArrayList<>());
        villagePeopleAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                CurrencyBean currencyBean = villagePeopleAdapter.getItem(position).newsBean;
                if (currencyBean instanceof StudyHeadBean) {
                    StudyHeadBean studyHeadBean = (StudyHeadBean) currencyBean;
                    switch (studyHeadBean.title) {
                        case TYPE_DZZCY:
                            VillageListActivity.startActivity(VillageListActivity.this, TYPE_DZZCY, requestUnitId);
                            break;

                        case TYPE_CWHCY:
                            VillageListActivity.startActivity(VillageListActivity.this, TYPE_CWHCY, requestUnitId);
                            break;

                        case TYPE_CJHCY:
                            VillageListActivity.startActivity(VillageListActivity.this, TYPE_CJHCY, requestUnitId);
                            break;

                        case TYPE_HZZZCY:
                            VillageListActivity.startActivity(VillageListActivity.this, TYPE_HZZZCY, requestUnitId);
                            break;
                    }
                }
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(villagePeopleAdapter);
    }

    /**
     * 初始化组织成员适配器
     */
    public void initCdzzcyAdapter() {
        cdzzcyAdapter = new CdzzcyAdapter(R.layout.item_village_people, new ArrayList<>());
        cdzzcyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(cdzzcyAdapter);
    }

    /**
     * 获取列表数据
     *
     * @param pageNum
     */
    private void getListData(int pageNum) {
        switch (title) {
            case TYPE_TSBC:
                getTsbcData(pageNum);
                break;

            //村级组织
            case TYPE_CJZZ:
                if (isVillageLeavel()) {
                    getHotDataRequest();
                } else {
                    queryPartyCount();
                }
                break;

            case TYPE_DZZCY:
            case TYPE_CWHCY:
            case TYPE_CJHCY:
            case TYPE_HZZZCY:
                getDzzcyPeopleList(pageNum);
                break;
        }
    }

    /**
     * @param pageNum
     */
    private void getDzzcyPeopleList(int pageNum) {
        getObservable(pageNum)
                .subscribe(new RxSubscriber<BaseData<DzzcyBean>>() {
                    @Override
                    public void onSuccess(BaseData<DzzcyBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<DzzcyBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                for (DzzcyBean dzzcyBean : dataList) {
                                    dzzcyBean.itemTyep = title;
                                }
                                if (1 == pageNum) {
                                    setPageNum(1);
                                } else {
                                    setPageNum(pageNum);
                                }

                                //刷新数据
                                initListBeans(dataList, cdzzcyAdapter);
                            } else {
                                if (1 == pageNum) {
                                    cdzzcyAdapter.replaceData(new ArrayList<>());
                                    cdzzcyAdapter.setEmptyView(emptyView);
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
                            cdzzcyAdapter.setEmptyView(errorView);
                        }
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    private Observable<BaseData<DzzcyBean>> getObservable(int pageNum) {
        switch (title) {
            case TYPE_DZZCY:
                return queryDzzcyList(1, pageNum, PAGE_SIZE);

            case TYPE_CWHCY:
                return queryDzzcyList(2, pageNum, PAGE_SIZE);

            case TYPE_CJHCY:
                return queryCjhcyList(pageNum, PAGE_SIZE);

            case TYPE_HZZZCY:
                return queryHzzzcyList(pageNum, PAGE_SIZE);
            default:
                return null;
        }
    }

    /**
     * 图说本村
     */
    private void getTsbcData(int pageNum) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryTsbcList(requestUrl, pageNum, PAGE_SIZE, requestUnitId)
                .compose(this.bindToLifecycle())
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
     * 获取村级组织人员
     */
    private void getHotDataRequest() {
        Observable.zip(
                queryDzzcyList(1, 1, 3),
                queryDzzcyList(2, 1, 3),
                queryCjhcyList(1, 3),
                queryHzzzcyList(1, 3),
                new Func4<BaseData<DzzcyBean>,
                        BaseData<DzzcyBean>,
                        BaseData<DzzcyBean>,
                        BaseData<DzzcyBean>,
                        List<StudyListItem>>() {
                    @Override
                    public List<StudyListItem> call(
                            BaseData<DzzcyBean> dzzcyList,
                            BaseData<DzzcyBean> cwhcyList,
                            BaseData<DzzcyBean> cjhList,
                            BaseData<DzzcyBean> hzhbList) {
                        List<StudyListItem> newList = new ArrayList<>();
                        StudyListItem studyListItem = null;

                        getStudyItemList(dzzcyList, newList, TYPE_DZZCY);
                        getStudyItemList(cwhcyList, newList, TYPE_CWHCY);
                        getStudyItemList(cjhList, newList, TYPE_CJHCY);
                        getStudyItemList(hzhbList, newList, TYPE_HZZZCY);
                        return newList;
                    }
                })
                .subscribe(new RxSubscriber<List<StudyListItem>>() {
                    @Override
                    public void onSuccess(List<StudyListItem> newsBeans) {
                        hideSkeletonScreen();
                        villagePeopleAdapter.replaceData(newsBeans);
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        hideSkeletonScreen();
                        ToastHelper.showShort("onError:" + responeThrowable.getCause().getMessage());
                    }
                });
    }

    public void getStudyItemList(BaseData<DzzcyBean> ztdtList, List<StudyListItem> newList, String title) {
        StudyListItem studyListItem = null;
        if (BaseData.SUCCESS.equals(ztdtList.result) && ztdtList.dataList.size() > 0) {
            studyListItem = new StudyListItem(new StudyHeadBean(title, getHeadRes(title)));
            newList.add(studyListItem);
            for (int i = 0, len = ztdtList.dataList.size(); i < len; i++) {
                DzzcyBean themePartyBean = ztdtList.dataList.get(i);
                themePartyBean.itemTyep = title;
                if (i == len - 1) {
                    themePartyBean.isLast = true;
                }
                newList.add(new StudyListItem(themePartyBean));
            }
        }
    }

    private int getHeadRes(String title) {
        switch (title) {
            //村党组织成员
            case VillageListActivity.TYPE_DZZCY:
                return R.mipmap.ic_people_cdzzcy;

            //村委会成员
            case VillageListActivity.TYPE_CWHCY:
                return R.mipmap.ic_people_cwhcy;

            //村监会成员
            case VillageListActivity.TYPE_CJHCY:
                return R.mipmap.ic_people_cjhcy;

            //合作组织成员
            case VillageListActivity.TYPE_HZZZCY:
                return R.mipmap.ic_people_hzzzcy;

            default:
                return R.mipmap.ic_people_cdzzcy;
        }
    }

    /**
     * 获取党组织成员
     *
     * @param type type为1：获取党组织成员 type为2：获取村委会成员
     */
    private Observable<BaseData<DzzcyBean>> queryDzzcyList(int type, int pageNum, int pageSize) {
        return HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryOrgPeopleList(pageNum, pageSize, requestUnitId, type)
                .compose(this.bindToLifecycle())
                .compose(new CustomCompose());
    }

    /**
     * 获取村监会成员
     */
    private Observable<BaseData<DzzcyBean>> queryCjhcyList(int pageNum, int pageSize) {
        return HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .querySupervisionList(pageNum, pageSize, requestUnitId)
                .compose(this.bindToLifecycle())
                .compose(new CustomCompose());
    }

    /**
     * 获取合作组织成员
     */
    private Observable<BaseData<DzzcyBean>> queryHzzzcyList(int pageNum, int pageSize) {
        return HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryCooperationList(pageNum, pageSize, requestUnitId)
                .compose(this.bindToLifecycle())
                .compose(new CustomCompose());
    }


    /**
     * 查询村级组织数量
     */
    private void queryPartyCount() {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryPartyCount(requestUnitId)
                .compose(this.bindToLifecycle())
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<OrgInfoCountBean>() {
                    @Override
                    public void onSuccess(OrgInfoCountBean baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<VillageOrgan> list = new ArrayList<>();
                            VillageOrgan villageOrgan = null;

                            villageOrgan = new VillageOrgan("村党组织成员", baseData.sum1);
                            list.add(villageOrgan);

                            villageOrgan = new VillageOrgan("村委会成员", baseData.sum2);
                            list.add(villageOrgan);

                            villageOrgan = new VillageOrgan("村监会成员", baseData.sum3);
                            list.add(villageOrgan);

                            villageOrgan = new VillageOrgan("合作组织成员", baseData.sum4);
                            list.add(villageOrgan);

                            villageOrganAdapter.replaceData(list);

                            //隐藏骨架图
                            hideSkeletonScreen();
                        } else {
                            //隐藏骨架图
                            hideSkeletonScreen();

                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        //隐藏骨架图
                        hideSkeletonScreen();

                        if (1 == pageNum) {
                            villageOrganAdapter.setEmptyView(errorView);
                        }
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    /**
     * 设置数据
     */
    private void initListBeans(List newsList, BaseQuickAdapter adapter) {
        //隐藏骨架图
        hideSkeletonScreen();

        if (1 == pageNum) {
            refreshLayout.setEnableLoadMore(true);
            //刷新加载更多状态
            refreshLayout.resetNoMoreData();
            adapter.replaceData(newsList);
        } else {
            adapter.addData(newsList);
        }
        refreshLayout.setEnableLoadMore(true);
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

        if (1 == pageNum && !success) {
            refreshLayout.setEnableLoadMore(false);
        }
    }

    /**
     * 初始化其他的tab
     */
    private void initMoreTab() {
        mTitles = new ArrayList<>();
        mTitles.add(StatisticsFragment.TYPE_JTJJ);
        mTitles.add(StatisticsFragment.TYPE_RYBZ);
        mTitles.add(StatisticsFragment.TYPE_LDGZ);

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
                if (null != screenCityDialog && screenCityDialog.isShowing()) {
                    screenCityDialog.dismiss();
                } else {
                    if (null != screenCityDialog) {
                        showCityDialog();
                    } else {
                        if (isTownLeavel()) {
                            //添加辛集市在左边
                            DepartmentBean departmentBean = new DepartmentBean();
                            departmentBean.id = accountBean.unitId;
                            departmentBean.departmentName = accountBean.unitName;
                            leftDepartments = new ArrayList<>();
                            leftDepartments.add(0, departmentBean);

                            showScreenDialog(leftDepartments, new ArrayList<>());
                        } else {
                            getDepartmentList(1);
                        }
                    }
                }
                break;

            case R.id.tv_right_operate:
                switch (title) {
                    case TYPE_TSBC:
                        AddTsbcActivity.startActivity(VillageListActivity.this, title);
                        break;

                    case TYPE_MORE:
                        ReleaseSelectActivity.startActivity(VillageListActivity.this, false);
                        break;
                }
                break;

            case R.id.tv_dzzcy:
                VillageListActivity.startActivity(VillageListActivity.this, TYPE_DZZCY, requestUnitId);
                break;

            case R.id.tv_cwhcy:
                VillageListActivity.startActivity(VillageListActivity.this, TYPE_CWHCY, requestUnitId);
                break;

            case R.id.tv_cjhcy:
                VillageListActivity.startActivity(VillageListActivity.this, TYPE_CJHCY, requestUnitId);
                break;

            case R.id.tv_hzhbcy:
                VillageListActivity.startActivity(VillageListActivity.this, TYPE_HZZZCY, requestUnitId);
                break;
        }
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

                        requestUnitId = villageId;

                        if (townshipName.equals(villageName)) {
                            tvScreen.setText(villageName);
                        } else {
                            tvScreen.setText(townshipName + villageName);
                        }
                        tvCity.setText(villageName);
                        tvCityUnit.setText(villageName + "党支部");
                        callNumber = terUri;

                        if (TYPE_MORE.equals(title)) {
                            EventBus.getDefault().post(new EventMessage(Messages.REFRESH_UNIT_ID, requestUnitId));
                        } else {
                            //查询村庄详情
//                            getListData(1);
                            llTop.setVisibility(View.VISIBLE);
                            flScreen.setVisibility(View.GONE);
                            if(null==villagePeopleAdapter){
                                initVillagePeopleAdapter();
                            }
                            //查询最热门信息
                            getHotDataRequest();
                        }
                    }
                }
            });
        }
        showCityDialog();
    }

    private void showCityDialog() {
        screenCityDialog.setAlignBackground(false);
        screenCityDialog.showPopupWindow(flScreen);
//        screenCityDialog.setPopupGravity(Gravity.BOTTOM);
    }

    /*********************************************地址的筛选框---end******************************/

    private void register() {
        Log.i("VillageListActivity", title + "注册了");
        if (!EventBus.getDefault().isRegistered(this)) {
            //注册EventBsus,注意参数是this，传入activity会报错
            EventBus.getDefault().register(this);
        }
    }

    private void unRegister() {
        Log.i("VillageListActivity", title + "取消注册了");
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
            //更新党建地图其他
            case Messages.REFRESH_MAP_MORE:
                refreshLayout.autoRefresh();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unRegister();
    }
}
