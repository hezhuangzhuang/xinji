package com.zxwl.xinji.fragment;


import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zxwl.commonlibrary.BaseLazyFragment;
import com.zxwl.commonlibrary.utils.DisplayUtil;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.bean.response.VotePeopleBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.xinji.R;
import com.zxwl.xinji.activity.OrganizationDocumentsActivity;
import com.zxwl.xinji.activity.ProposalActivity;
import com.zxwl.xinji.activity.RefreshRecyclerActivity;
import com.zxwl.xinji.activity.SearchActivity;
import com.zxwl.xinji.activity.TBXActivity;
import com.zxwl.xinji.activity.WorkingConditionActivity;
import com.zxwl.xinji.adapter.GovernmentAdapter;
import com.zxwl.xinji.adapter.section.GovernmentSection;
import com.zxwl.xinji.bean.GovernmentBean;
import com.zxwl.xinji.utils.Constant;
import com.zxwl.xinji.utils.PreferenceUtil;
import com.zxwl.xinji.widget.VoteDetailsDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 便民服务
 */
public class GovernmentWorkFragment extends BaseLazyFragment implements View.OnClickListener {
    private TextView etContent;
    private ImageView ivRightOperate;

    private RecyclerView rvList;
    private GovernmentAdapter governmentAdapter;
    private List<GovernmentSection> list;

    private int[] governmentRes = {
            0,
            R.mipmap.ic_government_fwdt,
            R.mipmap.ic_government_xwqlqd,
            R.mipmap.ic_government_wxy,
            R.mipmap.ic_government_zwfwdh,

            0,
            R.mipmap.ic_government_dwgk,
            R.mipmap.ic_government_cwgk,

            0,
            R.mipmap.ic_government_bffzrgl,
            R.mipmap.ic_government_bfhdzs,

            0,
            R.mipmap.ic_government_cjwt,
            R.mipmap.ic_government_wyzx
    };

    private String[] governmentString = {
            "服务为民",
            "服务大厅",
            "小微权力清单",
            "微心愿",
            "政务服务电话",

            "党务村务公开",
            "党务公开",
            "村务公开",

            "扶贫帮扶",
            "帮扶责任人管理",
            "帮扶活动展示",

            "互动交流",
            "意见建议",
            "我要咨询"
    };


    public static final String TYPE_DWCWGK = "党务村务公开";//党务村务公开
    public static final String TYPE_DWGK = "党务公开";//党务公开
    public static final String TYPE_CWGK = "村务公开";//村务公开


    public static final String TYPE_HJXJ = "换届选举";//换届选举
    public static final String TYPE_DZZHJ = "党组织换届";//党务公开
    public static final String TYPE_CWHHJ = "村委会换届";//村务公开

    public static final String TYPE_FWWM = "服务为民";//小微权力清单
    public static final String TYPE_FWDT = "服务大厅";//服务大厅
    public static final String TYPE_XWQLQD = "小微权力清单";//小微权力清单
    public static final String TYPE_CMSWDB = "村民事务代办";//村民事务代办
    public static final String TYPE_DYLXH = "党员联系户";//志愿服务
    public static final String TYPE_WXY = "微心愿";//微心愿
    public static final String TYPE_ZWFWDH = "政务服务电话";//政务服务电话

    public static final String TYPE_FPBF = "扶贫帮扶";//扶贫帮扶
    public static final String TYPE_BFZRRGL = "帮扶责任人管理";//帮扶责任人管理
    public static final String TYPE_BFHDZS = "帮扶活动展示";//帮扶活动展示

    public static final String TYPE_HDJL = "互动交流";//互动交流
    public static final String TYPE_CJWT = "常见问题";//常见问题
    public static final String TYPE_YJJY = "意见建议";//意见建议
    public static final String TYPE_WYZX = "我要咨询";//我要咨询

    public static final String TYPE_XSPB_ = "";//线上评比
    public static final String TYPE_XSPB = "在线评比";//线上评比

    public static final String TYPE_GZZT = "工作状态";//工作状态
    public static final String TYPE_XXJY = "学习教育";//学习教育
    public static final String TYPE_DCYJ = "调查研究";//调查研究
    public static final String TYPE_JSWT = "检视问题";//检视问题
    public static final String TYPE_ZGLS = "整改落实";//整改落实

    public GovernmentWorkFragment() {
    }

