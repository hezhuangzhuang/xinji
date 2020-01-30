package com.zxwl.xinji.adapter;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.xinji.R;
import com.zxwl.xinji.bean.GovernmentBean;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/11/15 15:58
 * 在线办公的适配器
 */
public class OnlineOfficeAdapter extends BaseQuickAdapter<GovernmentBean, BaseViewHolder> {

    public OnlineOfficeAdapter(int layoutResId, @Nullable List<GovernmentBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GovernmentBean item) {
        TextView tvContent = helper.getView(R.id.tv_content);
        tvContent.setText(item.name);
        tvContent.setCompoundDrawablesWithIntrinsicBounds(0, item.resId, 0, 0);
    }
}
