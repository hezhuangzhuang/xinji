package com.zxwl.network.bean.response;

/**
 * author：pc-20171125
 * data:2019/11/26 14:32
 */
public class StudyNewsBean {

    /**
     * result : success
     * message : 查询成功!
     * data : {"announcer":"中讯网联","banner":1,"bannerValue":"是","columnId":1,"columnName":"推荐","context":"<p>内容内容内容<br/><\/p>","createDate":1574735309000,"creator":2,"id":162,"pic1":"http://192.168.20.249:8080/xjdj/picFolder/news/20191126102828234_2.jpeg","pic2":"","pic3":"","reviewDate":1574735346000,"reviewOpinion":"审核通过","reviewState":"1","reviewStateVal":"通过","reviewer":2,"reviewerName":"admin","state":0,"status":0,"statusVal":"上架","title":"测试banner1","titleType":2,"unitId":1,"videoThumbnailUrl":"","videoUrl":""}
     */

    public String result;
    public String message;
    public NewsBean data;

    public static class DataBean {
        /**
         * announcer : 中讯网联
         * banner : 1
         * bannerValue : 是
         * columnId : 1
         * columnName : 推荐
         * context : <p>内容内容内容<br/></p>
         * createDate : 1574735309000
         * creator : 2
         * id : 162
         * pic1 : http://192.168.20.249:8080/xjdj/picFolder/news/20191126102828234_2.jpeg
         * pic2 :
         * pic3 :
         * reviewDate : 1574735346000
         * reviewOpinion : 审核通过
         * reviewState : 1
         * reviewStateVal : 通过
         * reviewer : 2
         * reviewerName : admin
         * state : 0
         * status : 0
         * statusVal : 上架
         * title : 测试banner1
         * titleType : 2
         * unitId : 1
         * videoThumbnailUrl :
         * videoUrl :
         */

        public String announcer;
        public int banner;
        public String bannerValue;
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
        public int titleType;
        public int unitId;
        public String videoThumbnailUrl;
        public String videoUrl;

        public int collectState; //收藏状态 0未收藏 1已收藏
        public int collectionId; //收藏ID 仅用于接口调用
        public long commentNum; // 评论数
    }
}
