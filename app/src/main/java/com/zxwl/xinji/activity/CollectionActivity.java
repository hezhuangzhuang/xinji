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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.jaeger.library.StatusBarUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zxwl.commonlibrary.utils.DialogUtils;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.bean.response.CollectionBean;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.bean.response.NewsBean;
import com.zxwl.network.bean.response.StudyNewsBean;
import com.zxwl.network.bean.response.VideoBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.CollectionAdapter;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.bean.ContentDetailsBean;
import com.zxwl.xinji.utils.PreferenceUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 收藏界面
 */
public class CollectionActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rlTopTitle;
    private ImageView ivBackOperate;
    private ImageView ivArray;
    private TextView tvTopTitle;
    private TextView tvRightOperate;

    private SmartRefreshLayout refreshLayout;
    private RecyclerView rvList;
    private CollectionAdapter collectionAdapter;
    private List<CollectionBean> listBeans;

    private View viewLine;
    private LinearLayout llOperation;
    private TextView tvAllSelect;
    private TextView tvDelete;

    private boolean isEdit = false;//是否处于编辑状态
    private boolean allSelect = false;//是否全选

    private LoginBean.AccountBean accountBean;
    private int pageNum;//当前页码

    private View emptyView;
    private View errorView;

    private int lastPosition;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, CollectionActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        rlTopTitle = (RelativeLayout) findViewById(R.id.rl_top_title);
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        ivArray = (ImageView) findViewById(R.id.iv_array);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvRightOperate = (TextView) findViewById(R.id.tv_right_operate);
        rvList = (RecyclerView) findViewById(R.id.rv_list);

        viewLine = (View) findViewById(R.id.view_line);
        llOperation = (LinearLayout) findViewById(R.id.ll_operation);
        tvAllSelect = (TextView) findViewById(R.id.tv_all_select);
        tvDelete = (TextView) findViewById(R.id.tv_delete);

        refreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);

        emptyView = getLayoutInflater().inflate(R.layout.include_empty, (ViewGroup) rvList.getParent(), false);

        TextView tvRetry = emptyView.findViewById(R.id.tv_retry);
        tvRetry.setText("快去收藏吧");

        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrganizationDocumentsActivity.startActivity(CollectionActivity.this, OrganizationDocumentsActivity.TYPE_NEWS);
            }
        });

        errorView = getLayoutInflater().inflate(R.layout.include_error, (ViewGroup) rvList.getParent(), false);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                querycCollectiont(1);
            }
        });
    }

    @Override
    protected void initData() {
        tvTopTitle.setText("收藏");
        tvRightOperate.setText("编辑");
        tvRightOperate.setVisibility(View.VISIBLE);

        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        accountBean = PreferenceUtil.getUserInfo(this);

        initRecycler();

        querycCollectiont(1);

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

    private void initRecycler() {
        listBeans = new ArrayList<>();
        collectionAdapter = new CollectionAdapter(R.layout.item_collection, listBeans);
        collectionAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                CollectionBean collectionBean = (CollectionBean) adapter.getData().get(position);
                if (isEdit) {
                    collectionBean.isSelect = !collectionBean.isSelect;
                    //判断是否有全选
                    List<Integer> allSelectList = isAllSelect();
                    if (allSelectList.size() > 0) {
                        tvDelete.setText(String.format(getResources().getString(R.string.delete_number), allSelectList.size()));
                        tvDelete.setEnabled(true);
                    } else {
                        tvDelete.setText("删除");
                        tvDelete.setEnabled(false);
                    }

                    if (allSelect) {
                        tvAllSelect.setText("全不选");
                    } else {
                        tvAllSelect.setText("全选");
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    lastPosition = position;
                    //查询详情
                    queryNewsById(Integer.valueOf(collectionBean.itemId));
                }
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(collectionAdapter);

        showSkeletonSceen(collectionAdapter);
    }

    /**
     * 获取全选的列表
     */
    private List<Integer> isAllSelect() {
        //选择的数量
        List<Integer> selectIndexs = new ArrayList<>();
        for (int i = 0; i < listBeans.size(); i++) {
            if (listBeans.get(i).isSelect) {
                selectIndexs.add(i);
            }
        }

        allSelect = (selectIndexs.size() == listBeans.size());
        return selectIndexs;
    }

    /**
     * 设置全部的状态
     */
    private void setSelectStatus(boolean select) {
        for (int i = 0; i < listBeans.size(); i++) {
            listBeans.get(i).isSelect = select;
        }
        collectionAdapter.notifyDataSetChanged();

        if (select) {
            tvAllSelect.setText("全不选");
            tvDelete.setText(String.format(getResources().getString(R.string.delete_number), listBeans.size()));
            tvDelete.setEnabled(true);
        } else {
            tvAllSelect.setText("全选");
            tvDelete.setText("删除");
            tvDelete.setEnabled(false);
        }
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(this);
        tvRightOperate.setOnClickListener(this);
        tvAllSelect.setOnClickListener(this);
        tvDelete.setOnClickListener(this);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                querycCollectiont(1);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                querycCollectiont(pageNum + 1);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_collection;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_operate:
                finish();
                break;

            case R.id.tv_right_operate:
                if (collectionAdapter.getData().size() <= 0) {
                    return;
                }

                isEdit = !isEdit;

                collectionAdapter.setEdit(isEdit);

                setOperationShow();
                break;

            case R.id.tv_all_select:
                isAllSelect();

                setSelectStatus(!allSelect);
                break;

            case R.id.tv_delete:
                List<Integer> allSelect = isAllSelect();
                StringBuilder deleteIndexs = new StringBuilder();
                StringBuilder deleteIds = new StringBuilder();

                for (int i = 0; i < allSelect.size(); i++) {
                    if (i == allSelect.size() - 1) {
                        deleteIndexs.append(allSelect.get(i));
                        deleteIds.append(collectionAdapter.getData().get(allSelect.get(i)).id);
                    } else {
                        deleteIds.append(collectionAdapter.getData().get(allSelect.get(i)).id + ",");
                        deleteIndexs.append(allSelect.get(i) + ",");
                    }
                }

                if (deleteIds.length() <= 0) {
                    return;
                }

                delCollectiontRequest(deleteIndexs.toString(), deleteIds.toString());
                break;
        }
    }

    /**
     * 删除数据
     *
     * @param deleteIndex
     */
    private void deleteRequest(String deleteIndex) {
        Iterator<CollectionBean> it = listBeans.iterator();
        while (it.hasNext()) {
            CollectionBean collectionBean = it.next();
            if (collectionBean.isSelect) {
                if ("0".equals(collectionBean.type)) {
                    EventBus.getDefault().post(new EventMessage(Messages.DEL_NEWS, collectionBean.itemId));
                } else {
                    EventBus.getDefault().post(new EventMessage(Messages.DEL_VIDEO, collectionBean.itemId));
                }
                it.remove();
            }
        }
        tvDelete.setEnabled(false);
        tvDelete.setText("删除");
        if (collectionAdapter.getData().size() > 0) {
            collectionAdapter.notifyDataSetChanged();
        } else {
            collectionAdapter.setEmptyView(emptyView);

            isEdit = false;
            collectionAdapter.setEdit(isEdit);
            setOperationShow();
            collectionAdapter.notifyDataSetChanged();
        }
    }

    private void setOperationShow() {
        if (isEdit) {
            llOperation.setVisibility(View.VISIBLE);
            tvRightOperate.setText("取消");
        } else {
            llOperation.setVisibility(View.GONE);
            tvRightOperate.setText("编辑");
        }
    }

    /**
     * @param deleteIndexs 条目的下标
     * @param deleteIds    条目的收藏id
     */
    private void delCollectiontRequest(String deleteIndexs, String deleteIds) {
        DialogUtils.showProgressDialog(this, "正在删除...");

        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .delCollectiont(deleteIds)
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        DialogUtils.dismissProgressDialog();
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            deleteRequest(deleteIndexs);
                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        DialogUtils.dismissProgressDialog();
                        ToastHelper.showShort("删除收藏失败");
                    }
                });
    }

    /**
     * 获取收藏列表
     *
     * @param pageNum
     */
    private void querycCollectiont(int pageNum) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .querycCollectiont(accountBean.id, pageNum)
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData<CollectionBean>>() {
                    @Override
                    public void onSuccess(BaseData<CollectionBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<CollectionBean> dataList = baseData.dataList;
                            //有数据
                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                    initListBeans(dataList);
                                } else {
                                    setPageNum(pageNum);
                                    initListBeans(dataList);
                                }
                            } else {
                                if (1 == pageNum) {
                                    collectionAdapter.replaceData(new ArrayList<>());
                                    collectionAdapter.setEmptyView(emptyView);
                                } else {
                                    refreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
                                }
                            }
                            hideSkeletonScreen();
                            finishRefresh(pageNum, true);
                        } else {
                            //隐藏骨架图
                            hideSkeletonScreen();

                            finishRefresh(pageNum, false);
                            collectionAdapter.setEmptyView(errorView);
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        //隐藏骨架图
                        hideSkeletonScreen();
                        if (1 == pageNum) {
                            collectionAdapter.setEmptyView(errorView);
                        }
                        finishRefresh(pageNum, false);
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

//    private void initListBeans(List<CollectionBean> dataList, boolean isRefresh) {
//        //如果是刷新则清空之前的数据
//        if (isRefresh) {
//            refreshLayout.resetNoMoreData();
//            listBeans.clear();
//        }
//
//        listBeans.addAll(dataList);
//        adapter.notifyDataSetChanged();
//
//        //如果是加载更多
//        if (!isRefresh && isEdit) {
//            allSelect = false;
//            tvAllSelect.setText("全选");
//        }
//    }

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
     * 设置数据
     */
    private void initListBeans(List<CollectionBean> newsList) {
        //隐藏骨架图
        hideSkeletonScreen();

        if (1 == pageNum) {
            //刷新加载更多状态
            refreshLayout.resetNoMoreData();
            collectionAdapter.replaceData(newsList);
        } else {
            collectionAdapter.addData(newsList);
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


    //EventBus的处理事件函数  该方法可以随意取名 必须为public 必须添加注解并指定线程模型
    //之后EventBus会自动扫描到此函数，进行数据传递
    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getData(EventMessage eventMessage) {
        switch (eventMessage.message) {
            //文章更新
            case Messages.UPDATE_NEWS:
                refreshLayout.autoRefresh();
                break;

            //文章更新
            case Messages.UPDATE_VIDEO:
                VideoBean videoBean = (VideoBean) eventMessage.t;

                if (null != videoBean && 0 == videoBean.collectState) {
                    collectionAdapter.remove(lastPosition);
                    if (collectionAdapter.getItemCount() == 0) {
                        collectionAdapter.setEmptyView(emptyView);
                    }
                }
                break;
        }
    }

    /**
     * 查询id
     *
     * @param newsId
     */
    private void queryNewsById(int newsId) {
        HttpUtils.getInstance(this)
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

        contentDetailsBean.id = newsBean.id;

        ContentDetailsActivity.startActivity(this, contentDetailsBean);
    }

}
