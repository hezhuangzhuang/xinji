package com.zxwl.xinji.fragment;


import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.sunfusheng.marqueeview.MarqueeView;
import com.zxwl.commonlibrary.BaseLazyFragment;
import com.zxwl.commonlibrary.utils.DisplayUtil;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.bean.response.NotifBean;
import com.zxwl.network.bean.response.RoundBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.xinji.R;
import com.zxwl.xinji.activity.ConfListActivity;
import com.zxwl.xinji.activity.EventSupervisorActivity;
import com.zxwl.xinji.activity.LoginActivity;
import com.zxwl.xinji.activity.MineActivity;
import com.zxwl.xinji.activity.MonitorActivity;
import com.zxwl.xinji.activity.NotifActivity;
import com.zxwl.xinji.activity.NotifDetailsActivity;
import com.zxwl.xinji.activity.OnlineEvaluationActivity;
import com.zxwl.xinji.activity.RefreshRecyclerActivity;
import com.zxwl.xinji.activity.ScreenViewPagerActivity;
import com.zxwl.xinji.activity.VideoCallActivity;
import com.zxwl.xinji.adapter.OnlineOfficeAdapter;
import com.zxwl.xinji.bean.GovernmentBean;
import com.zxwl.xinji.utils.Constant;
import com.zxwl.xinji.utils.PreferenceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 在线办公
 */
public class OnlineOfficeFragment extends BaseLazyFragment implements View.OnClickListener {
    private TextView tvTopTitle;
    private TextView tvRightOperate;
    private RecyclerView rvList;
    private LinearLayout llBanner;
    private RoundedImageView ivTopBg;
    private RelativeLayout  rlTopTitle;

    private OnlineOfficeAdapter onlineOfficeAdapter;
    private List<GovernmentBean> dataList = new ArrayList<>();

    private LoginBean.AccountBean accountBean;

    private int[] governmentRes = {
            R.mipmap.ic_work_notif,
            R.mipmap.ic_work_sjdb,
            R.mipmap.ic_work_zxpb,

            R.mipmap.ic_work_sphj,
            R.mipmap.ic_work_sphy,
            R.mipmap.ic_work_ssjk,

            R.mipmap.ic_work_jsb,
//            R.mipmap.ic_work_bfzrrgl,
            R.mipmap.ic_work_sjtj
    };

    private String[] governmentString = {
            "通知公告",
            "事件督办",
            "在线评比",

            "视频呼叫",
            "视频会议",
            "实时监控",

            "记事本",
//            "帮扶责任人管理",
            "数据统计"
    };

    private int[] villageGovernmentRes = {
            R.mipmap.ic_work_notif,
            R.mipmap.ic_work_sjdb,
            R.mipmap.ic_work_zxpb,

            R.mipmap.ic_work_sphy,
            R.mipmap.ic_work_ssjk,

            R.mipmap.ic_work_jsb,
//            R.mipmap.ic_work_bfzrrgl,
            R.mipmap.ic_work_sjtj
    };

    private String[] villageGovernmentString = {
            "通知公告",
            "事件督办",
            "在线评比",

            "视频会议",
            "实时监控",

            "记事本",
//            "帮扶责任人管理",
            "数据统计"
    };

    private int[] commonGovernmentRes = {
            R.mipmap.ic_work_notif,
            R.mipmap.ic_work_sjdb,
            R.mipmap.ic_work_zxpb,

            R.mipmap.ic_work_ssjk,

            R.mipmap.ic_work_jsb,
//            R.mipmap.ic_work_bfzrrgl,
            R.mipmap.ic_work_sjtj
    };

    private String[] commonGovernmentString = {
            "通知公告",
            "事件督办",
            "在线评比",

            "实时监控",

            "记事本",
//            "帮扶责任人管理",
            "数据统计"
    };

    private TextView tvMore;
    private MarqueeView marqueeView;

    public OnlineOfficeFragment() {
    }

