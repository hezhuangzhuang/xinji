package com.zxwl.network.bean.response;

import java.io.Serializable;

/**
 * author：pc-20171125
 * data:2019/11/18 19:23
 * 支部委员的对象
 */
public class PartyBranchPersonBean implements Serializable {
    /**
     * age : 47
     * birthday : 1972-03
     * firstChar : G
     * flag : 2
     * flagVal : 否
     * id : 32
     * idCard : 110101197203074739
     * name : 郭富城
     * orgPosition : 1
     * orgPositionValue : 村党支部书记
     * sex : 1
     * sexValue : 男
     * township : 和睦井乡
     * townshipId : 64
     * type : 1
     * village :
     */
    public String age;
    public String firstChar;
    public int flag;
    public String flagVal;
    public int id;
    public String idCard;
    public String name;
    public int orgPosition;
    public String orgPositionValue;
    public String villagePositionValue;
    public int sex;
    public String sexValue;
    public String township;
    public int townshipId;
    public int type;
    public String village;
    public String telephone;
    public long workingTime;

    /**
     * addpartyDate : 1571760000000
     * birthPlace : 1
     * birthday : 636739200000
     * education : 1
     * educationVal : 小学及以下
     * nation : 1
     * partyTreeName :
     * pic : http://localhost:8080/xjdj/picFolder/depart/20191029154634231_2.jpeg
     * sexVal : 男
     */
    public long addpartyDate;
    public String birthPlace;
    public String birthday;
    public int education;
    public String educationVal;
    public String nation;
    public String partyTreeName;
    public String pic;
    public String sexVal;

    private int villagePosition;

    //党员生日
    public String birthdayStr;
    //党员职务
    public String postsVal;
}
