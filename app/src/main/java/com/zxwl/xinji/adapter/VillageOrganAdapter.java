package com.zxwl.xinji.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.xinji.R;
import com.zxwl.xinji.bean.VillageOrgan;

import java.util.List;

/**
 * 村级组织适配器
 */
public class VillageOrganAdapter extends BaseQuickAdapter<VillageOrgan, BaseViewHolder> {

    public VillageOrganAdapter(int layoutResId, @Nullable List<VillageOrgan> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VillageOrgan item) {
        helper.setText(R.id.tv_people_number, item.peopleNumber);
        helper.setText(R.id.tv_unit_name, item.unitName);
    }
}
