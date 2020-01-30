package com.zxwl.network.bean.response;

import java.io.Serializable;

/**
 * author：pc-20171125
 * data:2019/9/17 15:23
 */
public class NotifBean implements Serializable {
    /**
     * message : 查询成功
     * result : success
     * data : {"beginDate":1568649600000,"content":"0917通知","endDate":1568995199000,"id":2320,"noticeId":103,"receiveId":91,"receiveName":"17764003575","sendDate":1568703558000,"senderId":92,"senderName":"张煜煜煜煜煜煜煜煜煜煜煜煜煜煜煜","state":1,"title":"0917通知","type":1}
     */
    public String message;
    public String result;
    public DataBean data;

    public static class DataBean implements Serializable {
        /**
         * beginDate : 1574697600000
         * content : 2019-11-26通知公告
         * endDate : 1575043199000
         * id : 2327
         * noticeId : 103
         * receiveId : 145
         * receiveName : 17764003575
         * sendDate : 1574734409000
         * senderId : 142
         * senderName : 张煜煜
         * state : 1
         * stateVal : 已读
         * title : 2019-11-26通知公告
         * type : 1
         */
        public long beginDate;
        public String content;
        public long endDate;
        public int id;
        public int noticeId;
        public int receiveId;
        public String receiveName;
        public long sendDate;
        public int senderId;
        public String senderName;
        public int state;
        public String stateVal;
        public String title;
        public int type;
    }



}
