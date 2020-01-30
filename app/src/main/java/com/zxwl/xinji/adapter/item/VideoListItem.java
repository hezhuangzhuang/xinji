package com.zxwl.xinji.adapter.item;

import com.chad.library.adapter.base.entity.SectionMultiEntity;
import com.zxwl.network.bean.response.BannerEntity;
import com.zxwl.network.bean.response.VideoBean;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/5/8 18:11
 * 视频item
 */
public class VideoListItem extends SectionMultiEntity<VideoBean> {
    public VideoBean videoBean;
    public List<BannerEntity> list;

    public VideoListItem(boolean isHeader, String header, List<BannerEntity> list) {
        super(isHeader, header);
        this.list = list;
    }

    public VideoListItem(VideoBean newsBean) {
        super(newsBean);
        this.videoBean = newsBean;
    }

    @Override
    public int getItemType() {
        return videoBean.itemType;
    }
}
