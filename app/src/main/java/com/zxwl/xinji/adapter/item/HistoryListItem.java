package com.zxwl.xinji.adapter.item;

import com.chad.library.adapter.base.entity.SectionMultiEntity;
import com.zxwl.network.bean.response.BannerEntity;
import com.zxwl.network.bean.response.HistoryBean;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/5/8 18:11
 */
public class HistoryListItem extends SectionMultiEntity<HistoryBean> {
    public HistoryBean historyBean;

    public List<BannerEntity> list;

    public HistoryListItem(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public HistoryListItem(HistoryBean homeInfoBean) {
        super(homeInfoBean);
        this.historyBean = homeInfoBean;
    }

    @Override
    public int getItemType() {
        return MultipleItem.TEXT;
    }
}
