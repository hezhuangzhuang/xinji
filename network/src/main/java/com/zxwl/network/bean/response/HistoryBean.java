package com.zxwl.network.bean.response;

/**
 * author：pc-20171125
 * data:2019/6/12 16:53
 */
public class HistoryBean {
//    {
//        "accountId":16,
//        "checkTime":1560323254000,
//        "datumId":50038,
//        "id":16,
//        "title":"20191004",
//        "type":0
//    }

    public int accountId;//用户id
    public String checkTime;//浏览时间
    public String datumId;//此id可以获取文章详情
    public int id;
    public String title;//名称
    public String type;//0，学习，1视频

//    public String time;//2019-02-02

    public int itemType = 1;
    public boolean isSelect = false;//是否选中
}
