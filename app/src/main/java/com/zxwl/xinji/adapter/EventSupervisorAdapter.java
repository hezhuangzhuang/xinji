package com.zxwl.xinji.adapter;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.Button;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.network.bean.response.SjdbBean;
import com.zxwl.xinji.R;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/8/6 16:27
 * 事件督办
 */
public class EventSupervisorAdapter extends BaseQuickAdapter<SjdbBean, BaseViewHolder> {

    public EventSupervisorAdapter(int layoutResId, @Nullable List<SjdbBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SjdbBean item) {
        TextView tvTilte = (TextView) helper.getView(R.id.tv_tilte);
        TextView tvTime = (TextView) helper.getView(R.id.tv_time);
        TextView tvWebsite = (TextView) helper.getView(R.id.tv_website);
        TextView tvUnit = (TextView) helper.getView(R.id.tv_unit);
        TextView tvContent = (TextView) helper.getView(R.id.tv_content);
        Button btReply = (Button) helper.getView(R.id.bt_reply);

        TextView tvResultTime = (TextView) helper.getView(R.id.tv_result_time);
        TextView tvResultWebsite = (TextView) helper.getView(R.id.tv_result_website);
        TextView tvResultContent = (TextView) helper.getView(R.id.tv_result_content);

        tvTilte.setText(item.title);
        tvTime.setText(DateUtil.longToString(item.sendDate, DateUtil.FORMAT_DATE_TIME));
        tvWebsite.setText("发布方:" + item.senderName);

        helper.setText(R.id.tv_red_status, item.stateValue);

        if("已读".equals(item.stateValue)){
            helper.setTextColor(R.id.tv_red_status, ContextCompat.getColor(mContext,R.color.color_666));
        }else {
            helper.setTextColor(R.id.tv_red_status, ContextCompat.getColor(mContext,R.color.color_e85541));
        }

        helper.setText(R.id.tv_reply_status, item.dealStateValue);

        TextView tvReplyStatus = helper.getView(R.id.tv_reply_status);

        if("已处理".equals(item.dealStateValue)){
            helper.setTextColor(R.id.tv_red_status, ContextCompat.getColor(mContext,R.color.color_11cf8f));
            tvReplyStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.ic_event_processed,0,0,0);
        }else {
            helper.setTextColor(R.id.tv_red_status, ContextCompat.getColor(mContext,R.color.color_e85541));
            tvReplyStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.ic_event_untreated,0,0,0);
        }

    }
}