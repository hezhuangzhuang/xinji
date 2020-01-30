package com.zxwl.xinji.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.network.bean.response.InformationBean;
import com.zxwl.xinji.R;

import java.util.List;

/**
 * 新闻资讯适配器
 * 现代远程教育适配器
 */
public class InformationAdapter extends BaseQuickAdapter<InformationBean, BaseViewHolder> {

    public InformationAdapter(int layoutResId, @Nullable List<InformationBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, InformationBean item) {
        helper.setText(R.id.tv_content,item.name);
    }
}
