package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jaeger.library.StatusBarUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.commonlibrary.utils.DialogUtils;
import com.zxwl.commonlibrary.utils.DisplayUtil;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.bean.response.VotePeopleDetailsBean;
import com.zxwl.network.bean.response.XspbBean;
import com.zxwl.network.bean.response.XspbDetailsBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.VoteAdapter;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.utils.PreferenceUtil;
import com.zxwl.xinji.widget.VoteDetailsDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * 线上评比详情界面
 */
public class OnlineEvaluationActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvTilte;
    private TextView tvVoteStatus;
    private TextView tvRightOperate;
    private TextView tvTime;
    private TextView tvWebsite;
    private ImageView ivImg;
    private TextView tvActivityTime;
    private TextView tvVoteNotice;
    private RecyclerView rvList;

    private VoteAdapter voteAdapter;

    public static final String XSPB_BEAN = "XSPB_BEAN";

    public static final String VOTE_ID = "VOTE_ID";

    private XspbBean xspbBean;

    private LoginBean.AccountBean accountBean;
    private int voteId;

    //是否有投票
    private boolean isVote = false;
    private View headView;

    public static final int PAGE_SIZE = 10;

    private SmartRefreshLayout refreshLayout;
    private View emptyView;
    private View errorView;
    private int pageNum;

    private List<VotePeopleDetailsBean> sortList = new ArrayList<>();

    public static void startActivity(Context context, XspbBean xspbBean) {
        Intent intent = new Intent(context, OnlineEvaluationActivity.class);
        intent.putExtra(XSPB_BEAN, xspbBean);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, int voteId) {
        Intent intent = new Intent(context, OnlineEvaluationActivity.class);
        intent.putExtra(VOTE_ID, voteId);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvRightOperate = (TextView) findViewById(R.id.tv_right_operate);
        refreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        rvList = (RecyclerView) findViewById(R.id.rv_list);

        emptyView = getLayoutInflater().inflate(R.layout.include_empty, (ViewGroup) rvList.getParent(), false);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVoteCandidateRequest(1);
            }
        });

        errorView = getLayoutInflater().inflate(R.layout.include_error, (ViewGroup) rvList.getParent(), false);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVoteCandidateRequest(1);
            }
        });

        headView = LayoutInflater.from(this).inflate(R.layout.head_vote, (ViewGroup) rvList.getParent(), false);

        ivImg = (ImageView) headView.findViewById(R.id.iv_img);

        tvTilte = (TextView) headView.findViewById(R.id.tv_tilte);

        tvVoteStatus = (TextView) headView.findViewById(R.id.tv_vote_status);

        tvTime = (TextView) headView.findViewById(R.id.tv_time);
        tvWebsite = (TextView) headView.findViewById(R.id.tv_website);
        tvActivityTime = (TextView) headView.findViewById(R.id.tv_activity_time);
        tvVoteNotice = (TextView) headView.findViewById(R.id.tv_vote_notice);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        accountBean = PreferenceUtil.getUserInfo(this);

        tvTopTitle.setText("在线评比");

        voteId = getIntent().getIntExtra(VOTE_ID, -1);

        querySjdbDetail(voteId);
    }

    private void setVoteContent(XspbBean xspbBean) {
        this.xspbBean = xspbBean;

        tvVoteStatus.setText(1 == xspbBean.voteState ? "您已投票" : "仅一次投票机会");

        isVote = (1 == xspbBean.voteState);

        if (TextUtils.isEmpty(xspbBean.picture)) {
            ivImg.setVisibility(View.GONE);
        } else {
            ivImg.setVisibility(View.VISIBLE);

            Glide.with(this)
                    .asBitmap()
                    .load(xspbBean.picture)
                    .into(ivImg);
        }

        tvTilte.setText(xspbBean.name);
        tvWebsite.setText(xspbBean.department);
        tvTime.setText(DateUtil.longToString(xspbBean.createTime, DateUtil.FORMAT_DATE_TIME));

        tvActivityTime.setText(DateUtil.longToString(xspbBean.deadline, DateUtil.FORMAT_DATE_TIME_CHINA));
        tvVoteNotice.setText(xspbBean.notice);

        tvRightOperate.setText("查看排名");

        tvRightOperate.setVisibility(0 == xspbBean.voteState ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(this);
        tvRightOperate.setOnClickListener(this);

        refreshLayout.setEnableRefresh(false);

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                getVoteCandidateRequest(pageNum + 1);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_online_evaluation;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_operate:
                finish();
                break;

            case R.id.tv_right_operate:
                VoteResultActivity.startActivity(OnlineEvaluationActivity.this, xspbBean.name, xspbBean.id);
                break;
        }
    }

    /**
     * 获取投票信息
     */
    private void getVoteCandidateRequest(int pageNum) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryVoteCandidate(pageNum, PAGE_SIZE, voteId)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
