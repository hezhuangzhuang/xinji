package com.zxwl.network.bean.response;

import java.io.Serializable;

/**
 * author：pc-20171125
 * data:2019/11/20 16:51
 */
public class SgdzBean implements Serializable,CurrencyBean {
    /**
     * id : 2
     * job : 1
     * jobVal : 政策宣传
     * name : 市级
     * remark : 1
     * specialty : 去1
     * township :
     * village :
     */
    public int id;
    public int job;
    public String jobVal;
    public String name;
    public String remark;
    public String specialty;
    public String township;
    public String village;

    public String vtownsname;
    public String villagename;

    public String cname;
}