    public static GovernmentWorkFragment newInstance() {
        GovernmentWorkFragment fragment = new GovernmentWorkFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View inflateContentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_government_work, container, false);
    }

    @Override
    protected void findViews(View view) {
        etContent = (TextView) view.findViewById(R.id.et_content);
        ivRightOperate = (ImageView) view.findViewById(R.id.iv_right_operate);
        rvList = view.findViewById(R.id.rv_list);
    }

    @Override
    protected void addListeners() {
        etContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.startActivity(getActivity());
            }
        });
    }

    @Override
    protected void initData() {
        rvList.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        initListData();

        initAdapter();

        if (isLogin()) {
            //查询我要咨询数量
            queryWyzxCount();
        }
    }

    private VoteDetailsDialog voteDetailsDialog;

    /**
     * 显示投票详情
     *
     * @param item
     */
    private void showVoteDetailsDialog(VotePeopleBean item) {
        if (null == voteDetailsDialog) {
            voteDetailsDialog = new VoteDetailsDialog(getActivity(),
                    DisplayUtil.getScreenWidth(),
                    DisplayUtil.getScreenHeight() * 7 / 10,
                    false,
                    null);
        }
        voteDetailsDialog.setVoteListener(new VoteDetailsDialog.onVoteListener() {
            @Override
            public void onVoteClick() {
            }
        });
        voteDetailsDialog.showPopupWindow();
    }

    private void initAdapter() {
        governmentAdapter = new GovernmentAdapter(R.layout.item_government, R.layout.item_government_head, list);
        governmentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (governmentString[position]) {
                    //服务大厅
                    case TYPE_FWDT:
                        TBXActivity.startActivity(getActivity(), Urls.FWDT_URL);
                        break;

                    //党务公开
                    case TYPE_DWGK:
                        RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_DWGK);
                        break;

                    //村务公开
                    case TYPE_CWGK:
                        RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_CWGK);
                        break;

                    //村委会换届
                    case TYPE_CWHHJ:
                        RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_CWHHJ);
                        break;

                    // 党组织换届
                    case TYPE_DZZHJ:
                        RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_DZZHJ);
                        break;

                    //小微权力清单
                    case TYPE_XWQLQD:
                        OrganizationDocumentsActivity.startActivity(getActivity(), OrganizationDocumentsActivity.TYPE_XWQLQD);
                        break;

                    //村民事务代办
                    case TYPE_CMSWDB:
                        RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_CMSWDB);
                        break;

                    //党员联系户
                    case TYPE_DYLXH:
                        RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_DYLXH);
                        break;

                    //微心愿
                    case TYPE_WXY:
                        WorkingConditionActivity.startActivity(getActivity(), WorkingConditionActivity.TYPE_WXY);
                        break;

                    //政务服务电话
                    case TYPE_ZWFWDH:
                        RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_ZWFWDH);
                        break;

                    //帮扶责任人管理
                    case TYPE_BFZRRGL:
                        RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_BFZRRGL);
                        break;

                    //帮扶活动展示
                    case TYPE_BFHDZS:
                        RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_ZYFW);
                        break;

                    //常见问题
                    case TYPE_CJWT:
                        RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_CJWT);
                        break;

                    //线上评比
                    case TYPE_XSPB:
                        RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_XSPB);
                        break;

                    //学习教育
                    case TYPE_XXJY:
                        RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_XXJY);
                        break;

                    //调查研究
                    case TYPE_DCYJ:
                        RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_DCYJ);
                        break;

                    //检视问题
                    case TYPE_JSWT:
                        RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_JSWT);
                        break;

                    //整改落实
                    case TYPE_ZGLS:
                        RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_ZGLS);
                        break;

                    //我要咨询
                    case TYPE_WYZX:
                        governmentAdapter.setUnReadNumber(0);
                        governmentAdapter.notifyDataSetChanged();
                        RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_WYZX);
                        break;

                    //意见建议
                    case TYPE_YJJY:
                        ProposalActivity.startActivity(getActivity(), ProposalActivity.TITLE_YJJY);
                        break;

                    default:
                        break;
                }
            }
        });

        rvList.setAdapter(governmentAdapter);
    }

    private void initListData() {
        list = new ArrayList<>();
        for (int i = 0; i < governmentRes.length; i++) {
            if (0 == governmentRes[i]) {
                list.add(new GovernmentSection(true, governmentString[i]));
            } else {
                list.add(new GovernmentSection(new GovernmentBean(governmentRes[i], governmentString[i])));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_content:
                break;
        }
    }

    private LoginBean.AccountBean accountBean;

    /**
     * 是否有登录
     *
     * @return true：登录，false：没登录
     */
    private boolean isLogin() {
        return PreferenceUtil.getBoolean(Constant.AUTO_LOGIN, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isLogin()) {
            accountBean = PreferenceUtil.getUserInfo(getActivity());
            //查询我要咨询数量
            queryWyzxCount();
        }
    }

    /**
     * 查询我要咨询数量
     */
    private void queryWyzxCount() {
        HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryWyzxUnReadCount(Integer.valueOf(accountBean.id))
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            if (null != governmentAdapter) {
                                governmentAdapter.setUnReadNumber(baseData.replyCount);
                            }
                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        ToastHelper.showShort("onError:" + responeThrowable.getCause().toString());
                    }
                });
    }
}
