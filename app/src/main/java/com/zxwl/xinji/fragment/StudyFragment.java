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
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.sdkwrapper.login.LoginCenter;
import com.orhanobut.logger.Logger;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zxwl.commonlibrary.BaseLazyFragment;
import com.zxwl.commonlibrary.utils.LogUtil;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.commonlibrary.widget.banner.BannerView;
import com.zxwl.ecsdk.common.UIConstants;
import com.zxwl.frame.inter.HuaweiLoginImp;
import com.zxwl.frame.utils.sharedpreferences.PreferencesHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.response.BannerEntity;
import com.zxwl.network.bean.response.CurrencyBean;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.bean.response.NewsBean;
import com.zxwl.network.bean.response.StudyNewsBean;
import com.zxwl.network.bean.response.ZtjyBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.interceptor.CommonLogger;
import com.zxwl.xinji.R;
import com.zxwl.xinji.activity.ContentDetailsActivity;
import com.zxwl.xinji.activity.MineActivity;
import com.zxwl.xinji.activity.NewsActivity;
import com.zxwl.xinji.activity.OrganizationDocumentsActivity;
import com.zxwl.xinji.activity.SearchActivity;
import com.zxwl.xinji.activity.TBXActivity;
import com.zxwl.xinji.activity.WorkingConditionActivity;
import com.zxwl.xinji.adapter.StudyNewAdapter;
import com.zxwl.xinji.adapter.item.StudyListItem;
import com.zxwl.xinji.bean.ContentDetailsBean;
import com.zxwl.xinji.bean.StudyHeadBean;
import com.zxwl.xinji.utils.Constant;
import com.zxwl.xinji.utils.PreferenceUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * 学习
 */
public class StudyFragment extends BaseLazyFragment implements View.OnClickListener , LocBroadcastReceiver {
    private static final String TAG = "StudyFragment";

    private TextView etContent;
    private TextView tvLxyz;
    private TextView tvXwzx;
    private TextView tvZzll;
    private TextView tvZcfg;
    private TextView tvZxks;
    private TextView tvXdycjy;
    private TextView tvGzdb;
    private TextView tvJyjl;
    private TextView tvNotif;
    private TextView tvMore;
    private BannerView bannerView;

    private RecyclerView rvList;
    private StudyNewAdapter studyAdapter;

    private LinearLayout llTwo;

    private View headView;

    private TextView tvRightOperate;

    private LoginBean.AccountBean accountBean;

    public static final String TYPE_JYJL = "经验交流";
    public static final String TYPE_ZZLL = "思想理论";
    public static final String TYPE_ZCFG = "政策法规";

    public StudyFragment() {
    }

