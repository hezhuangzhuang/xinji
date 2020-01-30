package com.zxwl.network.bean.response;

/**
 * author：pc-20171125
 * data:2019/8/13 10:59
 * 我要咨询列表item的bean
 */
public class AdvisoryBean implements CurrencyBean{
    /**
     * id : 1
     * replyContent : 北京市
     * replyCount : 2
     * replyDate : 1564708137000
     * replyId : 2
     * replyName : admin
     * saveDate : 1564629728000
     * sendDate : 1564629728000
     * sendSontent : 中国首都？
     * senderId : 75
     * senderName : 彭宇奇
     * state : 1
     * stateValue : 已答复
     */
    public int id;
    public String replyContent;
    public int replyCount;
    public long replyDate;
    public int replyId;
    public String replyName;
    public long saveDate;
    public long sendDate;
    public String sendSontent;
    public int senderId;
    public String senderName;
    public int state;
    public String stateValue;

    public int type;//0:咨询方，1:回复方

}
