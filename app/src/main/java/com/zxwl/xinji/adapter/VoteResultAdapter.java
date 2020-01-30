package com.zxwl.xinji.adapter;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.zxwl.network.bean.response.VotePeopleBean;
import com.zxwl.xinji.R;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/8/7 14:08
 * 投票的适配器
 */
public class VoteResultAdapter extends BaseQuickAdapter<VotePeopleBean, BaseViewHolder> {

    public VoteResultAdapter(int layoutResId, @Nullable List<VotePeopleBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VotePeopleBean item) {
        helper.addOnClickListener(R.id.bt_vote);

        RoundedImageView ivHead = helper.getView(R.id.iv_head);

        TextView tvOrder = (TextView) helper.getView(R.id.tv_order);
        TextView tvName = (TextView) helper.getView(R.id.tv_name);
        TextView tvPartyBranch = (TextView) helper.getView(R.id.tv_party_branch);
        TextView tvVoteNumber = helper.getView(R.id.tv_vote_number);

        Glide.with(mContext)
                .asBitmap()
                .apply(new RequestOptions().placeholder(R.mipmap.ic_minel_head).error(R.mipmap.ic_minel_head))
                .load(item.pic1)
                .into(ivHead);

        tvOrder.setText(String.valueOf(helper.getPosition() + 3));
        tvName.setText(item.name);
        tvPartyBranch.setText(item.partyname);
        tvVoteNumber.setText(item.count + "票");
    }
}
