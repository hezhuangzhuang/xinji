package com.zxwl.xinji.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.network.bean.response.TsbcBean;
import com.zxwl.xinji.R;

import java.util.List;

/**
 * 图说本村适配器
 */
public class TsbcAdapter extends BaseQuickAdapter<TsbcBean, BaseViewHolder> {

    public TsbcAdapter(int layoutResId, @Nullable List<TsbcBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TsbcBean item) {
        String unitName = "辛集市";
        if (!TextUtils.isEmpty(item.townName)) {
            unitName = item.townName;
        }

        if (!TextUtils.isEmpty(item.villageName)) {
            unitName = item.villageName;
        }

        helper.setText(R.id.tv_title, item.title);
        helper.setText(R.id.tv_website, unitName);
        helper.setText(R.id.tv_time, DateUtil.longToString(item.createDate, DateUtil.FORMAT_DATE));
        RoundedImageView ivOne = (RoundedImageView) helper.getView(R.id.iv_one);
        RoundedImageView ivTwo = (RoundedImageView) helper.getView(R.id.iv_two);
        RoundedImageView ivThree = (RoundedImageView) helper.getView(R.id.iv_three);

        Glide.with(mContext)
                .asBitmap()
                .apply(new RequestOptions().placeholder(R.color.tran).error(R.color.tran))
                .load(item.pic1)
                .into(ivOne);

        Glide.with(mContext)
                .asBitmap()
                .apply(new RequestOptions().placeholder(R.color.tran).error(R.color.tran))
                .load(item.pic2)
                .into(ivTwo);

        Glide.with(mContext)
                .asBitmap()
                .apply(new RequestOptions().placeholder(R.color.tran).error(R.color.tran))
                .load(item.pic3)
                .into(ivThree);

        helper.addOnClickListener(
                R.id.iv_one,
                R.id.iv_two,
                !TextUtils.isEmpty(item.pic3) ? R.id.iv_three : -1);
    }
}
