package com.zxwl.xinji.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.xinji.R;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/8/19 14:53
 */
public class ImageAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public ImageAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String url) {
        ImageView imageView = helper.getView(R.id.iv_img);

        Glide.with(mContext)
                .asBitmap()
//                .apply(new RequestOptions().placeholder(R.mipmap.image_loading))
                .load(url)
                .into(imageView);
    }
}
