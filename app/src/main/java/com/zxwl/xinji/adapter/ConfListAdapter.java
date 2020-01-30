package com.zxwl.xinji.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.network.bean.response.ConfBean;
import com.zxwl.xinji.R;

import java.util.List;

/**
 * 地图适配器
 */
public class ConfListAdapter extends BaseQuickAdapter<ConfBean, BaseViewHolder> {

    public ConfListAdapter(int layoutResId, @Nullable List<ConfBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ConfBean item) {
        ImageView ivJoinConf = (ImageView) helper.getView(R.id.iv_join_conf);
        TextView tvTitle = (TextView) helper.getView(R.id.tv_title);
        TextView tvTime = (TextView) helper.getView(R.id.tv_time);
        TextView tvStatus = (TextView) helper.getView(R.id.tv_status);

        ivJoinConf.setVisibility(3 == item.confState ? View.VISIBLE : View.GONE);

        tvTitle.setText(item.name);

        long beginTimeLong = DateUtil.stringToLong(item.beginTime, DateUtil.FORMAT_DATE_TIME_SECOND);
        long endTimeLong = DateUtil.stringToLong(item.endTime, DateUtil.FORMAT_DATE_TIME_SECOND);

        tvTime.setText(DateUtil.longToString(beginTimeLong, DateUtil.FORMAT_DATE_TIME) + "-" + DateUtil.longToString(endTimeLong, DateUtil.FORMAT_TIME));

        tvStatus.setText(item.confStateName);

        helper.addOnClickListener(R.id.cl_content,R.id.iv_join_conf);
    }
}
