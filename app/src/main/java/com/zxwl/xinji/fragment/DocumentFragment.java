package com.zxwl.xinji.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flyco.tablayout.SlidingTabLayout;
import com.zxwl.commonlibrary.BaseLazyFragment;
import com.zxwl.commonlibrary.utils.DialogUtils;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.commonlibrary.widget.NoScrollViewPager;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.xinji.R;
import com.zxwl.xinji.activity.SearchActivity;
import com.zxwl.xinji.adapter.MyPagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * author：pc-20171125
 * data:2019/10/17 15:42
 */
public class DocumentFragment extends BaseLazyFragment {
    private SlidingTabLayout tabLayout;
    private NoScrollViewPager vpContent;

    private List<String> mTitles;

    private List<Fragment> mFragments = new ArrayList<>();

    private String title;

    private int currentIndex;

    private LinearLayout llSearch;
    private ImageView ivSearch;


    public DocumentFragment() {
    }

    public static final String TITLE = "TITLE";
    //单位id
    public static final String UNIT_ID = "UNIT_ID";

    private int unitId;

    public static DocumentFragment newInstance(String title) {
        DocumentFragment documentFragment = new DocumentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        documentFragment.setArguments(bundle);
        return documentFragment;
    }

    public static DocumentFragment newInstance(String title, int unitId) {
        DocumentFragment documentFragment = new DocumentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putInt(UNIT_ID, unitId);
        documentFragment.setArguments(bundle);
        return documentFragment;
    }

    @Override
    protected View inflateContentView(LayoutInflater inflater, ViewGroup container) {
        title = getArguments().getString(TITLE);
        boolean isZtdrAndXwqlqd = isZtdr() || isXwqlqd() || isTsbc();
        return inflater.inflate(isZtdrAndXwqlqd ? R.layout.fragment_ztdr : R.layout.fragment_document, container, false);
    }

    @Override
    protected void findViews(View view) {
        tabLayout = (SlidingTabLayout) view.findViewById(R.id.tb_layout);
        vpContent = (NoScrollViewPager) view.findViewById(R.id.vp_content);
        llSearch = (LinearLayout) view.findViewById(R.id.ll_search);
        ivSearch = (ImageView) view.findViewById(R.id.iv_search);
    }

    @Override
    protected void initData() {
        title = getArguments().getString(TITLE);

        unitId = getArguments().getInt(UNIT_ID, -1);

        //初始化请求的url
        initRequestUrl();

        //是否是主题党日模块
        if (isZtdr()) {
            queryZtdrYear();
        } else if (isXwqlqd()) {
            initXwqlqdAdapter();
        } else if (isTsbc()) {
            queryZtdrYear();
        } else {
            llSearch.setVisibility(View.VISIBLE);
            queryFileYear();
        }
    }

    /**
     * 是否是图说本村
     *
     * @return
     */
    private boolean isTsbc() {
        switch (title) {
            case RefreshRecyclerFragment.TYPE_TSBC:
            case RefreshRecyclerFragment.TYPE_RYBZ:
            case RefreshRecyclerFragment.TYPE_LDGZ:
                return true;

            default:
                return false;
        }
    }

