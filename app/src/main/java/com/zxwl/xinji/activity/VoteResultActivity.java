package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.jaeger.library.StatusBarUtil;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.bean.response.VotePeopleBean;
import com.zxwl.network.bean.response.XspbBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.VoteResultAdapter;
import com.zxwl.xinji.base.BaseActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * 投票结果
 */
public class VoteResultActivity extends BaseActivity {
    private RelativeLayout rlTopTitle;
    private ImageView ivBackOperate;
    private TextView tvTopTitle;

    private RoundedImageView ivTwoHead;
    private TextView tvTwoName;
    private TextView tvTwoVoteNumber;

    private RoundedImageView ivOneHead;
    private TextView tvOneName;
    private TextView tvOneVoteNumber;

    private RoundedImageView ivThreeHead;
    private TextView tvThreeName;
    private TextView tvThreeVoteNumber;

    private RecyclerView rvList;

    private LinearLayout llTwo;
    private LinearLayout llThree;

    public static final String XSPB_BEAN = "XSPB_BEAN";

    public static final String TITLE = "TITLE";
    public static final String VOTE_ID = "VOTE_ID";
    private String title;
    private int voteId;

    private XspbBean xspbBean;

    private LoginBean.AccountBean accountBean;

    private VoteResultAdapter voteResultAdapter;

    private View headView;

    private View errorView;

    public static void startActivity(Context context, XspbBean xspbBean) {
        Intent intent = new Intent(context, VoteResultActivity.class);
        intent.putExtra(XSPB_BEAN, xspbBean);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, String title, int voitId) {
        Intent intent = new Intent(context, VoteResultActivity.class);
        intent.putExtra(TITLE, title);
        intent.putExtra(VOTE_ID, voitId);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        rlTopTitle = (RelativeLayout) findViewById(R.id.rl_top_title);
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        rvList = (RecyclerView) findViewById(R.id.rv_list);

        headView = LayoutInflater.from(this).inflate(R.layout.head_vote_result, (ViewGroup) rvList.getParent(), false);

        ivTwoHead = (RoundedImageView) headView.findViewById(R.id.iv_two_head);
        tvTwoName = (TextView) headView.findViewById(R.id.tv_two_name);
        tvTwoVoteNumber = (TextView) headView.findViewById(R.id.tv_two_vote_number);
        ivOneHead = (RoundedImageView) headView.findViewById(R.id.iv_one_head);
        tvOneName = (TextView) headView.findViewById(R.id.tv_one_name);
        tvOneVoteNumber = (TextView) headView.findViewById(R.id.tv_one_vote_number);
        ivThreeHead = (RoundedImageView) headView.findViewById(R.id.iv_three_head);
        tvThreeName = (TextView) headView.findViewById(R.id.tv_three_name);
        tvThreeVoteNumber = (TextView) headView.findViewById(R.id.tv_three_vote_number);

        llTwo = (LinearLayout) headView.findViewById(R.id.ll_two);
        llThree = (LinearLayout) headView.findViewById(R.id.ll_three);

        errorView = getLayoutInflater().inflate(R.layout.include_error, (ViewGroup) rvList.getParent(), false);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVoteRequest();
            }
        });
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        xspbBean = (XspbBean) getIntent().getSerializableExtra(XSPB_BEAN);
        title = getIntent().getStringExtra(TITLE);
        voteId = getIntent().getIntExtra(VOTE_ID, -1);

        tvTopTitle.setText(title);

        initAdapter();

        getVoteRequest();
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
        return R.layout.activity_vote_result_;
    }

    /**
     * 获取投票信息
     */
    private void getVoteRequest() {
        showSkeletonSceen(voteResultAdapter);

        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryVotePeoples(Urls.QUERY_CANDIDATE_LIST_ACTION, voteId)
                .compose(this.bindToLifecycle())
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<VotePeopleBean>>() {
                    @Override
                    public void onSuccess(BaseData<VotePeopleBean> baseData) {
                        hideSkeletonScreen();
                        if (BaseData.SUCCESS.equals(baseData.result) && baseData.dataList.size() > 0) {
                            List<VotePeopleBean> voteBeanList = baseData.dataList;
                            Collections.sort(voteBeanList);

                            for (int i = 0; i < voteBeanList.size(); i++) {
                                VotePeopleBean voteBean = voteBeanList.get(i);
                                switch (i) {
                                    case 0:
                                        tvOneName.setText(voteBean.name);
                                        tvOneVoteNumber.setText(voteBean.count + "票");
                                        setHead(voteBean.pic1, ivOneHead);
                                        break;

                                    case 1:
                                        llTwo.setVisibility(View.VISIBLE);
                                        tvTwoName.setText(voteBean.name);
                                        tvTwoVoteNumber.setText(voteBean.count + "票");
                                        setHead(voteBean.pic1, ivTwoHead);
                                        break;

                                    case 2:
                                        llThree.setVisibility(View.VISIBLE);
                                        tvThreeName.setText(voteBean.name);
                                        tvThreeVoteNumber.setText(voteBean.count + "票");
                                        setHead(voteBean.pic1, ivThreeHead);
                                        break;
                                }
                            }

                            if (voteBeanList.size() > 3) {
                                voteResultAdapter.replaceData(voteBeanList.subList(3, voteBeanList.size()));
                            }
                        } else {
                            voteResultAdapter.setEmptyView(errorView);
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        voteResultAdapter.setEmptyView(errorView);
                        ToastHelper.showShort("onError:" + responeThrowable.getCause().getMessage());
                    }
                });
    }

    /**
     * 组织生活
     */
    private void initAdapter() {
        voteResultAdapter = new VoteResultAdapter(R.layout.item_vote_ranking, new ArrayList<>());
        voteResultAdapter.addHeaderView(headView);
        //默认情况下无数据时只显示emptyview，调用下面两个方法，无数据也可以显示头部
        voteResultAdapter.setHeaderAndEmpty(true);
        voteResultAdapter.setHeaderFooterEmpty(true, true);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(voteResultAdapter);
    }

    private void setHead(String url, ImageView imageView) {
        Glide.with(this)
                .asBitmap()
                .apply(new RequestOptions().placeholder(R.mipmap.ic_minel_head).error(R.mipmap.ic_minel_head))
                .load(url)
                .into(imageView);
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
}
