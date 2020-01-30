package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.jaeger.library.StatusBarUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.response.BannerEntity;
import com.zxwl.network.bean.response.InformationBean;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.bean.response.NewsBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.InformationAdapter;
import com.zxwl.xinji.adapter.NewsListAdapter;
import com.zxwl.xinji.adapter.item.MultipleItem;
import com.zxwl.xinji.adapter.item.NewsListItem;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.bean.ContentDetailsBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 新闻资讯
 * 现代远程教育
 */
public class NewsActivity extends BaseActivity {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvRightOperate;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView rvList;

    //标题
    public static final String TITLE = "TITLE";

    public static final String COLUMN_ID = "COLUMN_ID";

    //标题
    private String title;

    //学习类型id
    private int columnId;

    private int pageNum = 0;
    private int PAGE_SIZE = 10;

    private View emptyView;
    private View errorView;

    private LoginBean.AccountBean accountBean;

    //新闻资讯，现代远程教育适配器
    private InformationAdapter informationAdapter;

    private NewsListAdapter newsListAdapter;

    public static final String TYPE_NEWS = "新闻资讯";

    public static final String TYPE_XDYCJY = "现代远程教育";

    public static final String TYPE_SXLL = "思想理论";

    public static final int TYPE_ZZLL_COLUMN = 2;

    public static final String TYPE_ZCFG = "政策法规";

    public static final String TYPE_JYJL = "经验交流";

    public static final String TYPE_LXYZ = "两学一做";

    public static void startActivity(Context context, String title) {
        Intent intent = new Intent(context, NewsActivity.class);
        intent.putExtra(TITLE, title);
        intent.putExtra(COLUMN_ID, -1);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, String title, int columnId) {
        Intent intent = new Intent(context, NewsActivity.class);
        intent.putExtra(TITLE, title);
        intent.putExtra(COLUMN_ID, columnId);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        refreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        rvList = (RecyclerView) findViewById(R.id.rv_list);

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

        columnId = getIntent().getIntExtra(COLUMN_ID, -1);
        title = getIntent().getStringExtra(TITLE);
        tvTopTitle.setText(title);

        //初始化适配器
        initAdapter();

        //获取数据
        getData(1);
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_party_contacts;
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        switch (title) {
            //新闻资讯
            case TYPE_NEWS:
                //现代远程医疗教育
            case TYPE_XDYCJY:
                initInformationAdapter();

                showSkeletonSceen(informationAdapter);
                break;

            //政策法规
            case TYPE_ZCFG:
            //经验交流
            case TYPE_JYJL:
             //两学一做
            case TYPE_LXYZ:
            //思想理论
            case TYPE_SXLL:
                initNewsAdapter();
                showSkeletonSceen(newsListAdapter);
                break;
        }
    }

    /**
     * 学习资讯适配器
     */
    private void initInformationAdapter() {
        informationAdapter = new InformationAdapter(R.layout.item_news, new ArrayList<>());
        informationAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                WebActivity.startActivity(NewsActivity.this, informationAdapter.getItem(position).url);
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(informationAdapter);
    }

    /**
     * 政治理论、政策法规、经验交流适配器
     */
    private void initNewsAdapter() {
        newsListAdapter = new NewsListAdapter(R.layout.layout_banner, new ArrayList<>());
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
                //不显示
                contentDetailsBean.oneLable = ContentDetailsActivity.NOT_SHOW;

                //TODO:详情是否有评论
                contentDetailsBean.isComment = isComment;

                contentDetailsBean.id = newsBean.id;

                ContentDetailsActivity.startActivity(NewsActivity.this, contentDetailsBean);
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(newsListAdapter);
    }

    //是否有评论
    private boolean isComment = false;

    /**
     * 获取数据
     *
     * @param pageNum
     */
    private void getData(int pageNum) {
        switch (title) {
            case TYPE_NEWS:
                getInformations(pageNum);
                break;

            case TYPE_XDYCJY:
                getXdycjys(pageNum);
                break;

            case TYPE_LXYZ:
                getLxyzData(pageNum);
                break;

            //政治理论
            //政策法规
            //经验交流
            case TYPE_SXLL:
            case TYPE_ZCFG:
            case TYPE_JYJL:
                isComment = true;
                getStudyNews(pageNum);
                break;
        }
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
                .queryStudyInfos(pageNum, PAGE_SIZE)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<InformationBean>>() {
                    @Override
                    public void onSuccess(BaseData<InformationBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<InformationBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                } else {
                                    setPageNum(pageNum);
                                }
                                //刷新数据
                                initListBeans(dataList, informationAdapter);
                            } else {
                                if (1 == pageNum) {
                                    informationAdapter.replaceData(new ArrayList<>());
                                    informationAdapter.setEmptyView(emptyView);
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
                            informationAdapter.setEmptyView(errorView);
                        }
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    /**
     * 获取现代远程教育
     *
     * @param pageNum
     */
    private void getXdycjys(int pageNum) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryStudyEdus(pageNum, PAGE_SIZE)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<InformationBean>>() {
                    @Override
                    public void onSuccess(BaseData<InformationBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<InformationBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                } else {
                                    setPageNum(pageNum);
                                }
                                //刷新数据
                                initListBeans(dataList, informationAdapter);
                            } else {
                                if (1 == pageNum) {
                                    informationAdapter.replaceData(new ArrayList<>());
                                    informationAdapter.setEmptyView(emptyView);
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
                            informationAdapter.setEmptyView(errorView);
                        }
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    /**
     * 查询理论学习列表
     *
     * @param pageNum
     */
    private void getStudyNews(int pageNum) {
        HttpUtils.getInstance(this)
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
                                List<NewsListItem> newsListItems = new ArrayList<>();

                                if (1 == pageNum) {
                                    //添加banner图
                                    newsListItems.add(createHeadData());

                                    setPageNum(1);
                                } else {
                                    setPageNum(pageNum);
                                }

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

    private NewsListItem createHeadData() {
        List<BannerEntity> list = new ArrayList<>();

        list.add(getBannerEntity(Urls.XXPT_IMAGE_URL, Urls.XXPT_URL));
        list.add(getBannerEntity(Urls.ZYJH_IMAGE_URL, Urls.ZYJH_URL));
        list.add(getBannerEntity(Urls.SXLL_IMAGE_URL, Urls.SXLL_URL));

        return new NewsListItem(true, "头部", list);
    }

    @NonNull
    private BannerEntity getBannerEntity(String url, String link) {
        BannerEntity bannerEntity;
        bannerEntity = new BannerEntity();
        bannerEntity.name = "";
        bannerEntity.url = url;
        bannerEntity.link = link;
        return bannerEntity;
    }

    /**
     * 获取两学一做
     *
     * @param pageNum
     */
    private void getLxyzData(int pageNum) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryLxyzs(pageNum, PAGE_SIZE)
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
                                    //一张小图
                                    dataList.get(i).titleType = MultipleItem.TEXT_SMALL_VIDEO;
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

}
