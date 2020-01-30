package com.zxwl.xinji.adapter;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.network.bean.response.PersonnelBean;
import com.zxwl.xinji.R;

import java.util.List;

/**
 * 选择人员适配器
 */
public class SelectPersonnelAdapter extends BaseQuickAdapter<PersonnelBean, BaseViewHolder> {

    public SelectPersonnelAdapter(int layoutResId, @Nullable List<PersonnelBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PersonnelBean item) {
        helper.setBackgroundColor(R.id.rv_content, item.isCheck ? ContextCompat.getColor(mContext, R.color.color_fff0d9) : ContextCompat.getColor(mContext, R.color.white));

        helper.setImageResource(R.id.iv_check, item.isCheck ? R.mipmap.ic_select_person_true : R.mipmap.ic_select_person_false);

        helper.setText(R.id.tv_name, item.name);
        helper.setText(R.id.tv_address, item.partyTreeName);
    }
}