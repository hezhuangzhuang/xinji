package com.zxwl.xinji.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.xinji.R;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/11/12 14:07
 * 筛选时间适配器
 */
public class ScreenTimeAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public ScreenTimeAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_content, item);
    }
}
