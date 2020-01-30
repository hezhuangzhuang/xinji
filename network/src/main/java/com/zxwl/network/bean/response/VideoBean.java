package com.zxwl.network.bean.response;

import java.io.Serializable;

/**
 * author：pc-20171125
 * data:2019/6/5 10:14
 */
public class VideoBean implements Serializable {

    /**
     * announcer : 新华社
     * columnId : 12
     * columnName : 第一频道
     * commentNum : 1
     * context : <p>视听测试222</p>
     * createDate : 1559196565000
     * creator : 41
     * creatorName : admin
     * id : 2
     * itemType : 5
     * minutePoor : 05.30 15:57
     * reviewDate : 1559203047000
     * reviewOpinion : 审核通过
     * reviewState : 1
     * reviewStateVal : 通过
     * reviewer : 41
     * reviewerName : admin
     * state : 0
     * status : 0
     * statusVal : 上架
     * tempThumbnailUrl : http://localhost:8080/xuexi/picFolder/news/20190530144935761_41.jpeg
     * title : 测试002
     * videoThumbnailUrl : http://localhost:8080/xuexi/picFolder/news/20190530144935761_41.jpeg
     * videoUrl : http://localhost:8080/xuexi/videoFolder/news/20190530144304382_41/20190530144304382_41.mp4
     */
    //用到的
    public String announcer;
    public String tempThumbnailUrl;
    public String title;
    public String videoThumbnailUrl;
    public String videoUrl;
    public int collectState;// 0未收藏 1已收藏
    public int collectionId;//收藏id
    public int commentNum;
    public String context;
    public String createDate;
    public int id;
    public int itemType;


    //没用到的
    public int state;
    public int creator;
    public String creatorName;
    public int columnId;
    public String columnName;
    public String saveTime;//收藏时间
    public String minutePoor;
    public String reviewDate;
    public String reviewOpinion;
    public String reviewState;
    public String reviewStateVal;
    public int reviewer;
    public String reviewerName;
    public int status;
    public String statusVal;


}
