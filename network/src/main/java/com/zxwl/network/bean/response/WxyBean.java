package com.zxwl.network.bean.response;

import java.io.Serializable;

/**
 * author：pc-20171125
 * data:2019/8/12 10:40
 * 微心愿
 */
public class WxyBean implements CurrencyBean, Serializable {
    /**
     * claimVal :
     * content : 2
     * createtime : 2019-10-21 09:36:40
     * creatorName : admin
     * id : 61
     * name : 2
     * operatorId : 2
     * reviewDate : 2019-10-21 10:36:43
     * reviewOpinion : 审核通过
     * reviewState : 1
     * reviewStateVal : 通过
     * reviewer : 2
     * reviewerName : admin
     * state : 0
     * telephone : 2
     * tinytime : 2019-10-21 00:00:00
     * townshipId : 64
     * unitname : 辛集市和睦井乡高家庄村
     * villageId : 250
     * villagename : 高家庄村
     * vtownsname : 和睦井乡
     * wishinfo : <p>2</p>
     */

    /**
     * id	主键
     * name	姓名
     * content	心愿内容
     * wishinfo	具体心愿诉求
     * createtime	发布时间
     * creatorName	创建人姓名
     * state	状态
     * unitId	组织ID
     * operatorId	操作员ID
     * unitname	组织所在名称
     * telephone	联系方式
     * reviewOpinion	审核意见
     * reviewState	审核状态
     * reviewStateVal	审核状态值
     * reviewerName	审核人
     * claim	是否认领 [0未认领 1已认领]
     * claimName	认领人姓名
     * claimTel	认领人联系方式
     * vtownsname	乡镇
     * villagename	街村
     */

    public String claimVal;
    public String content;
    public String createtime;
    public String creatorName;
    public int id;
    public String name;
    public int operatorId;
    public String reviewDate;
    public String reviewOpinion;
    public String reviewState;
    public String reviewStateVal;
    public int reviewer;
    public String reviewerName;
    public int claim;//1:已认领
    public String telephone;
    public String tinytime;
    public int townshipId;
    public String unitname;
    public int villageId;
    public String villagename;
    public String vtownsname;
    public String wishinfo;
    public String claimName;
    public String claimTel;

//
//    /**
//     * content : 1
//     * createtime : 1565255016000
//     * id : 9
//     * name : 1
//     * operatorId : 2
//     * state : 0
//     * unitId : 1
//     * wishinfo :
//     * unitname : 辛集市
//     */
//    public String content;
//    public long createtime;
//    public int id;
//    public String name;
//    public int operatorId;
//    public int state;
//    public int unitId;
//    public String wishinfo;
//    public String unitname;
//    public String creatorName;
//
//    /**
//     * areaType : 1
//     * tinytime : 1566748800000
//     * villagename :
//     * vtownsname :
//     */
//
//    public int areaType;
//    public long tinytime;
//    public String villagename;
//    public String vtownsname;


}
