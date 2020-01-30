package com.zxwl.network.bean.response;

/**
 * author：pc-20171125
 * data:2019/8/28 18:28
 */
public class UrgeDetail {


    /**
     * message : 查询成功
     * result : success
     * data : {"beginDate":1565193600000,"content":"111","dealState":0,"dealStateValue":"未处理","endDate":1565452800000,"id":11,"readDate":1565606606150,"receiveId":75,"receiveName":"彭宇奇","sendDate":1565253612000,"senderId":2,"senderName":"admin","state":1,"stateValue":"已读","title":"11","unitId":1,"urgeId":2}
     */

    public String message;
    public String result;
    public DataBean data;

    public static class DataBean {
        /**
         * beginDate : 1565193600000
         * content : 111
         * dealState : 0
         * dealStateValue : 未处理
         * endDate : 1565452800000
         * id : 11
         * readDate : 1565606606150
         * receiveId : 75
         * receiveName : 彭宇奇
         * sendDate : 1565253612000
         * senderId : 2
         * senderName : admin
         * state : 1
         * stateValue : 已读
         * title : 11
         * unitId : 1
         * urgeId : 2
         */

        public long dealDate;
        public long beginDate;
        public String content;
        public String dealContent;
        public int dealState;
        public String dealStateValue;
        public long endDate;
        public int id;
        public long readDate;
        public int receiveId;
        public String receiveName;
        public long sendDate;
        public int senderId;
        public String senderName;
        public int state;
        public String stateValue;
        public String title;
        public int unitId;
        public int urgeId;
    }
}
