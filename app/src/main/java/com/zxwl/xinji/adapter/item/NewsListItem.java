package com.zxwl.xinji.adapter.item;

import com.chad.library.adapter.base.entity.SectionMultiEntity;
import com.zxwl.network.bean.response.BannerEntity;
import com.zxwl.network.bean.response.NewsBean;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/5/8 18:11
 * 新闻item
 */
public class NewsListItem extends SectionMultiEntity<NewsBean> {
    public NewsBean newsBean;
    public List<BannerEntity> list;

    public NewsListItem(boolean isHeader, String header, List<BannerEntity> list) {
        super(isHeader, header);
        this.list = list;
    }

    public NewsListItem(NewsBean newsBean) {
        super(newsBean);
        this.newsBean = newsBean;
    }

    @Override
    public int getItemType() {
        return newsBean.titleType;
    }
}
