package com.zxwl.xinji.adapter;

import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.commonlibrary.widget.CircleImageView;
import com.zxwl.network.bean.response.PartyBranchPersonBean;
import com.zxwl.xinji.R;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/11/18 19:05
 * 党支部适配器
 */
public class PartyBranchPersonAdapter extends BaseQuickAdapter<PartyBranchPersonBean, BaseViewHolder> {
    public PartyBranchPersonAdapter(int layoutResId, @Nullable List<PartyBranchPersonBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PartyBranchPersonBean item) {
        ConstraintLayout constraintLayout  = helper.getView(R.id.cl_content);
        CircleImageView ivHead = (CircleImageView) helper.getView(R.id.iv_head);
        TextView tvUnit = helper.getView(R.id.tv_unit);
        TextView tvName = helper.getView(R.id.tv_name);

        helper.setText(R.id.tv_name, item.name);
        helper.setText(R.id.tv_unit, item.township + item.village);
//        if(TextUtils.isEmpty(item.township + item.village)){
//            tvUnit.setVisibility(View.GONE);
//        }else {
//            tvUnit.setVisibility(View.VISIBLE);
//            helper.setText(R.id.tv_unit, item.township + item.village);
//        }

        Glide.with(mContext)
                .asBitmap()
                .apply(new RequestOptions().error(R.mipmap.ic_minel_head))
                .load(item.pic)
                .into(ivHead);
    }

}
