package com.zxwl.network.bean.response;

/**
 * author：pc-20171125
 * data:2019/8/21 09:50
 */
public class StudyBean {
    /**
     * result : success
     * message : 查询成功!
     * data : {"announcer":"是","columnId":3,"columnName":"经验交流","context":"<p>是<\/p>","createDate":1566291224000,"creator":2,"id":103,"pic1":"","pic2":"","pic3":"","reviewDate":1566292811000,"reviewOpinion":"审核通过","reviewState":"1","reviewStateVal":"通过","reviewer":2,"reviewerName":"admin","state":0,"status":0,"statusVal":"上架","title":"是","titleType":1,"videoThumbnailUrl":"","videoUrl":""}
     */

    public String result;
    public String message;
    public DataBean data;

    public static class DataBean {
        /**
         * announcer : 是
         * columnId : 3
         * columnName : 经验交流
         * context : <p>是</p>
         * createDate : 1566291224000
         * creator : 2
         * id : 103
         * pic1 :
         * pic2 :
         * pic3 :
         * reviewDate : 1566292811000
         * reviewOpinion : 审核通过
         * reviewState : 1
         * reviewStateVal : 通过
         * reviewer : 2
         * reviewerName : admin
         * state : 0
         * status : 0
         * statusVal : 上架
         * title : 是
         * titleType : 1
         * videoThumbnailUrl :
         * videoUrl :
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
       public String pic4;
       public String pic5;
       public String pic6;
       public String pic7;
       public String pic8;
       public String pic9;
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
       public String videoThumbnailUrl;
       public String videoUrl;
    }

}
