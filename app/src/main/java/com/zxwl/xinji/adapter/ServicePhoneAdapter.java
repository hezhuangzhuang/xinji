package com.zxwl.xinji.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.network.bean.response.ServicePhoneBean;
import com.zxwl.xinji.R;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/8/6 16:45
 * 服务电话适配器
 */
public class ServicePhoneAdapter extends BaseQuickAdapter<ServicePhoneBean, BaseViewHolder> {

    public ServicePhoneAdapter(int layoutResId, List<ServicePhoneBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ServicePhoneBean item) {
        helper.setText(R.id.tv_phone, item.tel);
        helper.setText(R.id.tv_name, item.name);
        helper.setText(R.id.tv_address, "地址：" + item.address);
    }
}