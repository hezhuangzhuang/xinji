package com.zxwl.network.bean.response;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.Collator;
import java.util.Locale;
import java.util.Objects;

/**
 * author：pc-20171125
 * data:2019/8/9 15:46
 * 选择列席人员
 */
public class PersonnelBean implements Serializable, Comparable<PersonnelBean> {
    //是否是列席人员
    public boolean isAttend = false;

    //是否是参会人员
    public boolean isParticipants = false;

    //是否选中
    public boolean isCheck = false;


    /**
     * applyDate : 1566230400000
     * id : 16
     * idCard : 420111201801019910
     * partyTreeId : 45
     * partyTreeName : 第一党小组
     * personType : 3
     * personVal : 积极分子
     * pic : http://192.168.20.249:8080/xjdj\css\base\images\heard\admin.png
     * recommend : xx
     * sex : 1
     * sexVal : 男
     */
    public String name;
    public long applyDate;
    public int id;
    public String idCard;
    public int partyTreeId;
    public String partyTreeName;
    public int personType;
    public String personVal;
    public String pic;
    public String recommend;
    public int sex;
    public String sexVal;
    public String firstChar;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonnelBean that = (PersonnelBean) o;
        return applyDate == that.applyDate &&
                id == that.id &&
                partyTreeId == that.partyTreeId &&
                personType == that.personType &&
                sex == that.sex &&
                Objects.equals(name, that.name) &&
                Objects.equals(idCard, that.idCard) &&
                Objects.equals(partyTreeName, that.partyTreeName) &&
                Objects.equals(personVal, that.personVal) &&
                Objects.equals(pic, that.pic) &&
                Objects.equals(recommend, that.recommend) &&
                Objects.equals(sexVal, that.sexVal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, applyDate, id, idCard, partyTreeId, partyTreeName, personType, personVal, pic, recommend, sex, sexVal);
    }

    @Override
    public int compareTo(@NonNull PersonnelBean personnelBean) {
        Collator ca = Collator.getInstance(Locale.CHINA);
        int flags = 0;
        if (ca.compare(firstChar, personnelBean.firstChar) < 0) {
            flags = -1;
        }
        else if(ca.compare(firstChar, personnelBean.firstChar) > 0) {
            flags = 1;
        }
        else {
            flags = 0;
        }
        return flags;
    }
}
