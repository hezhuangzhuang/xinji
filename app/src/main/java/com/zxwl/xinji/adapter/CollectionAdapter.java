package com.zxwl.xinji.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.network.bean.response.CollectionBean;
import com.zxwl.xinji.R;

import java.util.List;

/**
 * 收藏的适配器
 */
public class CollectionAdapter extends BaseQuickAdapter<CollectionBean, BaseViewHolder> {

    private boolean isEdit = false;//编辑模式

    public void setEdit(boolean edit) {
        isEdit = edit;
        notifyDataSetChanged();
    }

    public CollectionAdapter(int layoutResId, @Nullable List<CollectionBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CollectionBean item) {
        helper.addOnClickListener( R.id.rl_content);
        if (isEdit) {
            helper.setVisible( R.id.iv_status, true);
            if (item.isSelect) {
                helper.setImageResource( R.id.iv_status,  R.mipmap.ic_collection_true);
            } else {
                helper.setImageResource( R.id.iv_status,  R.mipmap.ic_collection_false);
            }
        } else {
            ImageView imageView = helper.getView( R.id.iv_status);
            imageView.setVisibility(View.GONE);
        }

        helper.setText( R.id.tv_title, item.title);
        helper.setText( R.id.tv_time, "收藏时间:  "+ DateUtil.longToString(Long.valueOf(item.saveTime), DateUtil.FORMAT_DATE));
    }
}
