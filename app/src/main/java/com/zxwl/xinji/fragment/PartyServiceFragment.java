package com.zxwl.xinji.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.zxwl.commonlibrary.BaseLazyFragment;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.commonlibrary.widget.banner.BannerView;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.response.BannerEntity;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.xinji.R;
import com.zxwl.xinji.activity.LoginActivity;
import com.zxwl.xinji.activity.MineActivity;
import com.zxwl.xinji.activity.OrganizationDocumentsActivity;
import com.zxwl.xinji.activity.ProposalActivity;
import com.zxwl.xinji.activity.RefreshRecyclerActivity;
import com.zxwl.xinji.activity.ScreenViewPagerActivity;
import com.zxwl.xinji.activity.ServiceMoreActivity;
import com.zxwl.xinji.activity.TBXActivity;
import com.zxwl.xinji.activity.VideoPlayActivity;
import com.zxwl.xinji.utils.Constant;
import com.zxwl.xinji.utils.PreferenceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 党群服务
 */
public class PartyServiceFragment extends BaseLazyFragment implements View.OnClickListener {
    private TextView tvTopTitle;
    private TextView tvRightOperate;
    private RoundedImageView ivTopBg;
    private RelativeLayout rlTopTitle;

    private FrameLayout flImg;

    private BannerView bannerView;

    private ImageView ivZyfw;
    private ImageView ivSgdz;
    private ImageView ivDylxh;
    private ImageView ivWxy;
    private ImageView ivFzdygc;
    private ImageView ivZzgxzj;
    private TextView tvWsbs;
    private TextView tvWsbss;
    private TextView tvYlbz;
    private TextView tvNccqjy;
    private TextView tvPfsf;
    private TextView tvGjjcx;
    private ImageView ivXwqlqd;
    private ImageView ivZwgk;
    private ImageView ivCwgk;
    private ImageView ivDwgk;
    private TextView tvShfwMore;
    private TextView tvTqyb;
    private TextView tvKdcx;
    private TextView tvGslk;
    private TextView tvWzcx;
    private TextView tvDszb;
    private TextView tvHcpyd;
    private ImageView ivYjjy;
    private ImageView ivWyzx;

    private TextView tvYjjy;
    private TextView tvWyzx;

    private ImageView ivZtsj;

    public PartyServiceFragment() {
    }

