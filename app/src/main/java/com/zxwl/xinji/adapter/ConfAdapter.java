package com.zxwl.xinji.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.network.bean.response.ThemePartyBean;
import com.zxwl.xinji.R;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/8/6 16:45
 * 三会一课适配器
 */
public class ConfAdapter extends BaseQuickAdapter<ThemePartyBean, BaseViewHolder> {
    private boolean isZtdr = false;

    public void setZtdr(boolean ztdr) {
        isZtdr = ztdr;
    }

    public ConfAdapter(int layoutResId, @Nullable List<ThemePartyBean> data) {
        super(layoutResId, data);
    }

    public static final int TYPE_DYDH = 1;//党员大会
    public static final int TYPE_DZBH = 2;//党支部会
    public static final int TYPE_DXZH = 3;//党小组会
    public static final int TYPE_DK = 4;//党课

    public static final int TYPE_CGXX = 5;//参观学习
    public static final int TYPE_MORE = 6;//其它
    public static final int TYPE_ZYFW = 7;//志愿服务


    @Override
    protected void convert(BaseViewHolder helper, ThemePartyBean item) {
        switch (item.type) {
            case TYPE_DYDH:
                helper.setImageResource(R.id.iv_states, isZtdr ? R.mipmap.ic_ztdr_dydh : R.mipmap.ic_conf_zbdydh);
                break;

            case TYPE_DZBH:
                helper.setImageResource(R.id.iv_states, isZtdr ? R.mipmap.ic_ztdr_dzbh : R.mipmap.ic_conf_zbwyh);
                break;

            case TYPE_DXZH:
                helper.setImageResource(R.id.iv_states, isZtdr ? R.mipmap.ic_ztdr_dxzh : R.mipmap.ic_conf_dxzh);
                break;

            case TYPE_DK:
                helper.setImageResource(R.id.iv_states, isZtdr ? R.mipmap.ic_ztdr_dk : R.mipmap.ic_conf_dk);
                break;

            case TYPE_CGXX:
                helper.setImageResource(R.id.iv_states, R.mipmap.ic_ztdr_cgxx);
                break;

            case TYPE_MORE:
                helper.setImageResource(R.id.iv_states, R.mipmap.ic_ztdr_more);
                break;

            case TYPE_ZYFW:
                helper.setImageResource(R.id.iv_states, R.mipmap.ic_ztdr_zyfw);
                break;
        }

        helper.setText(R.id.tv_conf_name, item.title);
        helper.setText(R.id.tv_time, DateUtil.longToString(item.activityDate, DateUtil.FORMAT_DATE));
        helper.setText(R.id.tv_local, item.address);
    }
}