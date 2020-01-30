package com.zxwl.xinji.bean;

import java.io.Serializable;
import java.util.List;

/**
 * author：pc-20171125
 * data:2019/8/23 13:46
 * 内容显示的详情
 */
public class ContentDetailsBean implements Serializable {
    public int id;
    public int collectionId;//收藏id
    public int collectState;//收藏状态
    public String title;
    public String content;
    public List<String> imageUrls;
    public String videoUrl;
    public String videoThumbnailUrl;

    public long time;
    public String unitName;
    public String pdfUrl;
    public String pdfName;

    public String oneLable;//人员
    public String one;//人员

    public String twoLable;//地点
    public String two;//地点

    public String threeLable;//地点
    public String three;//地点

    public String fourLable;//人员
    public String four;//人员

    public String fiveLable;//人员
    public String five;//人员

    public String sixLable;//人员
    public String six;//人员

    public String host;//主持人

    //是否有评论
    public boolean isComment = false;

    public static ContentDetailsBean ContentDetailsBeanFactory(
            String title,
            String content,
            List<String> imageUrls,
            String videoUrl,
            String videoThumbnailUrl,
            long time,
            String unitName) {
        ContentDetailsBean contentDetailsBean = new ContentDetailsBean();
        contentDetailsBean.title = title;
        contentDetailsBean.content = content;
        contentDetailsBean.imageUrls = imageUrls;
        contentDetailsBean.videoUrl = videoUrl;
        contentDetailsBean.videoThumbnailUrl = videoThumbnailUrl;
        contentDetailsBean.time = time;
        contentDetailsBean.unitName = unitName;
        return contentDetailsBean;
    }

    public static ContentDetailsBean ContentDetailsBeanFactory(
            String title,
            String content,
            List<String> imageUrls,
            String videoUrl,
            String videoThumbnailUrl,
            long time,
            String unitName,
            String pdfUrl,
            String pdfName
    ) {
        ContentDetailsBean contentDetailsBean = new ContentDetailsBean();
        contentDetailsBean.title = title;
        contentDetailsBean.content = content;
        contentDetailsBean.imageUrls = imageUrls;
        contentDetailsBean.videoUrl = videoUrl;
        contentDetailsBean.videoThumbnailUrl = videoThumbnailUrl;
        contentDetailsBean.time = time;
        contentDetailsBean.unitName = unitName;
        contentDetailsBean.pdfUrl = pdfUrl;
        contentDetailsBean.pdfName = pdfName;
        return contentDetailsBean;
    }


}
