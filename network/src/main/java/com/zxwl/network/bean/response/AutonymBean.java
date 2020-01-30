package com.zxwl.network.bean.response;

/**
 * author：pc-20171125
 * data:2019/5/27 13:55
 */
public class AutonymBean {
    /**
     * name : 张三
     * idNo : 340421190710145412
     * respMessage : 身份证信息匹配
     * respCode : 0000
     * province : 安徽省
     * city : 淮南市
     * county : 凤台县
     * birthday : 19071014
     * sex : M
     * age : 111
     */
    public String name;
    public String idNo;
    public String respMessage;
    public String respCode;
    public String province;
    public String city;
    public String county;
    public String birthday;
    public String sex;
    public String age;

    @Override
    public String toString() {
        return "AutonymBean{" +
                "name='" + name + '\'' +
                ", idNo='" + idNo + '\'' +
                ", respMessage='" + respMessage + '\'' +
                ", respCode='" + respCode + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", county='" + county + '\'' +
                ", birthday='" + birthday + '\'' +
                ", sex='" + sex + '\'' +
                ", age='" + age + '\'' +
                '}';
    }
}
