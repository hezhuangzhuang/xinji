package com.zxwl.network.bean.response;//

import java.io.Serializable;

/**
 * author：pc-20171125
 * data:2019/6/14 09:27
 */
public class MessageBean implements Serializable {
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
    public int state;//0：未读,1:已读
    public String title;
    public int type;

    public boolean isSelect = false;
}
