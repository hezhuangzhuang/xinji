package com.zxwl.commonlibrary.rxbus;

/**
 * author：hw
 * data:2017/6/30 09:19
 * 使用rxbus传递数据时的根数据
 */
public class RxBaseData<T> {
    public String message;//消息
    public String succeed;//是否成功
    public T t;

    public RxBaseData(String message, String succeed, T t) {
        this.message = message;
        this.succeed = succeed;
        this.t = t;
    }

    public RxBaseData(String message, T t) {
        this.message = message;
        this.succeed = succeed;
        this.t = t;
    }

    public RxBaseData(String message, String succeed) {
        this.message = message;
        this.succeed = succeed;
    }

    @Override
    public String toString() {
        return "RxBaseData{" +
                "message='" + message + '\'' +
                ", succeed='" + succeed + '\'' +
                ", t=" + t +
                '}';
    }
}