    public static PartyServiceFragment newInstance() {
        PartyServiceFragment fragment = new PartyServiceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View inflateContentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_party_service, container, false);
    }

    @Override
    protected void findViews(View convertView) {
        flImg = (FrameLayout) convertView.findViewById(R.id.fl_img);
        rlTopTitle = (RelativeLayout) convertView.findViewById(R.id.rl_top_title);
        tvTopTitle = (TextView) convertView.findViewById(R.id.tv_top_title);
        tvRightOperate = (TextView) convertView.findViewById(R.id.tv_right_operate);
        ivZyfw = (ImageView) convertView.findViewById(R.id.iv_zyfw);
        ivSgdz = (ImageView) convertView.findViewById(R.id.iv_sgdz);
        ivDylxh = (ImageView) convertView.findViewById(R.id.iv_dylxh);
        ivWxy = (ImageView) convertView.findViewById(R.id.iv_wxy);
        ivFzdygc = (ImageView) convertView.findViewById(R.id.iv_fzdygc);
        ivZzgxzj = (ImageView) convertView.findViewById(R.id.iv_zzgxzj);
        tvWsbs = (TextView) convertView.findViewById(R.id.tv_wsbs);
        tvWsbss = (TextView) convertView.findViewById(R.id.tv_wsbss);
        tvYlbz = (TextView) convertView.findViewById(R.id.tv_ylbz);
        tvNccqjy = (TextView) convertView.findViewById(R.id.tv_nccqjy);
        tvPfsf = (TextView) convertView.findViewById(R.id.tv_pfsf);
        tvGjjcx = (TextView) convertView.findViewById(R.id.tv_gjjcx);
        ivXwqlqd = (ImageView) convertView.findViewById(R.id.iv_xwqlqd);
        ivZwgk = (ImageView) convertView.findViewById(R.id.iv_zwgk);
        ivCwgk = (ImageView) convertView.findViewById(R.id.iv_cwgk);
        ivDwgk = (ImageView) convertView.findViewById(R.id.iv_dwgk);
        tvShfwMore = (TextView) convertView.findViewById(R.id.tv_shfw_more);
        tvTqyb = (TextView) convertView.findViewById(R.id.tv_tqyb);
        tvKdcx = (TextView) convertView.findViewById(R.id.tv_kdcx);
        tvGslk = (TextView) convertView.findViewById(R.id.tv_gslk);
        tvWzcx = (TextView) convertView.findViewById(R.id.tv_wzcx);
        tvDszb = (TextView) convertView.findViewById(R.id.tv_dszb);
        tvHcpyd = (TextView) convertView.findViewById(R.id.tv_hcpyd);
        ivYjjy = (ImageView) convertView.findViewById(R.id.iv_yjjy);
        ivWyzx = (ImageView) convertView.findViewById(R.id.iv_wyzx);
        tvYjjy = (TextView) convertView.findViewById(R.id.tv_yjjy);
        tvWyzx = (TextView) convertView.findViewById(R.id.tv_wyzx);
        ivZtsj = (ImageView) convertView.findViewById(R.id.iv_ztsj);
        ivTopBg = (RoundedImageView) convertView.findViewById(R.id.iv_top_bg);
        bannerView = (BannerView) convertView.findViewById(R.id.banner);
    }

    @Override
    protected void addListeners() {
        tvRightOperate.setOnClickListener(this);
        ivZyfw.setOnClickListener(this);
        ivSgdz.setOnClickListener(this);
        ivDylxh.setOnClickListener(this);
        ivWxy.setOnClickListener(this);
        ivFzdygc.setOnClickListener(this);
        ivZzgxzj.setOnClickListener(this);
        tvWsbs.setOnClickListener(this);
        tvWsbss.setOnClickListener(this);
        tvYlbz.setOnClickListener(this);
        tvNccqjy.setOnClickListener(this);
        tvPfsf.setOnClickListener(this);
        tvGjjcx.setOnClickListener(this);
        ivXwqlqd.setOnClickListener(this);
        ivZwgk.setOnClickListener(this);
        ivCwgk.setOnClickListener(this);
        ivDwgk.setOnClickListener(this);
        tvShfwMore.setOnClickListener(this);
        tvTqyb.setOnClickListener(this);
        tvKdcx.setOnClickListener(this);
        tvGslk.setOnClickListener(this);
        tvWzcx.setOnClickListener(this);
        tvDszb.setOnClickListener(this);
        tvHcpyd.setOnClickListener(this);
        ivYjjy.setOnClickListener(this);
        ivWyzx.setOnClickListener(this);
        tvYjjy.setOnClickListener(this);
        tvWyzx.setOnClickListener(this);
        ivZtsj.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        tvTopTitle.setText("党群服务");

//        ImmersionBar.with(this)
//                // 默认状态栏字体颜色为黑色
////                .statusBarDarkFont(statusBarDarkFont())
//                // 解决软键盘与底部输入框冲突问题，默认为false，还有一个重载方法，可以指定软键盘mode
//                .keyboardEnable(true)
//                .init();
//
//        //设置沉浸式
//        ImmersionBar.setTitleBar(this, rlTopTitle);

        getBackgroundPic();
    }

    @Override
    public void onClick(View v) {
        if (isCheckLogin(v.getId()) && !isLogin()) {
            ToastHelper.showShort("请登录后查看");
            LoginActivity.startActivity(getActivity());
            return;
        }

        switch (v.getId()) {
            /****党员服务*****/
            case R.id.iv_zyfw:
                RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_ZYFW);
                break;

            case R.id.iv_sgdz:
                RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_SGDZ);
                break;

            case R.id.iv_dylxh:
                RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_DYLXH);
                break;

            case R.id.iv_wxy:
                ScreenViewPagerActivity.startActivity(getActivity(), ScreenViewPagerActivity.TYPE_WXY);
                break;

            case R.id.iv_fzdygc:
                RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_FZDYGC);
                break;

            case R.id.iv_zzgxzj:
                RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_ZZGXZJ);
                break;

            /****便民服务*****/
            case R.id.tv_wsbs:
                startWebActivity(Urls.WSBS_URL);
                break;

            case R.id.tv_wsbss:
                startWebActivity(Urls.WSBShui_URL);
                break;

            case R.id.tv_ylbz:
                startWebActivity(Urls.YLBZ_URL);
                break;

            case R.id.tv_nccqjy:
                startWebActivity(Urls.NCCQJY_URL);
                break;

            case R.id.tv_pfsf:
                startWebActivity(Urls.PFSF_URL);
                break;

            case R.id.tv_gjjcx:
                startWebActivity(Urls.GJJCX_URL);
                break;

            /****阳光服务*****/
            //小微权力清单
            case R.id.iv_xwqlqd:
                OrganizationDocumentsActivity.startActivity(getActivity(), OrganizationDocumentsActivity.TYPE_XWQLQD);
                break;

            case R.id.iv_zwgk:
                startWebActivity(Urls.ZWGK_URL);
                break;

            case R.id.iv_cwgk:
                RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_CWGK);
                break;

            case R.id.iv_dwgk:
                startWebActivity(Urls.JJJC_URL);
                break;

            /****生活服务*****/
            case R.id.tv_tqyb:
                startWebActivity(Urls.TQYB_URL);
                break;

            case R.id.tv_kdcx:
                startWebActivity(Urls.KDCX_URL);
                break;

            case R.id.tv_gslk:
                VideoPlayActivity.startActivity(getActivity());
                break;

            case R.id.tv_wzcx:
                startWebActivity(Urls.WZCX_URL);
                break;

            case R.id.tv_dszb:
                startWebActivity(Urls.DSZB_URL);
                break;

            case R.id.tv_hcpyd:
                startWebActivity(Urls.HCPYD_URL);
                break;

            case R.id.iv_ztsj:
                startWebActivity(Urls.ZTSJ_URL);
                break;

            case R.id.tv_shfw_more:
                ServiceMoreActivity.startActivity(getActivity());
                break;

            /****互动交流*****/
            case R.id.iv_yjjy:
            case R.id.tv_yjjy:
                ProposalActivity.startActivity(getActivity(), ProposalActivity.TITLE_YJJY);
                break;

            case R.id.iv_wyzx:
            case R.id.tv_wyzx:
                RefreshRecyclerActivity.startActivity(getActivity(), RefreshRecyclerActivity.TYPE_WYZX);
                break;

            case R.id.tv_right_operate:
                MineActivity.startActivity(getActivity());
                break;
        }
    }

    /**
     * 跳转到webactivity
     *
     * @param url
     */
    private void startWebActivity(String url) {
        TBXActivity.startActivity(getContext(), url);
    }

    /**
     * 是否需要检查登录
     *
     * @param viewId
     * @return
     */
    private boolean isCheckLogin(int viewId) {
        switch (viewId) {
            //政务服务
            case R.id.iv_zyfw:
                return true;

            //设岗定责
            case R.id.iv_sgdz:
                return true;

            //党员联系户
            case R.id.iv_dylxh:
                return true;

            //村务公开
            case R.id.iv_cwgk:
                return true;

            //党务公开
            case R.id.iv_dwgk:
                return true;

            //我要咨询
            case R.id.iv_wyzx:
                return true;

            default:
                return false;
        }
    }

    private boolean isLogin() {
        return PreferenceUtil.getBoolean(Constant.AUTO_LOGIN, false);
    }

    private void getBackgroundPic() {
        HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.CREATE_BASE_URL)
                .builder(StudyApi.class)
                .queryBackground(1)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<String>>() {
                    @Override
                    public void onSuccess(BaseData<String> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            if (BaseData.SUCCESS.equals(baseData.result) && baseData.dataList.size() > 0) {
//                                setAvatar(baseData.data);
                                initBanner(getBannerList(baseData.dataList));
                            }else {
                                flImg.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                    }
                });
    }

    private List<BannerEntity> getBannerList(List<String> dataList) {
        List<BannerEntity> list = new ArrayList<>();
        BannerEntity bannerEntity = null;

        for(String url:dataList){
            bannerEntity = new BannerEntity();
            bannerEntity.url = url;
            list.add(bannerEntity);
        }
        return list;
    }

    private void setAvatar(String url) {
        Glide.with(mContext)
                .asBitmap()
                .apply(new RequestOptions().placeholder(R.mipmap.ic_top_dqfw).error(R.mipmap.ic_top_dqfw))
                .load(url)
                .into(ivTopBg);
    }


    /**
     * 初始化banner数据
     */
    private void initBanner(List<BannerEntity> bannerList) {
        flImg.setVisibility(View.VISIBLE);
        bannerView.delayTime(5).build(bannerList);
        bannerView.setVisibility(View.VISIBLE);
        bannerView.setOnItemClickListener(new BannerView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                position = position % bannerList.size();
                //查询详情
//                queryNewsById(bannerList.get(position).newsId);
            }
        });
    }

}
