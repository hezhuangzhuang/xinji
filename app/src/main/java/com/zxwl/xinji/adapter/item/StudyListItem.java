package com.zxwl.xinji.adapter.item;

import com.chad.library.adapter.base.entity.SectionMultiEntity;
import com.zxwl.network.bean.response.BannerEntity;
import com.zxwl.network.bean.response.CurrencyBean;
import com.zxwl.network.bean.response.DzzcyBean;
import com.zxwl.network.bean.response.NewsBean;
import com.zxwl.network.bean.response.ThemePartyBean;
import com.zxwl.network.bean.response.ZtjyBean;
import com.zxwl.xinji.adapter.StudyNewAdapter;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/5/8 18:11
 * 新闻item
 */
public class StudyListItem extends SectionMultiEntity<CurrencyBean> {
    public CurrencyBean newsBean;
    public List<BannerEntity> list;

    public StudyListItem(boolean isHeader, String header, List<BannerEntity> list) {
        super(isHeader, header);
        this.list = list;
    }

    public StudyListItem(CurrencyBean newsBean) {
        super(newsBean);
        this.newsBean = newsBean;
    }

    @Override
    public int getItemType() {
        boolean isBean = newsBean instanceof NewsBean || newsBean instanceof ZtjyBean || newsBean instanceof ThemePartyBean || newsBean instanceof DzzcyBean;
        return isBean ? StudyNewAdapter.TYPE_CONTENT : StudyNewAdapter.TYPE_HEAD;
    }
}