//                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData<VotePeopleDetailsBean>>() {
                    @Override
                    public void onSuccess(BaseData<VotePeopleDetailsBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<VotePeopleDetailsBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                voteAdapter.setVote(isVote);
                                if (1 == pageNum) {
                                    sortList.clear();
                                    sortList.addAll(dataList);
                                    setPageNum(1);
                                    voteAdapter.replaceData(dataList);
                                    voteAdapter.setMaxCount(getVoteNumber(voteAdapter.getData()));
                                } else {
                                    sortList.addAll(dataList);
                                    setPageNum(pageNum);
                                    voteAdapter.addData(dataList);
                                    voteAdapter.setMaxCount(getVoteNumber(voteAdapter.getData()));
                                }

                                //进行排序
                                voteSort();
                            } else {
                                if (1 == pageNum) {
                                    voteAdapter.replaceData(new ArrayList<>());
                                } else {
                                    //设置之后，将不会再触发加载事件
                                    refreshLayout.finishLoadMoreWithNoMoreData();
                                }
                            }
                            finishRefresh(pageNum, true);
                        } else {
                            finishRefresh(pageNum, false);
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        finishRefresh(pageNum, false);

                        if (1 == pageNum) {
                            voteAdapter.setEmptyView(errorView);
                        }

                        ToastHelper.showShort("onError:" + responeThrowable.getCause().getMessage());
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

    /**
     * 关闭动画
     *
     * @param pageNum
     */
    private void finishRefresh(int pageNum, boolean success) {
        if (1 == pageNum) {
            refreshLayout.setEnableFooterFollowWhenNoMoreData(true);
        } else {
            refreshLayout.finishLoadMore(success);
        }

        if (1 == pageNum && !success) {
            refreshLayout.setEnableLoadMore(false);
        }
    }

    private void initAdapter() {
        voteAdapter = new VoteAdapter(R.layout.item_vote_new, new ArrayList<>());
        //设置是否已投票
        voteAdapter.setVote(1 == xspbBean.voteState);

        voteAdapter.addHeaderView(headView);
        //默认情况下无数据时只显示emptyview，调用下面两个方法，无数据也可以显示头部
        voteAdapter.setHeaderAndEmpty(true);
        voteAdapter.setHeaderFooterEmpty(true, true);

        voteAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                VotePeopleDetailsBean voteBean = voteAdapter.getItem(position);
                voteRequest(voteBean);
            }
        });

        voteAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                VotePeopleDetailsBean item = voteAdapter.getItem(position);

                //获取item在排序列表中的位置
                int i = sortList.indexOf(item);
                int ranking = item.ranking;

                if (ranking != 0) {
//                    //获取上一位的票数
                    VotePeopleDetailsBean votePeopleDetailsBean = sortList.get(ranking - 1);

                    item.disparity = votePeopleDetailsBean.num - item.num;
                } else {
                    item.disparity = 0;
                }
                item.ranking = (i + 1);
                item.order = position + 1;
                item.isVote = isVote;
                showVoteDetailsDialog(item);
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(voteAdapter);
    }

    private VoteDetailsDialog voteDetailsDialog;

    /**
     * 显示投票详情对话框
     *
     * @param item
     */
    private void showVoteDetailsDialog(VotePeopleDetailsBean item) {
        voteDetailsDialog = new VoteDetailsDialog(this,
                DisplayUtil.getScreenWidth(),
                DisplayUtil.getScreenHeight() * 7 / 10,
                false,
                item);

        voteDetailsDialog.setVoteListener(new VoteDetailsDialog.onVoteListener() {
            @Override
            public void onVoteClick() {
                voteRequest(item);
            }
        });
        voteDetailsDialog.showPopupWindow();
    }

    /**
     * 投票
     */
    private void voteRequest(VotePeopleDetailsBean voteBean) {
        DialogUtils.showProgressDialog(this, "正在投票...");

        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .votePeople(voteId, voteBean.id)
                .compose(this.bindToLifecycle())
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            tvVoteStatus.setText("您已投票");
                            isVote = true;
                            //设置为已投票
                            xspbBean.voteState = 1;
                            xspbBean.voteStateValue = "已评比";

                            voteBean.num++;

                            voteAdapter.setVote(true);
                            voteAdapter.setMaxCount(getVoteNumber(voteAdapter.getData()));
                            voteAdapter.notifyDataSetChanged();

                            tvRightOperate.setVisibility(View.VISIBLE);

                            sortList = voteAdapter.getData();

                            //进行排序
                            voteSort();

                            if (null != voteDetailsDialog && voteDetailsDialog.isShowing()) {
                                voteDetailsDialog.dismiss();
                                voteDetailsDialog = null;
                            }
                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                        DialogUtils.dismissProgressDialog();
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        DialogUtils.dismissProgressDialog();

                        ToastHelper.showShort("onError:" + responeThrowable.getCause().getMessage());
                    }
                });
    }

    /**
     * 获得总票数
     *
     * @return
     */
    private int getVoteNumber(List<VotePeopleDetailsBean> peopleBeanList) {
        int count = 0;
        for (int i = 0; i < peopleBeanList.size(); i++) {
            count += peopleBeanList.get(i).num;
        }
        return count + 1;
    }

    /**
     * 通过票数进行排序
     */
    private void voteSort() {
        Collections.sort(sortList);
    }

    /**
     * 通过id查询投票详情
     */
    private void querySjdbDetail(int voteId) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryVoteDetailById(voteId)
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<XspbDetailsBean>() {
                    @Override
                    public void onSuccess(XspbDetailsBean basedate) {
                        if (BaseData.SUCCESS.equals(basedate.result)) {
                            xspbBean = basedate.data;

                            xspbBean.voteState = Integer.valueOf(basedate.voteState);

                            setVoteContent(basedate.data);

                            initAdapter();

                            //获取候选人
                            getVoteCandidateRequest(1);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        ToastHelper.showShort("投票详情异常");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (isVote) {
            EventBus.getDefault().post(new EventMessage(Messages.UPDATE_XSPB, xspbBean));
        }
        super.onDestroy();
    }

}
