package com.zxwl.network.bean.response;

import java.io.Serializable;

/**
 * author：pc-20171125
 * data:2019/8/6 15:01
 * 线上评比
 */
public class XspbBean implements CurrencyBean, Serializable {
    /**
     * deadTime : 1566006179000
     * id : 105
     * operatorId : 75
     * operatorName : 彭宇奇
     * state : 0
     * stateValue : 进行中
     * title : 测试投票0815
     * voteId : 59
     * votePeopleId :
     * votePeopleName :
     * voteState : 0
     * voteStateValue : 未投票
     */

    public long createDate;
    public long deadTime;
    public int id;
    public int operatorId;
    public String operatorName;
    public int state;//状态：0进行中 1已过期
    public String stateValue;
    public String title;
    public int voteId;
    public String votePeopleId;
    public String votePeopleName;
    public int voteState;//0未投票，1已投票
    public String voteStateValue;

    public String content;//投票简介
    public String department;//部门

    public String picture;//图片

    public long createTime;
    public int creatorId;
    public long deadline;
//    public String department;
//    public int id;
    public String name;
    public String notice;
    public long reviewDate;
    public String reviewOpinion;
    public String reviewState;
    public String reviewStateVal;
    public int reviewer;
    public String reviewerName;
//    public int state;
//    public String stateValue;
    public String time;

}
