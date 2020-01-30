package com.zxwl.xinji.adapter.item;

import com.chad.library.adapter.base.entity.SectionMultiEntity;
import com.zxwl.network.bean.response.ChatBean;

/**
 * author：pc-20171125
 * data:2019/5/11 14:49
 */
public class ChatItem extends SectionMultiEntity<ChatBean> {
    public ChatBean chatBean;

    public ChatItem(ChatBean chatBean) {
        super(chatBean);
        this.chatBean = chatBean;
    }

    @Override
    public int getItemType() {
        return chatBean.type;
    }
}
