package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.jaeger.library.StatusBarUtil;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.commonlibrary.widget.CustomViewPager;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.response.InformationBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.MyPagerAdapter;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.fragment.BackHandleInterface;
import com.zxwl.xinji.fragment.DocumentFragment;
import com.zxwl.xinji.fragment.RefreshRecyclerFragment;
import com.zxwl.xinji.fragment.WebFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 组工文件
 */
public class OrganizationDocumentsActivity extends BaseActivity implements BackHandleInterface {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private SlidingTabLayout tabLayout;
    private CustomViewPager vpContent;

    private List<String> mTitles;

    private List<Fragment> mFragments = new ArrayList<>();

    private int currentIndex;

    public static final String TITLE = "TITLE";
    private String title;

    public static final String TYPE_NEWS = "党建资讯";

    public static final String TYPE_DOCUMENTS = "组工文件";
    public static final String TYPE_XWQLQD = "小微权力清单";
    public static final String TYPE_YCJY = "远程教育";

    public static void startActivity(Context context, String title) {
        Intent intent = new Intent(context, OrganizationDocumentsActivity.class);
        intent.putExtra(TITLE, title);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tabLayout = (SlidingTabLayout) findViewById(R.id.tb_layout);
        vpContent = (CustomViewPager) findViewById(R.id.vp_content);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        title = getIntent().getStringExtra(TITLE);

        tvTopTitle.setText(title);

        initTab();
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_organization_documents;
    }

    private void initTab() {
        getFragments();
    }

    private void initTabLayout() {
        MyPagerAdapter mAdapter = new MyPagerAdapter(getSupportFragmentManager(), mFragments, mTitles);
        vpContent.setAdapter(mAdapter);
        tabLayout.setViewPager(vpContent);

        vpContent.setCurrentItem(currentIndex);
        vpContent.setOffscreenPageLimit(mTitles.size());

        tabLayout.setCurrentTab(currentIndex);
        tabLayout.onPageSelected(currentIndex);
    }

    private void getFragments() {
        mTitles = new ArrayList<>();

        switch (title) {
            case TYPE_DOCUMENTS:
                tabLayout.setTabSpaceEqual(true);
                getDocumentFragments();
                initTabLayout();
                break;

            case TYPE_NEWS:
                tabLayout.setTabSpaceEqual(false);

                getInformations(1);
                break;

            case TYPE_XWQLQD:
                tabLayout.setTabSpaceEqual(true);

                getXwqlqdFragments();

                initTabLayout();
                break;

            //远程教育
            case TYPE_YCJY:
                tabLayout.setTabSpaceEqual(true);

                getYcjyFragments();
                initTabLayout();
                break;
        }
    }

    /**
     * 获取组工文件的fragments
     */
    private void getDocumentFragments() {
        mTitles.add("中央");
        mTitles.add("省级");
        mTitles.add("市级");

        for (int i = 0; i < mTitles.size(); i++) {
            if (0 == i) {
                mFragments.add(WebFragment.newInstance(Urls.ZGWJ_URL, "组工文件_共产党员网"));
//                mFragments.add(AgentWebFragment.getInstance(null, Urls.ZGWJ_URL));
            } else {
                mFragments.add(DocumentFragment.newInstance(mTitles.get(i)));
            }
        }
    }

    public static final String TYPE_XDYCJY = "现代远程教育";
    public static final String TYPE_GCDYDSLM = "共产党员电视栏目";

    /**
     * 获取远程的fragments
     */
    private void getYcjyFragments() {
        mTitles.add(TYPE_XDYCJY);
        mTitles.add(TYPE_GCDYDSLM);

        mFragments.add(WebFragment.newInstance(Urls.XDYCJY_URL, TYPE_XDYCJY));
        mFragments.add(WebFragment.newInstance(Urls.GCDYDSLM_URL, TYPE_GCDYDSLM));
    }

    /**
     * 获取小微权力清单的fragments
     */
    private void getXwqlqdFragments() {
        mTitles.add(RefreshRecyclerFragment.TYPE_DWGZL);
        mTitles.add(RefreshRecyclerFragment.TYPE_CWGZL);
        mTitles.add(RefreshRecyclerFragment.TYPE_BMFWL);

        for (int i = 0; i < mTitles.size(); i++) {
            if (0 == i) {
                mFragments.add(RefreshRecyclerFragment.newInstance(RefreshRecyclerFragment.TYPE_DWGZL, 1));
            } else {
                mFragments.add(DocumentFragment.newInstance(mTitles.get(i)));
            }
        }
    }

    public static final int NEWS_UNIT_ID = 0x10086;

    /**
     * 获取新闻资讯的fragments
     */
    private void getNewFragments(List<InformationBean> informationBeans) {
        mTitles.add(RefreshRecyclerFragment.TYPE_TJ);
        for (int i = 0; i < informationBeans.size(); i++) {
            mTitles.add(informationBeans.get(i).name);
        }
        mTitles.add(RefreshRecyclerFragment.TYPE_DSBC);

        mFragments.add(RefreshRecyclerFragment.newInstance(mTitles.get(0), NEWS_UNIT_ID));

        for (int i = 0; i < informationBeans.size(); i++) {
            mFragments.add(WebFragment.newInstance(informationBeans.get(i).url, mTitles.get(i + 2)));
        }

        mFragments.add(RefreshRecyclerFragment.newInstance(mTitles.get(mTitles.size()-1), NEWS_UNIT_ID));
    }

    private WebFragment backHandleFragment;

    @Override
    public void onBackPressed() {
        //if判断里面就调用了来自Fragment的onBackPressed()
        //一样！！，如果onBackPressed是返回false，就会进入条件内进行默认的操作
        if (null == backHandleFragment || !backHandleFragment.onBackPressed()) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    @Override
    public void onSelectedFragment(WebFragment webFragment) {
        backHandleFragment = webFragment;
    }

    /**
     * 获取新闻资讯
     *
     * @param pageNum
     */
    private void getInformations(int pageNum) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryStudyInfos(pageNum, 10)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<InformationBean>>() {
                    @Override
                    public void onSuccess(BaseData<InformationBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<InformationBean> dataList = baseData.dataList;
                            getNewFragments(dataList);
                            initTabLayout();
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
}
