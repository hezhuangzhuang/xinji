package com.zxwl.network.bean.response;

import java.io.Serializable;

/**
 * author：pc-20171125
 * data:2019/12/2 17:07
 */
public class NoteBean implements Serializable,CurrencyBean {
//    {
//            "context": "context",
//            "createTime": 1575271438000,
//            "creator": 2,
//            "id": 1,
//            "title": "xxx"
//        }

    public int id;
    public long createTime;
    public int creator;
    public String context;
    public String title;
    public String creatorName;


}
