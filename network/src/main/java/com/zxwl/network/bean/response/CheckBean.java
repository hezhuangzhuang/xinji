package com.zxwl.network.bean.response;

import java.io.Serializable;

/**
 * author：pc-20171125
 * data:2019/6/14 09:56
 */
public class CheckBean implements Serializable{
    /**
     * result : success
     * message : 2
     * data : {"beginDate":1560477300000,"content":"测试","dealState":0,"endDate":1560736500000,"id":1182,"itemId":0,"messageId":65,"receiveId":15,"receiveName":"李世民2","sendDate":1560477333000,"senderId":41,"senderName":"admin","state":0,"title":"测试","type":1,"typeValue":"伟人诞辰"}
     */

    public String result;
    public String message;
    public MessageBean data;

    @Override
    public String toString() {
        return "CheckBean{" +
                "result='" + result + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
