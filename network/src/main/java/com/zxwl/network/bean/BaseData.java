package com.zxwl.network.bean;

import java.util.List;

/**
 * author：hw
 * data:2017/6/2 15:14
 */
public class BaseData<T> {
    public String data;
    public String error;
    public String message;
    public String result;

    public String sessionID;
    public String id;

    public List<T> dataList;
    public int code;

    //请求成功
    public static final String SUCCESS ="success";

    /**
     * 督办消息未读数
     */
    public String urgeCount;

    /**
     * 我要咨询未读数
     */
    public int replyCount;

    @Override
    public String toString() {
        return "BaseData{" +
                "data='" + data + '\'' +
                ", success=" + SUCCESS +
                ", message='" + message + '\'' +
                '}';
    }




}
