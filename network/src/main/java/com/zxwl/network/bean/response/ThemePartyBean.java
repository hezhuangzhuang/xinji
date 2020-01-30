package com.zxwl.network.bean.response;

import java.io.Serializable;

/**
 * author：pc-20171125
 * data:2019/8/15 14:44
 * 主题党日，三会一课，民主评议，组织生活，其他
 */
public class ThemePartyBean implements Serializable, CurrencyBean {
    /**
     * activityDate : 1565746158000
     * address : 武汉市
     * attendNames : 李四!*!张三
     * attendUsers : 8,4
     * context : <p>测试三会一课</p>
     * createDate : 1565746200000
     * creator : 2
     * host : 李四2
     * id : 1
     * pic1 : http://localhost:8080/xjdj/picFolder/news/20190814092949107_2.jpeg
     * pic2 :
     * title : 测试三会一课
     * titleType : 2
     * type : 4
     * typeValue : 党课
     * unitId : 1
     * users : 8
     * videoThumbnailUrl : http://localhost:8080/xjdj/videoFolder/news/20190814092954151_2/20190814092954151_2.png
     * videoUrl : http://localhost:8080/xjdj/videoFolder/news/20190814092954151_2/20190814092954151_2.m3u8
     */
    public long activityDate;
    public String address;
    public String attendNames;
    public String attendUsers;
    public String context;
    public long createDate;
    public int creator;
    public String host;
    public int id;
    public String pic1;
    public String pic2;
    public String pic3;
    public String pic4;
    public String pic5;
    public String pic6;
    public String pic7;
    public String pic8;
    public String pic9;
    public String title;
    public int titleType;
    public int type;
    public String typeValue;
    public String typeVal;
    public int unitId;
    public String users;
    public String videoThumbnailUrl;
    public String videoUrl;

    public String creatorName;

    public String villageName;

    public String townName;

    public String reviewedNum;

    public int iconRes;//自己添加的字段，代表显示的icon资源

    /**
     * townId : 0
     * villageId : 0
     */
    public int townId;
    public int villageId;

    public String itemTyep;
}
