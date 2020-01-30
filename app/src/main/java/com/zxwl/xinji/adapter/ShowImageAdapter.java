package com.zxwl.xinji.adapter;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.luck.picture.lib.entity.LocalMedia;
import com.zxwl.xinji.R;

import java.util.ArrayList;
import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.pictureselector.adapter
 * email：893855882@qq.com
 * data：16/7/27
 * 图片展示适配器
 */
public class ShowImageAdapter extends BaseQuickAdapter<LocalMedia, BaseViewHolder> {
    private LayoutInflater mInflater;
    private List<LocalMedia> list = new ArrayList<>();
    private int selectMax = 9;

    public ShowImageAdapter(int layoutResId, @Nullable List<LocalMedia> data) {
        super(layoutResId, data);
    }

    public void setList(List<LocalMedia> list) {
        this.list = list;
    }

    @Override
    protected void convert(BaseViewHolder helper, LocalMedia item) {
        ImageView imageView = helper.getView(R.id.fiv);
        LinearLayout llDel = helper.getView(R.id.ll_del);
        llDel.setVisibility(View.GONE);

//        LocalMedia media = mData.get(helper.getPosition() - 1);

        String path = item.getPath();

        RequestOptions options = new RequestOptions()
                .centerCrop()
//                .placeholder(R.color.color_f6f6f6)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(mContext)
                .load(path)
                .apply(options)
                .into(imageView);
    }

}
