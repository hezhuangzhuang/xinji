package com.zxwl.xinji.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.network.bean.response.LonlatBean;
import com.zxwl.xinji.R;

import java.util.List;

/**
 * 地图适配器
 */
public class AddressNewAdapter extends BaseQuickAdapter<LonlatBean, BaseViewHolder> {
    private ImageView ivImg;
    private TextView tvCity;
    private TextView tvParty;

    public AddressNewAdapter(int layoutResId, @Nullable List<LonlatBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LonlatBean item) {
        helper.setText(R.id.tv_city, item.departmentName);
    }
}
