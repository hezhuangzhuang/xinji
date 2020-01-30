package com.zxwl.xinji.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.network.bean.response.MessageBean;
import com.zxwl.xinji.R;

import java.util.List;

/**
 * author：pc-20171125
 * 通知的适配器
 */
public class NotifAdapter extends BaseQuickAdapter<MessageBean, BaseViewHolder> {
    private boolean isEdit = false;//编辑模式

    public void setEdit(boolean edit) {
        isEdit = edit;
        notifyDataSetChanged();
    }


    public NotifAdapter(int layoutResId, @Nullable List<MessageBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MessageBean item) {
        helper.addOnClickListener(R.id.rl_content);
        helper.setText(R.id.tv_lable, item.senderName);
        helper.setText(R.id.tv_content, item.content);
        helper.setText(R.id.tv_title, item.title);
        helper.setText(R.id.tv_time, DateUtil.longToString(item.beginDate, DateUtil.FORMAT_MONTH_DAY_TIME));

        if (isEdit) {
            helper.setVisible(R.id.iv_status, true);
            if (item.isSelect) {
                helper.setImageResource(R.id.iv_status, R.mipmap.ic_collection_true);
            } else {
                helper.setImageResource(R.id.iv_status, R.mipmap.ic_collection_false);
            }

            helper.setVisible(R.id.iv_read_status, false);
        } else {
            ImageView imageView = helper.getView(R.id.iv_status);
            imageView.setVisibility(View.GONE);

            helper.setVisible(R.id.iv_read_status, 0 == item.state);
        }
    }
}
