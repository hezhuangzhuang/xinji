package com.zxwl.network.bean.response;

import java.io.Serializable;

/**
 * author：pc-20171125
 * data:2019/8/6 15:01
 * 党员联系户
 */
public class DylxhBean implements CurrencyBean,Serializable {
    /**
     * caddress : 111111111
     * cname : 妮妮拟
     * cphone : 12121
     * createtime : 1565599177000
     * id : 7
     * name : 李丽丽
     * phone : 15555
     * state : 0
     * unitId : 1
     * unitname : 辛集市
     * workunit : 11111
     */

    public int id;//主键
    public int state;//状态
    public String name;//党员姓名
    public String phone;//党员联系电话
    public long createtime;//创建时间
    public String cname;//联系户姓名
    public String cphone;//联系户联系电话
    public String caddress;//联系户住址
    public String workunit;//联系户工作单位
    public String unitname;//发布人员组织所在名称x

    public String remark;//备注

    public int unitId;

    /**
     * caddress : 111111111
     * cname : 妮妮拟
     * cphone : 12121
     * createtime : 1565599177000
     * id : 7
     * name : 李丽丽
     * phone : 15555
     * state : 0
     * unitId : 1
     * unitname : 辛集市
     * workunit : 11111
     */

//    public String caddress;
//    public int id;
//    public String name;
//    public String phone;
//    public long createtime;
//    public String cname;
//    public String cphone;
//    public int state;
//    public int unitId;
//    public String unitname;
//    public String workunit;




}
