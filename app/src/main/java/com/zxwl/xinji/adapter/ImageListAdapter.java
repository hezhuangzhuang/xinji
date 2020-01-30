package com.zxwl.xinji.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.xinji.R;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/10/18 17:17
 */
public class ImageListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public ImageListAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String imageUrl) {
        ImageView imageView = helper.getView(R.id.iv_img);

        Glide.with(mContext)
                .asBitmap()
//                .apply(new RequestOptions().placeholder(R.mipmap.ic_minel_head).error(R.mipmap.ic_minel_head))
                .load(imageUrl)
                .apply(new RequestOptions().centerCrop())
                .into(imageView);
    }
}
