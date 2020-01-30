package com.zxwl.xinji.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.network.bean.response.DepartmentBean;
import com.zxwl.xinji.R;

import java.util.List;

/**
 * 地图适配器
 */
public class AddressAdapter extends BaseQuickAdapter<DepartmentBean, BaseViewHolder> {
    private ImageView ivImg;
    private TextView tvCity;
    private TextView tvParty;

    public AddressAdapter(int layoutResId, @Nullable List<DepartmentBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DepartmentBean item) {
        helper.setText(R.id.tv_city, item.departmentName);
    }
}
