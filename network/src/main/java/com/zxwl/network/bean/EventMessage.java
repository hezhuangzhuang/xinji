package com.zxwl.network.bean;

/**
 * author：pc-20171125
 * data:2019/6/25 19:11
 */
public class EventMessage<T> {
    public String message;//消息
    public String succeed;//是否成功
    public T t;

    public EventMessage(String message, String succeed, T t) {
        this.message = message;
        this.succeed = succeed;
        this.t = t;
    }

    public EventMessage(String message, T t) {
        this.message = message;
        this.succeed = succeed;
        this.t = t;
    }

    public EventMessage(String message, String succeed) {
        this.message = message;
        this.succeed = succeed;
    }
}
