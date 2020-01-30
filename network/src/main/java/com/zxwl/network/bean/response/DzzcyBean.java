package com.zxwl.network.bean.response;

/**
 * author：pc-20171125
 * data:2019/12/5 09:55
 * 党组织成员
 * 村监会成员
 */
public class DzzcyBean implements CurrencyBean{
    /**
     * age : 29
     * birthday : 1990-03
     * firstChar : B
     * flag : 1
     * flagVal : 是
     * id : 47
     * idCard : 110101199003077651
     * name : 北周村1
     * orgPosition : 1
     * orgPositionValue : 村党支部书记
     * sex : 1
     * sexValue : 男
     * township : 和睦井乡
     * townshipId : 64
     * type : 1
     * village : 北周村
     * villageId : 119
     * villagePosition : 1
     * villagePositionValue : 村委会主任
     */

    public String age;
    public String birthday;
    public String firstChar;
    public int flag;
    public String flagVal;
    public int id;
    public String idCard;
    public String name;
    public int orgPosition;
    public String orgPositionValue;
    public int sex;
    public String sexValue;
    public String township;
    public int townshipId;
    public int type;
    public String village;
    public int villageId;
    public int villagePosition;
    public String villagePositionValue;


    /**
     * politicalStatus : 1
     * politicalStatusValue : 共产党员
     * position : 2
     * positionValue : 成员
     * remark : 凄凄切切群群群群群群群群群群群群群群群群群群群群群群群群群群群群群群群群群群前秦前钱qqq
     * telephone : 88888888
     */

    //村监会成员，合作组织成员
    public int politicalStatus;
    public String politicalStatusValue;
    public int position;
    public String positionValue;
    public String remark;
    public String telephone;

    public String itemTyep;
    //是否是最后一条数据
    public boolean isLast = false;

}
