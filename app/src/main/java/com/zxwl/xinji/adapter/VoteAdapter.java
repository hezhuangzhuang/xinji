package com.zxwl.xinji.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.zxwl.network.bean.response.VotePeopleDetailsBean;
import com.zxwl.xinji.R;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/8/7 14:08
 * 投票的适配器
 */
public class VoteAdapter extends BaseQuickAdapter<VotePeopleDetailsBean, BaseViewHolder> {
    //是否投票
    private boolean isVote = false;

    public void setVote(boolean vote) {
        isVote = vote;
    }

    private int maxCount;

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
        notifyDataSetChanged();
    }

    public VoteAdapter(int layoutResId, @Nullable List<VotePeopleDetailsBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VotePeopleDetailsBean item) {
        RoundedImageView ivHead = (RoundedImageView) helper.getView(R.id.iv_head);
        TextView tvOrder = (TextView) helper.getView(R.id.tv_order);
        TextView tvName = (TextView) helper.getView(R.id.tv_name);
        TextView tvContent = (TextView) helper.getView(R.id.tv_content);
        TextView btVote = (TextView) helper.getView(R.id.bt_vote);
        TextView tvVoteNumber = (TextView) helper.getView(R.id.tv_vote_number);
        RoundCornerProgressBar progress = (RoundCornerProgressBar) helper.getView(R.id.progress);

        btVote.setVisibility(isVote? View.GONE:View.VISIBLE);

        helper.addOnClickListener(R.id.bt_vote);

        Glide.with(mContext)
                .asBitmap()
                .apply(new RequestOptions().placeholder(R.mipmap.ic_minel_head).error(R.mipmap.ic_minel_head))
                .load(item.pic1)
                .into(ivHead);

        tvOrder.setText((item.order) + "号");
        tvName.setText(item.name);
        tvContent.setText(item.introduction);
        tvVoteNumber.setText(item.num + "票");

        progress.setMax(maxCount);
        progress.setProgress(item.num);
    }
}
