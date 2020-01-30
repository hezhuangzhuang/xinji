package com.zxwl.xinji.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zxwl.commonlibrary.BaseLazyFragment;
import com.zxwl.commonlibrary.utils.FontsUtils;
import com.zxwl.commonlibrary.widget.banner.BannerView;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.bean.response.BannerEntity;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.bean.response.NewsBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.NewsListAdapter;
import com.zxwl.xinji.adapter.item.NewsListItem;
import com.zxwl.xinji.utils.PreferenceUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 关注
 */
public class FollowFragment extends BaseLazyFragment implements View.OnClickListener {
    private TextView tvIntegral;
    private TextView tvRightOperate;
    private ImageView ivSearch;

    private SmartRefreshLayout refreshLayout;
    private RecyclerView rvList;

    private NewsListAdapter adapter;
    private List<NewsListItem> listBeans;

    private int pageNum = 0;
    private int PAGE_SIZE = 10;

    private View emptyView;
    private View errorView;

    private LoginBean.AccountBean accountBean;

    public FollowFragment() {
    }

    public static FollowFragment newInstance() {
        FollowFragment fragment = new FollowFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View inflateContentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_follow, container, false);
    }

    @Override
    protected void findViews(View view) {
        tvIntegral = (TextView) view.findViewById(R.id.tv_integral);
        tvRightOperate = (TextView) view.findViewById(R.id.tv_right_operate);
        refreshLayout = (SmartRefreshLayout) view.findViewById(R.id.refreshLayout);
        rvList = (RecyclerView) view.findViewById(R.id.rv_list);
        ivSearch = (ImageView) view.findViewById(R.id.iv_right_operate);

        emptyView = getActivity().getLayoutInflater().inflate(R.layout.include_empty, (ViewGroup) rvList.getParent(), false);

        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.autoRefresh();
            }
        });

        errorView = getActivity().getLayoutInflater().inflate(R.layout.include_error, (ViewGroup) rvList.getParent(), false);

        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.autoRefresh();
            }
        });
    }

    @Override
    protected void addListeners() {
        tvIntegral.setOnClickListener(this);
        tvRightOperate.setOnClickListener(this);
        ivSearch.setOnClickListener(this);

        refreshLayout.setEnableFooterFollowWhenNoMoreData(true);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
//                getData(1);
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
//                getData(pageNum + 1);
            }
        });
    }

    @Override
    protected void initData() {
        FontsUtils.setSingleFontSerif(tvIntegral);

        initListAdapter();

        refreshLayout.autoRefresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    private void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    private void getData(int pageNum) {
        HttpUtils.getInstance(mContext)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryNews(1, pageNum)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
//                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData<NewsBean>>() {
                    @Override
                    public void onSuccess(BaseData<NewsBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<NewsBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                    initListBeans(dataList, true);
                                } else {
                                    setPageNum(pageNum);
                                    initListBeans(dataList, false);
                                }
                            } else {
                                if (1 == pageNum) {
                                    adapter.replaceData(new ArrayList<>());
                                    adapter.setEmptyView(emptyView);
                                } else {
                                    refreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
                                }
                                hideSkeletonScreen();
                            }
                            finishRefresh(pageNum, true);
                        } else {
                            //隐藏骨架图
                            hideSkeletonScreen();

                            finishRefresh(pageNum, false);
                            Toast.makeText(mContext.getApplicationContext(), baseData.message, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        //隐藏骨架图
                        hideSkeletonScreen();
                        finishRefresh(pageNum, false);
                        if (1 == pageNum) {
                            adapter.setEmptyView(errorView);
                        }
                        Toast.makeText(mContext.getApplicationContext(), "请求失败,error：" + responeThrowable.getCause().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
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

    private void initListBeans(List<NewsBean> dataList, boolean isRefresh) {
        //获取里面的banner
        List<BannerEntity> bannerEntities = new ArrayList<>();
        List<NewsListItem> newsList = new ArrayList<>();

        initNewList(dataList, bannerEntities, newsList);

        //如果是刷新则清空之前的数据
        initHeadView(bannerEntities);

        //隐藏骨架图
        hideSkeletonScreen();

        if (pageNum == 1) {
            //刷新加载更多状态
            refreshLayout.resetNoMoreData();
            adapter.replaceData(newsList);
        } else {
            adapter.addData(newsList);
        }
    }

    private void initNewList(List<NewsBean> dataList, List<BannerEntity> bannerEntities, List<NewsListItem> newsList) {
        for (int i = 0; i < dataList.size(); i++) {
            NewsBean newsBean = dataList.get(i);

            BannerEntity bannerEntity = null;
//            if ("是".equals(newsBean.bannerValue)) {
//                bannerEntities.add(bannerEntity);
//            } else {
//                newsList.add(new NewsListItem(newsBean));
//            }
        }
    }

    private void initHeadView(List<BannerEntity> bannerEntities) {
        if (bannerEntities.size() > 0) {
            //添加Header
            View header = LayoutInflater.from(mContext).inflate(R.layout.layout_banner, rvList, false);
            BannerView banner = (BannerView) header;

            banner.setOnItemClickListener(new BannerView.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    if (null != bannerEntities && bannerEntities.size() > 0 && position < bannerEntities.size()) {
                        BannerEntity bannerEntity = bannerEntities.get(position);

                    }
                }
            });
            banner.delayTime(5).build(bannerEntities);
            adapter.setHeaderView(header);
        }
    }

    private void initListAdapter() {
        listBeans = new ArrayList<>();
        adapter = new NewsListAdapter(R.layout.layout_banner, listBeans);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                lastPosition = position;
                NewsListItem item = (NewsListItem) adapter.getItem(position);
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        showSkeletonSceen();
    }

    private void showSkeletonSceen() {
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

    private void hideSkeletonScreen() {
        if (null != skeletonScreen) {
            skeletonScreen.hide();
            skeletonScreen = null;
        }
        //隐藏骨架图
//        skeletonScreen.hide();
    }

    private SkeletonScreen skeletonScreen;


    @Override
    public void onResume() {
        super.onResume();

        accountBean = PreferenceUtil.getUserInfo(mContext);

//        queryTotalPoint();

        if (!EventBus.getDefault().isRegistered(this)) {
            //注册EventBus,注意参数是this，传入activity会报错
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            //取消EventBus注册
            EventBus.getDefault().unregister(this);
        }
    }



    private int lastPosition;//最后一次点击的下标


    //EventBus的处理事件函数  该方法可以随意取名 必须为public 必须添加注解并指定线程模型
    //之后EventBus会自动扫描到此函数，进行数据传递
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getData(EventMessage eventMessage) {
        switch (eventMessage.message) {
            case Messages.DEL_NEWS:
                setCollectionStatus(eventMessage.succeed);
                break;

            //文章更新
            case Messages.UPDATE_NEWS:
//                NewsBean bean = (NewsBean) eventMessage.t;
//                NewsBean newsBean = adapter.getData().get(lastPosition).newsBean;

                //最后改变的bean对象
                int updatePosition = -1;

                NewsBean bean = (NewsBean) eventMessage.t;
                NewsBean newsBean = null;

                //判断是不是banner对象
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    if (bean.id == adapter.getItem(i).newsBean.id) {
                        updatePosition = i;
                        newsBean = adapter.getItem(i).newsBean;
                        break;
                    }
                }

                //更新列表
                if (-1 != updatePosition) {
                    adapter.notifyItemChanged(lastPosition);
                    adapter.notifyDataSetChanged();
                    rvList.scrollToPosition(lastPosition);
                }

                //更新数据
                if (null != newsBean) {
//                    newsBean.commentNum = bean.commentNum;
//                    newsBean.collectionId = bean.collectionId;
//                    newsBean.collectState = bean.collectState;
                }
                break;
        }
    }

    private void setCollectionStatus(String id) {
        //是否删除
        boolean isDel = false;
        List<NewsListItem> data = adapter.getData();

        for (int j = 0; j < data.size(); j++) {
            if (data.get(j).newsBean.id == Integer.valueOf(id)) {
                data.get(j).newsBean.collectState = 0;
                isDel = true;
            }
        }
    }


}
