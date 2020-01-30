package com.zxwl.network.bean.response;

import java.io.Serializable;


/**
 * author：pc-20171125
 * data:2019/5/31 11:48
 * 新闻的bean
 */
public class InformationBean implements Serializable {
    /**
     * announcer : 222222
     * banner : 0
     * bannerValue : 否
     * columnId : 1
     * columnName : 关注
     * commentNum : 1
     * context : <p>3333333333333333333333</p>
     * createDate : 2019-05-28 15:50:06
     * creator : 41
     * creatorName : admin
     * id : 50018
     * minutePoor : 05.28 15:51
     * pic1 :
     * pic2 :
     * pic3 :
     * reviewDate : 2019-05-28 15:51:48
     * reviewOpinion : 审核通过
     * reviewState : 1
     * reviewStateVal : 通过
     * reviewer : 41
     * reviewerName : admin
     * state : 0
     * status : 0
     * statusVal : 上架
     * title : 111
     * type : 0
     */
    //用到的
    public String announcer;
    public String banner;
    public String bannerValue;
    public String commentNum;
    public String context;
    public String createDate;
    public int id;//收藏id
    public String pic1;
    public String pic2;
    public String pic3;
    public int itemType = 1;//文章样式
    public int collectState;//0未收藏，1已收藏
    public int collectionId;//收藏id
    public String title;

    /**
     * name : 新浪
     * url : https://www.sina.com.cn
     */
    public String name;
    public String url;

}
