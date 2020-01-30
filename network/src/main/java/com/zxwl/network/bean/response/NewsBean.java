package com.zxwl.network.bean.response;

import java.io.Serializable;


/**
 * author：pc-20171125
 * data:2019/5/31 11:48
 * 新闻的bean
 */
public class NewsBean implements CurrencyBean, Serializable {
    /**
     * announcer : 新华社
     * columnId : 3
     * columnName : 经验交流
     * context : <p>庆祝国庆节</p>
     * createDate : 1565249912000
     * creator : 2
     * id : 97
     * pic1 : http://localhost:8080/xjdj/picFolder/news/20190808162921224_2.jpeg
     * pic2 :
     * pic3 :
     * reviewDate : 1565253418000
     * reviewOpinion : 审核通过
     * reviewState : 1
     * reviewStateVal : 通过
     * reviewer : 2
     * reviewerName : admin
     * state : 0
     * status : 0
     * statusVal : 上架
     * title : 庆祝国庆节
     * videoThumbnailUrl : http://localhost:8080/xjdj/videoFolder/news/20190808163213396_2/20190808163213396_2.png
     * videoUrl : http://localhost:8080/xjdj/videoFolder/news/20190808163213396_2/20190808163213396_2.m3u8
     */

    public String announcer;
    public int columnId;
    public String columnName;
    public String context;
    public long createDate;
    public int creator;
    public int id;
    public String pic1;
    public String pic2;
    public String pic3;
    public long reviewDate;
    public String reviewOpinion;
    public String reviewState;
    public String reviewStateVal;
    public int reviewer;
    public String reviewerName;
    public int state;
    public int status;
    public String statusVal;
    public String title;
    public String videoThumbnailUrl;
    public String videoUrl;

    public String result;

    //用到的
//    public String announcer;
//    public String context;
//    public String createDate;
//    public int id;//收藏id
//    public String pic1;
//    public String pic2;
//    public String pic3;
//    public String banner;
//    public String title;
    public String bannerValue;
    public String commentNum;
    public int titleType = 1;//文章样式
    public int collectState;//0未收藏，1已收藏
    public int collectionId;//收藏id

    public String itemTyep;

    /**
     * banner : 1
     * columnId : 2
     * createDate : 2019-11-20 17:24:03
     * creator : 2
     * id : 158
     * reviewDate : 2019-11-20 17:24:22
     * reviewer : 2
     * state : 0
     * status : 0
     * titleType : 4
     * unitId : 1
     */

}