    @Override
    protected void addListeners() {
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.startActivity(getActivity(), SearchActivity.TYPE_ZGWJ, "省级".equals(title) ? 1 : 2);
            }
        });
    }

    /**
     * 是否是主题党日模块
     *
     * @return
     */
    private boolean isZtdr() {
        switch (title) {
            case RefreshRecyclerFragment.TYPE_ZTDR:
            case RefreshRecyclerFragment.TYPE_SHYK:
            case RefreshRecyclerFragment.TYPE_MZPY:
            case RefreshRecyclerFragment.TYPE_ZZSHH:
            case RefreshRecyclerFragment.TYPE_MORE:
                return true;

            default:
                return false;
        }
    }

    /**
     * 是否是小微权力清单
     *
     * @return
     */
    private boolean isXwqlqd() {
        switch (title) {
            case RefreshRecyclerFragment.TYPE_CWGZL:
                return true;

            case RefreshRecyclerFragment.TYPE_BMFWL:
                return true;

            default:
                return false;
        }
    }


    /**
     * 查询组工文件年份
     */
    private void queryFileYear() {
        String requestType = "省级".equals(title) ? "1" : "2";

        HttpUtils.getInstance(mContext)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryYear(requestType)
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            if (TextUtils.isEmpty(baseData.data)) {
                                tabLayout.setVisibility(View.GONE);
                            } else {
                                tabLayout.setVisibility(View.VISIBLE);

                                initTab(true, baseData.data);
                            }
                        } else {
                            Toast.makeText(mContext.getApplicationContext(), baseData.message, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        DialogUtils.dismissProgressDialog();
                        Toast.makeText(mContext.getApplicationContext(), "请求失败,error：" + responeThrowable.getCause().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String requetYearUrl = "";

    private void initRequestUrl() {
        switch (title) {
            case RefreshRecyclerFragment.TYPE_ZTDR:
                requetYearUrl = Urls.QUERY_THEME_DAY_YEAR;
                break;

            case RefreshRecyclerFragment.TYPE_SHYK:
                requetYearUrl = Urls.QUERY_THREE_SESSIONS_YEAR;
                break;

            case RefreshRecyclerFragment.TYPE_MZPY:
                requetYearUrl = Urls.QUERY_DEMOCRATIC_YEAR;
                break;

            case RefreshRecyclerFragment.TYPE_ZZSHH:
                requetYearUrl = Urls.QUERY_LIFE_MEETING_YEAR;
                break;

            case RefreshRecyclerFragment.TYPE_MORE:
                requetYearUrl = Urls.QUERY_OTHERS_YEAR;
                break;

            case RefreshRecyclerFragment.TYPE_LDGZ:
                requetYearUrl = Urls.QUERY_LDGZ_YEAR;
                break;

            case RefreshRecyclerFragment.TYPE_RYBZ:
                requetYearUrl = Urls.QUERY_RYBZ_YEAR;
                break;

            case RefreshRecyclerFragment.TYPE_TSBC:
                requetYearUrl = Urls.QUERY_TSBC_YEAR;
                break;
        }
    }

    /**
     * 请求主题党日的年份
     */
    private void queryZtdrYear() {
        HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryThemeDayYear(requetYearUrl)
                .compose(this.bindToLifecycle())
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            if (!TextUtils.isEmpty(baseData.data)) {
                                if (TextUtils.isEmpty(baseData.data)) {
                                    tabLayout.setVisibility(View.GONE);
                                } else {
                                    tabLayout.setVisibility(View.VISIBLE);

                                    initTab(false, baseData.data);
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

    /**
     * 是否是组工文件
     *
     * @param isFile
     */
    private void initTab(boolean isFile, String data) {
        mTitles = Arrays.asList(data.split(","));

        //添加标题
        for (int i = 0; i < mTitles.size(); i++) {//            mTitles.set(i, mTitles.get(i));
            mTitles.set(i, mTitles.get(i));
        }

        if (isFile) {
            for (int i = 0; i < mTitles.size(); i++) {
                mFragments.add(RefreshRecyclerFragment.newInstance(mTitles.get(i), "省级".equals(title) ? 1 : 2));
            }
        } else {
            for (int i = 0; i < mTitles.size(); i++) {
                //得到年份
                int year = Integer.valueOf(mTitles.get(i).substring(0, mTitles.get(i).length() - 1));
                mFragments.add(RefreshRecyclerFragment.newInstance(title, unitId, year));
            }
        }

        initPagerAdapter();
    }

    private void initXwqlqdAdapter() {
        mTitles = new ArrayList<>();
        if (RefreshRecyclerFragment.TYPE_CWGZL.equals(title)) {
            mTitles.add(RefreshRecyclerFragment.TYPE_ZDSXJC);
            mTitles.add(RefreshRecyclerFragment.TYPE_CWGLSX);
            mTitles.add(RefreshRecyclerFragment.TYPE_YGCWSX);
        } else {
            mTitles.add(RefreshRecyclerFragment.TYPE_JZJZKWSPSX);
            mTitles.add(RefreshRecyclerFragment.TYPE_SNFWGLSX);
            mTitles.add(RefreshRecyclerFragment.TYPE_JHSYFWSX);
            mTitles.add(RefreshRecyclerFragment.TYPE_QTSX);
        }

        for (int i = 0; i < mTitles.size(); i++) {
            mFragments.add(RefreshRecyclerFragment.newInstance(mTitles.get(i), 1));
        }

        initPagerAdapter();
    }

    /**
     * 初始化pageradapter
     */
    private void initPagerAdapter() {
        MyPagerAdapter mAdapter = new MyPagerAdapter(getChildFragmentManager(), mFragments, mTitles);
        vpContent.setAdapter(mAdapter);
        tabLayout.setViewPager(vpContent);

        vpContent.setCurrentItem(currentIndex);
        vpContent.setOffscreenPageLimit(mTitles.size());

        tabLayout.setCurrentTab(currentIndex);
        tabLayout.onPageSelected(currentIndex);
    }
}

