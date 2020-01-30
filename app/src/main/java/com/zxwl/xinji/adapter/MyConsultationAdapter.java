package com.zxwl.xinji.adapter;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.network.bean.response.AdvisoryBean;
import com.zxwl.xinji.R;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/8/6 16:45
 * 我要咨询适配器
 */
public class MyConsultationAdapter extends BaseQuickAdapter<AdvisoryBean, BaseViewHolder> {

    public MyConsultationAdapter(int layoutResId, @Nullable List<AdvisoryBean> data) {
        super(layoutResId, data);
    }

    private TextView tvUnreadNumber;
    private TextView tvTitle;
    private TextView tvContent;

    @Override
    protected void convert(BaseViewHolder helper, AdvisoryBean item) {
        //咨询方
        if (0 == item.type) {
            helper.setText(R.id.tv_title, item.sendSontent);
            helper.setText(R.id.tv_content, "最后由" + item.senderName + "发布于" + DateUtil.longToString(item.sendDate, DateUtil.FORMAT_DATE_TIME));
        } else {
            //答复方
            helper.setText(R.id.tv_title, item.replyContent);
            helper.setText(R.id.tv_content, "最后由" + item.replyName + "发布于" + DateUtil.longToString(item.replyDate, DateUtil.FORMAT_DATE_TIME));
        }

        //是否显示未读
        helper.setText(R.id.tv_unread_number, String.valueOf(item.replyCount));
        helper.setVisible(R.id.tv_unread_number, 0 != item.replyCount);
    }
}