    public static OnlineOfficeFragment newInstance() {
        OnlineOfficeFragment fragment = new OnlineOfficeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View inflateContentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_online_office, container, false);
    }

    @Override
    protected void findViews(View convertView) {
        tvTopTitle = (TextView) convertView.findViewById(R.id.tv_top_title);
        tvRightOperate = (TextView) convertView.findViewById(R.id.tv_right_operate);
        rvList = (RecyclerView) convertView.findViewById(R.id.rv_list);

        llBanner = (LinearLayout) convertView.findViewById(R.id.ll_banner);
        tvMore = (TextView) convertView.findViewById(R.id.tv_more);
        marqueeView = (MarqueeView) convertView.findViewById(R.id.marqueeView);
        ivTopBg = (RoundedImageView) convertView.findViewById(R.id.iv_top_bg);
        rlTopTitle = (RelativeLayout) convertView.findViewById(R.id.rl_top_title);
    }

    @Override
    protected void addListeners() {
        tvRightOperate.setOnClickListener(this);
        tvMore.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        tvTopTitle.setText("在线办公");

//        ImmersionBar.with(this)
//                // 默认状态栏字体颜色为黑色
////                .statusBarDarkFont(statusBarDarkFont())
//                // 解决软键盘与底部输入框冲突问题，默认为false，还有一个重载方法，可以指定软键盘mode
//                .keyboardEnable(true)
//                .init();
//
//        //设置沉浸式
//        ImmersionBar.setTitleBar(this, rlTopTitle);

        accountBean = PreferenceUtil.getUserInfo(getActivity());

        initAdapter();

        getBackgroundPic();

        //查询最新通知
//        queryRoundList();
    }

    private void initAdapter() {
        //是否隐藏视频功能
        //账号等级为街村或为非管理员

        //不是管理员不显示呼叫按钮
        if (null != accountBean && 1 != accountBean.checkAdmin) {
            for (int i = 0; i < commonGovernmentRes.length; i++) {
                dataList.add(new GovernmentBean(commonGovernmentRes[i], commonGovernmentString[i]));
            }
        } else {
            if (null != accountBean && (3 == accountBean.level)) {
                for (int i = 0; i < villageGovernmentRes.length; i++) {
                    dataList.add(new GovernmentBean(villageGovernmentRes[i], villageGovernmentString[i]));
                }
            } else {
                for (int i = 0; i < governmentRes.length; i++) {
                    dataList.add(new GovernmentBean(governmentRes[i], governmentString[i]));
                }
            }
        }

        onlineOfficeAdapter = new OnlineOfficeAdapter(R.layout.item_online_office, dataList);
        onlineOfficeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!isLogin()) {
                    ToastHelper.showShort("请登录后查看");
                    LoginActivity.startActivity(getActivity());
                    return;
                }
                switch (onlineOfficeAdapter.getItem(position).name) {
                    //通知公告
                    case "通知公告":
                        NotifActivity.startActivity(getActivity());
                        break;

                    //事件督办
                    case "事件督办":
                        RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_SJDB);
                        break;

                    //在线评比
                    case "在线评比":
                        RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_XSPB);
                        break;

                    //视频呼叫
                    case "视频呼叫":
                        VideoCallActivity.startActivity(getActivity(), VideoCallActivity.TYPE_SPHJ);
                        break;

                    //视频会议
                    case "视频会议":
                        ConfListActivity.startActivity(getActivity());
                        break;

                    //实时监控
                    case "实时监控":
                        MonitorActivity.startActivity(getActivity());
                        break;

                    //记事本
                    case "记事本":
                        RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_JSB);
                        break;

                    //帮扶责任人
                    case "帮扶责任人管理":
                        RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_BFZRRGL);
                        break;

                    //数据统计
                    case "数据统计":
                        ScreenViewPagerActivity.startActivity(getActivity(), ScreenViewPagerActivity.TYPE_SJTJ);
                        break;
                }
            }
        });
        rvList.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rvList.setAdapter(onlineOfficeAdapter);

        //设置条目之间的距离
        rvList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int childPosition = parent.getChildPosition(view);

                int offset = DisplayUtil.dp2px(10);
                if (childPosition % 3 == 0) {
                    outRect.set(
                            offset,
                            0,
                            0,
                            offset);
                } else if (childPosition % 3 == 1) {
                    outRect.set(
                            offset,
                            0,
                            0,
                            offset);
                } else if (childPosition % 3 == 2) {
                    outRect.set(
                            offset,
                            0,
                            offset,
                            offset);
                }
            }
        });
    }

    /**
     * 是否有登录
     *
     * @return true：登录，false：没登录
     */
    private boolean isLogin() {
        return PreferenceUtil.getBoolean(Constant.AUTO_LOGIN, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_right_operate:
                MineActivity.startActivity(getActivity());
                break;

            case R.id.tv_more:
                RoundBean roundBean = roundBeanList.get(marqueeView.getPosition());
                //通知公告
                if (RoundBean.TYPE_TZGG == roundBean.type) {
                    NotifActivity.startActivity(getActivity());
                } //事件督办
                else if (RoundBean.TYPE_SJDB == roundBean.type) {
                    RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_SJDB);
                }//在线评比
                else if (RoundBean.TYPE_ZXPB == roundBean.type) {
                    RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_XSPB);
                }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        marqueeView.startFlipping();
    }

    @Override
    public void onStop() {
        super.onStop();
        marqueeView.stopFlipping();
    }

    private List<RoundBean> roundBeanList;

    /**
     * 查询最新通知
     */
    private void queryRoundList() {
        HttpUtils.getInstance(mContext)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryRoundList()
                .compose(this.bindToLifecycle())
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<RoundBean>>() {
                    @Override
                    public void onSuccess(BaseData<RoundBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result) && baseData.dataList.size() > 0) {
                            for (int i = 0; i < baseData.dataList.size(); i++) {
                                RoundBean roundBean = baseData.dataList.get(i);
                                if (RoundBean.TYPE_TZGG == roundBean.type) {
                                    roundBean.imageRes = R.mipmap.ic_work_head_notif;
                                } else if (RoundBean.TYPE_SJDB == roundBean.type) {
                                    roundBean.imageRes = R.mipmap.ic_work_head_sjdb;
                                } else if (RoundBean.TYPE_ZXPB == roundBean.type) {
                                    roundBean.imageRes = R.mipmap.ic_work_head_zxpb;
                                }
                            }
                            roundBeanList = baseData.dataList;
                            marqueeView.startWithList(roundBeanList);
                            marqueeView.setOnItemClickListener(new MarqueeView.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position, TextView textView) {
                                    RoundBean currentRound = roundBeanList.get(position);
                                    marqueeClick(currentRound);
                                }
                            });
                            llBanner.setVisibility(View.VISIBLE);
                        } else {
                            llBanner.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        ToastHelper.showShort("请求异常");
                    }
                });
    }

    /**
     * 按钮的点击事件
     *
     * @param currentRound
     */
    public void marqueeClick(RoundBean currentRound) {
        //通知公告
        if (RoundBean.TYPE_TZGG == currentRound.type) {
            queryNotifById(currentRound.id);
        } //事件督办
        else if (RoundBean.TYPE_SJDB == currentRound.type) {
            EventSupervisorActivity.startActivity(getContext(), currentRound.id);
        }//在线评比
        else if (RoundBean.TYPE_ZXPB == currentRound.type) {
            OnlineEvaluationActivity.startActivity(getActivity(), currentRound.id);
        }
    }

    /**
     * 通过id查询消息
     */
    private void queryNotifById(int notifId) {
        HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryNoticeById(String.valueOf(notifId))
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<NotifBean>() {
                    @Override
                    public void onSuccess(NotifBean baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            NotifDetailsActivity.startActivity(getContext(), baseData);
                        } else {
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        ToastHelper.showShort("通知查询异常");
                    }
                });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    queryRoundList();
                }
            }, 1000);
        }
    }

    private void getBackgroundPic() {
        HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.CREATE_BASE_URL)
                .builder(StudyApi.class)
                .queryBackground(2)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            if (BaseData.SUCCESS.equals(baseData.result) && !TextUtils.isEmpty(baseData.data)) {
                                setAvatar(baseData.data);
                            }
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                    }
                });
    }

    private void setAvatar(String url) {
        Glide.with(mContext)
                .asBitmap()
                .apply(new RequestOptions().placeholder(R.mipmap.ic_top_poster).error(R.mipmap.ic_top_poster))
                .load(url)
                .into(ivTopBg);
    }
}
