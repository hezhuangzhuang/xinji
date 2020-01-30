package com.zxwl.xinji.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.section.GovernmentSection;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/7/30 16:53
 * 政务公开
 */
public class GovernmentAdapter extends BaseSectionQuickAdapter<GovernmentSection, BaseViewHolder> {

    //未读消息数
    private int unReadNumber;

    public void setUnReadNumber(int unReadNumber) {
        this.unReadNumber = unReadNumber;
        notifyDataSetChanged();
    }

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param layoutResId      The layout resource id of each item.
     * @param sectionHeadResId The section head layout id for each item
     * @param data             A new list is created out of this one to avoid mutable list
     */
    public GovernmentAdapter(int layoutResId, int sectionHeadResId, List<GovernmentSection> data) {
        super(layoutResId, sectionHeadResId, data);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, GovernmentSection item) {
        helper.setText(R.id.tv_head, item.header);
    }

    @Override
    protected void convert(BaseViewHolder helper, GovernmentSection item) {
        helper.setText(R.id.tv_content, item.t.name);

        TextView tvContent = helper.getView(R.id.tv_content);
        tvContent.setCompoundDrawablesWithIntrinsicBounds(item.t.resId, 0, 0, 0);
        helper.addOnClickListener(R.id.rl_content);

        if (unReadNumber > 0 && helper.getPosition() == 14) {
            helper.setVisible(R.id.tv_unread_number, true);
            helper.setText(R.id.tv_unread_number, unReadNumber > 99 ? "+99" : String.valueOf(unReadNumber));
        } else {
            helper.setVisible(R.id.tv_unread_number, false);
        }
    }
}
