package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.zxwl.ecsdk.utils.IntentConstant;
import com.zxwl.frame.activity.VideoConfActivity;
import com.zxwl.frame.inter.HuaweiCallImp;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.bean.response.ConfBean;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.App;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.ConfListAdapter;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.utils.PreferenceUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 会议列表
 */
public class ConfListActivity extends BaseActivity {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView rvList;
    private TextView tvCreateConf;

    private int pageNum = 0;
    private int PAGE_SIZE = 10;

    private View emptyView;
    private View errorView;
    private ImageView ivEmptyImg;
    private TextView tvEmptyContent;
    private TextView tvRetry;

    private ConfListAdapter confAdapter;

    // 1=等待审批、2=等待召开、3=正在召开、4=会议结束、5=审批驳回、6=审批超时、7=取消会议、8=会议异常（默认传3，查询正在召开的会议列表）
    private int confState = 3;

    private LoginBean.AccountBean accountBean;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ConfListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        refreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        rvList = (RecyclerView) findViewById(R.id.rv_list);
        tvCreateConf = (TextView) findViewById(R.id.tv_create_conf);

        emptyView = getLayoutInflater().inflate(R.layout.include_empty, (ViewGroup) rvList.getParent(), false);
        ivEmptyImg = (ImageView) emptyView.findViewById(R.id.iv_empty_img);
        tvEmptyContent = (TextView) emptyView.findViewById(R.id.tv_empty_content);
        tvRetry = (TextView) emptyView.findViewById(R.id.tv_retry);

        ivEmptyImg.setImageResource(R.mipmap.ic_conf_empty);
        tvEmptyContent.setText("暂无视频会议...");

        tvRetry.setVisibility(View.GONE);

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

        register();

        tvTopTitle.setText("视频会议");

        accountBean = PreferenceUtil.getUserInfo(this);

        initConfAdapter();

        queryConfData(1);

        //是否隐藏视频功能
        //账号等级为街村或为非管理员
        boolean isHideVideo = 3 == accountBean.level || 1 != accountBean.checkAdmin;

        tvCreateConf.setVisibility(isHideVideo ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvCreateConf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoCallActivity.startActivity(ConfListActivity.this, VideoCallActivity.TYPE_SPHY);
            }
        });

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                queryConfData(1);
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                queryConfData(pageNum + 1);
            }
        });

        refreshLayout.setEnableLoadMore(false);
    }

    /**
     * 获取会议列表
     */
    private void queryConfData(int pageNum) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
//                .setBaseUrl(!TextUtils.isEmpty(Urls.CREATE_BASE_URL)?Urls.BASE_URL:"")
                .setBaseUrl(Urls.CREATE_BASE_URL)
                .builder(StudyApi.class)
                .queryVideoConfs(pageNum, PAGE_SIZE, confState, accountBean.id)
                .compose(this.bindToLifecycle())
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<ConfBean>>() {
                    @Override
                    public void onSuccess(BaseData<ConfBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<ConfBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                } else {
                                    setPageNum(pageNum);
                                }
                                //刷新数据
                                initListBeans(dataList, confAdapter);
                            } else {
                                if (1 == pageNum) {
                                    confAdapter.replaceData(new ArrayList<>());
                                    confAdapter.setEmptyView(emptyView);
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
                            confAdapter.setEmptyView(errorView);
                        }
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    /**
     * 初始化我要咨询适配器
     */
    public void initConfAdapter() {
        confAdapter = new ConfListAdapter(R.layout.item_conf_list, new ArrayList<>());
        confAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                ConfBean confBean = confAdapter.getItem(position);

                if (view instanceof ConstraintLayout) {
                    VideoConfDetailsActivity.startActivity(ConfListActivity.this, confBean);
                }//加入会议
                else {
                    if (!TextUtils.isEmpty(confBean.accessCode)) {
                        if (null != com.zxwl.frame.utils.AppManager.getInstance().getActivity(VideoConfActivity.class)) {
                            Intent intent = new Intent(IntentConstant.VIDEO_CONF_ACTIVITY_ACTION);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
                            App.getInstance().startActivity(intent);
                        } else {
                            ToastHelper.showShort("正在加入会议...");
                            HuaweiCallImp.getInstance().joinConf(confBean.accessCode);
                        }
                    } else {
                        ToastHelper.showShort("会议接入码为空...");
                    }
                }
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(confAdapter);

        showSkeletonSceen(confAdapter);
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


    @Override
    protected int getLayoutId() {
        return R.layout.activity_conf_list;
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

    //EventBus的处理事件函数  该方法可以随意取名 必须为public 必须添加注解并指定线程模型
    //之后EventBus会自动扫描到此函数，进行数据传递
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshLayout(EventMessage eventMessage) {
        switch (eventMessage.message) {
            //更新会议列表
            case Messages.REFRESH_RECYCLER:
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
