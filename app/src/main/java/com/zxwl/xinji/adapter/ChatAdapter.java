package com.zxwl.xinji.adapter;

import com.chad.library.adapter.base.BaseSectionMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.item.ChatItem;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/5/11 14:48
 */
public class ChatAdapter extends BaseSectionMultiItemQuickAdapter<ChatItem, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param sectionHeadResId The section head layout id for each item
     * @param data             A new list is created out of this one to avoid mutable list
     */
    public ChatAdapter(int sectionHeadResId, List<ChatItem> data) {
        super(sectionHeadResId, data);

        addItemType(0, R.layout.item_send_msg);
        addItemType(1, R.layout.item_receive_msg);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, ChatItem item) {
    }

    @Override
    protected void convert(BaseViewHolder helper, ChatItem item) {
        helper.setText(R.id.tv_time, item.chatBean.senderName + " " + DateUtil.longToString(item.chatBean.sendDate, DateUtil.FORMAT_DATE_TIME));
        helper.setText(R.id.tv_content, item.chatBean.content);
    }
}