    public static StudyFragment newInstance() {
        StudyFragment fragment = new StudyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View inflateContentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_study, container, false);
    }

    @Override
    protected void findViews(View view) {
        etContent = (TextView) view.findViewById(R.id.et_content);
        rvList = (RecyclerView) view.findViewById(R.id.rv_list);
        tvRightOperate = (TextView) view.findViewById(R.id.tv_right_operate);
        tvRightOperate.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_FED803));

        headView = LayoutInflater.from(getActivity()).inflate(R.layout.head_fragment_study, (ViewGroup) rvList.getParent(), false);

        tvLxyz = (TextView) headView.findViewById(R.id.tv_zgwj);
        tvXwzx = (TextView) headView.findViewById(R.id.tv_wsbs);
        tvZzll = (TextView) headView.findViewById(R.id.tv_sxll);
        tvZcfg = (TextView) headView.findViewById(R.id.tv_ylbz);
        tvZxks = (TextView) headView.findViewById(R.id.tv_zxks);
        tvXdycjy = (TextView) headView.findViewById(R.id.tv_xdycjy);
        tvGzdb = (TextView) headView.findViewById(R.id.tv_wsbss);
        tvJyjl = (TextView) headView.findViewById(R.id.tv_dwwd);
        tvNotif = (TextView) headView.findViewById(R.id.tv_notif);
        tvMore = (TextView) headView.findViewById(R.id.tv_more);
        bannerView = (BannerView) headView.findViewById(R.id.banner);
        llTwo = (LinearLayout) headView.findViewById(R.id.ll_two);
    }

    @Override
    protected void addListeners() {
        etContent.setOnClickListener(this);
        tvGzdb.setOnClickListener(this);
        tvXdycjy.setOnClickListener(this);
        tvLxyz.setOnClickListener(this);
        tvXwzx.setOnClickListener(this);
        tvZzll.setOnClickListener(this);
        tvZcfg.setOnClickListener(this);
        tvZxks.setOnClickListener(this);
        tvGzdb.setOnClickListener(this);
        tvJyjl.setOnClickListener(this);
        tvMore.setOnClickListener(this);
        tvRightOperate.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        initAdapter();

        showSkeletonSceen(studyAdapter);

        if(isLogin()){
            accountBean = PreferenceUtil.getUserInfo(getActivity());
            //登录华为平台
            HuaweiLoginImp.getInstance().loginRequest(getActivity(),
                    accountBean.siteAccount,
                    accountBean.sitePwd,
                    Urls.SMC_REGISTER_SERVER,
                    Urls.SMC_REGISTER_PORT,
                    "",
                    "",
                    "",
                    "");

//            LocBroadcast.getInstance().registerBroadcast(this, mActions);
        }else {
//            LocBroadcast.getInstance().unRegisterBroadcast(this, mActions);
        }
    }
    private boolean isLogin(){
        return PreferenceUtil.getBoolean(Constant.AUTO_LOGIN,false);
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.i("StudyFragment", "onPause");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            if (null != studyAdapter) {
                //获取banner数据
                getBannerData();

                //获取热门学习
                getHotStudysRequest();
            }
        }
    }

    /**
     * 获取banner
     */
    private void getBannerData() {
        HttpUtils.getInstance(mContext)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryBanners()
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
//                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData<BannerEntity>>() {
                    @Override
                    public void onSuccess(BaseData<BannerEntity> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result) && baseData.dataList.size() > 0) {
                            initBanner(baseData.dataList);
                        } else {
//                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        ToastHelper.showShort("onError:" + responeThrowable.getCause().getMessage());
                    }
                });
    }

    /**
     * 初始化banner数据
     */
    private void initBanner(List<BannerEntity> bannerList) {
        bannerView.delayTime(5).build(bannerList);
        bannerView.setVisibility(View.VISIBLE);
        bannerView.setOnItemClickListener(new BannerView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                position = position % bannerList.size();
                //查询详情
                queryNewsById(bannerList.get(position).newsId);
            }
        });
    }

    private void queryNewsById(int newsId) {
        HttpUtils.getInstance(mContext)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryNewById(newsId)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<StudyNewsBean>() {
                    @Override
                    public void onSuccess(StudyNewsBean newsBean) {
                        if (!"error".equals(newsBean.result)) {
                            startNewsActivity(newsBean.data);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        ToastHelper.showShort("请求异常");
                    }
                });
    }

    /**
     * 获取热门学习
     */
    private void getHotStudysRequest() {
        Observable.zip(getHotStudys(),
                getHotEducations(),
                new Func2<BaseData<NewsBean>, BaseData<ZtjyBean>, List<StudyListItem>>() {
                    @Override
                    public List<StudyListItem> call(BaseData<NewsBean> hotStudys, BaseData<ZtjyBean> hotEducations) {
                        List<StudyListItem> newList = new ArrayList<>();
                        StudyListItem studyListItem = null;
                        if (BaseData.SUCCESS.equals(hotStudys.result) && hotStudys.dataList.size() > 0) {
                            studyListItem = new StudyListItem(new StudyHeadBean("热门学习", R.mipmap.ic_study_hot));
                            newList.add(studyListItem);
                            for (int i = 0; i < hotStudys.dataList.size(); i++) {
                                newList.add(new StudyListItem(hotStudys.dataList.get(i)));
                            }
                        }

                        if (BaseData.SUCCESS.equals(hotEducations.result) && hotEducations.dataList.size() > 0) {
                            studyListItem = new StudyListItem(new StudyHeadBean("工作动态", R.mipmap.ic_study_work));
                            newList.add(studyListItem);

                            for (int i = 0; i < hotEducations.dataList.size(); i++) {
                                newList.add(new StudyListItem(hotEducations.dataList.get(i)));
                            }
                        }
                        return newList;
                    }
                })
                .subscribe(new RxSubscriber<List<StudyListItem>>() {
                    @Override
                    public void onSuccess(List<StudyListItem> newsBeans) {
                        studyAdapter.replaceData(newsBeans);
                        hideSkeletonScreen();
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        ToastHelper.showShort("onError:" + responeThrowable.getCause().getMessage());
                        hideSkeletonScreen();
                    }
                });
    }

    private Observable getHotStudys() {
        return HttpUtils.getInstance(mContext)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryHotStudys()
                .compose(this.bindToLifecycle()).compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose());
    }

    private Observable getHotEducations() {
        return HttpUtils.getInstance(mContext)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryHotEducations()
                .compose(this.bindToLifecycle()).compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_content:
                SearchActivity.startActivity(getActivity());
                break;

            //新闻资讯
            case R.id.tv_wsbs:
                OrganizationDocumentsActivity.startActivity(getActivity(), OrganizationDocumentsActivity.TYPE_NEWS);
                break;

            //思想理论
            case R.id.tv_sxll:
                NewsActivity.startActivity(getActivity(), NewsActivity.TYPE_SXLL, NewsActivity.TYPE_ZZLL_COLUMN);
                break;

            //党章党规
            case R.id.tv_ylbz:
                TBXActivity.startActivity(getActivity(), Urls.DZDG_URL);
                break;

            //组工文件
            case R.id.tv_zgwj:
                OrganizationDocumentsActivity.startActivity(getActivity(), OrganizationDocumentsActivity.TYPE_DOCUMENTS);
                break;

            //现代远程教育
            case R.id.tv_xdycjy:
                OrganizationDocumentsActivity.startActivity(getActivity(), OrganizationDocumentsActivity.TYPE_YCJY);
                break;

            //工作动态
            case R.id.tv_wsbss:
                WorkingConditionActivity.startActivity(getActivity(), WorkingConditionActivity.TYPE_GZDT);
                break;

            //党务问答
            case R.id.tv_dwwd:
                TBXActivity.startActivity(getActivity(), Urls.DWWD_URL);
                break;

            //在线考试
            case R.id.tv_zxks:
                accountBean = PreferenceUtil.getUserInfo(getActivity());
                if (null != accountBean) {
                    TBXActivity.startActivity(getActivity(), Urls.BASE_URL + "/webApp/examList.html?operatorId=" + accountBean.id);
//                    CommonActivity.startActivity(getActivity(), Urls.BASE_URL + "/webApp/examList.html?operatorId=" + accountBean.id);
                }
                break;

            //我的
            case R.id.tv_right_operate:
                MineActivity.startActivity(getActivity());
                break;
        }
    }

    /**
     * 打开微信小程序
     */
    private void startMiniprogram() {
        IWXAPI api = WXAPIFactory.createWXAPI(getActivity(), Constant.weixinAppId);

        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = Constant.weixiUserName; // 填小程序原始id
        req.path = "";                  //拉起小程序页面的可带参路径，不填默认拉起小程序首页
        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
        api.sendReq(req);
    }

    /**
     * 组织生活
     */
    private void initAdapter() {
        studyAdapter = new StudyNewAdapter(R.layout.item_study_list, new ArrayList<>());
        studyAdapter.addHeaderView(headView);
        //默认情况下无数据时只显示emptyview，调用下面两个方法，无数据也可以显示头部
        studyAdapter.setHeaderAndEmpty(true);
        studyAdapter.setHeaderFooterEmpty(true, true);
        studyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                CurrencyBean currencyBean = studyAdapter.getItem(position).newsBean;

                if (currencyBean instanceof NewsBean) {
                    NewsBean newsBean = (NewsBean) currencyBean;

                    startNewsActivity(newsBean);
                } else if (currencyBean instanceof ZtjyBean) {
                    ZtjyBean ztjyBean = (ZtjyBean) currencyBean;
                    startZtjyActivity(ztjyBean);
                } else {
                    StudyHeadBean studyHeadBean = (StudyHeadBean) currencyBean;
                    if ("热门学习".equals(studyHeadBean.title)) {
                        OrganizationDocumentsActivity.startActivity(getActivity(), OrganizationDocumentsActivity.TYPE_NEWS);
                    } else {
                        WorkingConditionActivity.startActivity(getActivity(), WorkingConditionActivity.TYPE_GZDT);
                    }
                }
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvList.setAdapter(studyAdapter);
    }

    private void startZtjyActivity(ZtjyBean ztjyBean) {
        List<String> urls = new ArrayList<>();
        ContentDetailsBean contentDetailsBean = null;
        StringBuffer stringBuffer = null;
        List<String> strings = null;

        String unitName = "辛集市";

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
                unitName);
        //不显示
        contentDetailsBean.oneLable =ContentDetailsActivity.NOT_SHOW;

        ContentDetailsActivity.startActivity(getActivity(), contentDetailsBean);
    }

    public void startNewsActivity(NewsBean newsBean) {
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

        contentDetailsBean.id =newsBean.id;

        ContentDetailsActivity.startActivity(getActivity(), contentDetailsBean);
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

    private String[] mActions = new String[]{
            CustomBroadcastConstants.GET_CONF_END,
            CustomBroadcastConstants.ACTION_CALL_END,
            CustomBroadcastConstants.ADD_LOCAL_VIEW,
            CustomBroadcastConstants.LOGIN_SUCCESS,
            CustomBroadcastConstants.LOGIN_FAILED
    };

    /***************************************************视频会议-start*****************************************/
    /**
     * 是否添加了本地视频画面
     */
    private boolean isAddLocal = false;

    //当前经纬度
    private double mLatitude;
    private double mLongitude;

    private Subscription confInfoSubscribe;

    /**
     * 停止查询数据
     */
    private void stopConfInfoSubscribe() {
        if (null != confInfoSubscribe) {
            if (!confInfoSubscribe.isUnsubscribed()) {
                confInfoSubscribe.unsubscribe();
                confInfoSubscribe = null;
            }
        }
    }

    /**
     * 查询会议是否有数据
     * 循环查询会议信息
     */
    private void checkAccount() {
        stopConfInfoSubscribe();

        confInfoSubscribe = Observable
                .interval(3, TimeUnit.SECONDS)
                .flatMap(new Func1<Long, Observable<BaseData>>() {
                    @Override
                    public Observable<BaseData> call(Long aLong) {
                        return HttpUtils.getInstance(getActivity())
                                .getRetofitClinet()
                                .setBaseUrl(Urls.BASE_URL)
                                .builder(StudyApi.class)
                                .addLonlat(String.valueOf(mLongitude), String.valueOf(mLatitude));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        CommonLogger.i("checkAccount", "出异常了");
                        checkAccount();
                    }

                    @Override
                    public void onSuccess(BaseData baseData) {
                        Logger.i("checkAccount", baseData.toString());
                        if (baseData.message.contains("当前操作员绑定会场不在会议中")) {
//                            stopConfInfoSubscribe();
                        }
                    }
                });
    }

    /**
     * 监听广播
     *
     * @param broadcastName
     * @param obj
     */
    @Override
    public void onReceive(String broadcastName, Object obj) {
        switch (broadcastName) {
            case CustomBroadcastConstants.GET_CONF_END:
                break;

            case CustomBroadcastConstants.ACTION_CALL_END:
                isAddLocal = false;
                stopConfInfoSubscribe();
                break;

            case CustomBroadcastConstants.ADD_LOCAL_VIEW:
                Log.i("OrganizingLifeFragment", "CustomBroadcastConstants.ADD_LOCAL_VIEW" + System.currentTimeMillis());
                if (!isAddLocal) {
                    isAddLocal = true;
                    checkAccount();
                }
                break;

            case CustomBroadcastConstants.LOGIN_SUCCESS:
                LogUtil.i(UIConstants.DEMO_TAG, "login success");

                LoginCenter.getInstance().getSipAccountInfo().setSiteName(PreferencesHelper.getData(UIConstants.SITE_NAME));
                //是否登录
                PreferencesHelper.saveData(UIConstants.IS_LOGIN, true);
                PreferencesHelper.saveData(UIConstants.REGISTER_RESULT, "0");
                ToastHelper.showShort("华为平台登录成功");
                break;

            case CustomBroadcastConstants.LOGIN_FAILED:
                String errorMessage = ((String) obj);
                LogUtil.i(UIConstants.DEMO_TAG, "login failed," + errorMessage);

                ToastHelper.showShort("华为平台登录失败" + errorMessage);
                retryCount++;
                if (retryCount <= 3) {
                    //登录华为平台
                    HuaweiLoginImp.getInstance().loginRequest(getActivity(), accountBean.siteAccount, accountBean.sitePwd, Urls.SMC_REGISTER_SERVER, Urls.SMC_REGISTER_PORT, "", "", "", "");
                }
                break;

            case CustomBroadcastConstants.LOGOUT:
                LogUtil.i(UIConstants.DEMO_TAG, "logout success");
                break;
        }
    }

    //华为平台登录重试次数
    private int retryCount = 0;
    /***************************************************视频会议-start*****************************************/

    @Override
    public void onDestroy() {

        Log.i(TAG, TAG + "-->onDestroy");
        LocBroadcast.getInstance().unRegisterBroadcast(this, mActions);
        super.onDestroy();
    }
}
