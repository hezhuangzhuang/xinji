package com.zxwl.network.bean.response;

import java.io.Serializable;

/**
 * author：pc-20171125
 * data:2019/8/15 09:57
 */
public class DepartmentDetailsBean implements Serializable {
    /**
     * message : 查询成功!
     * result : success
     * data : {"baseInfo":"基本简介基本简介基本简介","departmentId":1,"id":3,"partyMemberInfo":"党员信息党员信息党员信息","pic1":"http://192.168.20.249:8080/xjdj/picFolder/news/20190813112900554_2.jpeg","pic2":"http://192.168.20.249:8080/xjdj/picFolder/news/20190813112904197_2.jpeg","pic3":"http://192.168.20.249:8080/xjdj/picFolder/news/20190813112907684_2.jpeg","unitInfo":"单位简介单位简介单位简介"}
     */
    public String message;
    public String result;
    public DataBean data;

    public static class DataBean implements Serializable {
        /**
         * baseInfo : 基本简介基本简介基本简介
         * departmentId : 1
         * id : 3
         * partyMemberInfo : 党员信息党员信息党员信息
         * pic1 : http://192.168.20.249:8080/xjdj/picFolder/news/20190813112900554_2.jpeg
         * pic2 : http://192.168.20.249:8080/xjdj/picFolder/news/20190813112904197_2.jpeg
         * pic3 : http://192.168.20.249:8080/xjdj/picFolder/news/20190813112907684_2.jpeg
         * unitInfo : 单位简介单位简介单位简介
         */
        //基本信息
        public String baseInfo;
        public int departmentId;
        public int id;
        //党建阵地
        public String unitInfo;
        public String partyMemberInfo;
        //基本信息图片
        public String pic1;
        public String pic2;
        public String pic3;
        public String pic4;
        public String pic5;
        public String pic6;
        public String pic7;
        public String pic8;
        public String pic9;

        //党建阵地图片
        public String pic11;
        public String pic22;
        public String pic33;
        public String pic44;
        public String pic55;
        public String pic66;
        public String pic77;
        public String pic88;
        public String pic99;

        //集体经济
        public String title;

        @Override
        public String toString() {
            return "DataBean{" +
                    "baseInfo='" + baseInfo + '\'' +
                    ", departmentId=" + departmentId +
                    ", id=" + id +
                    ", partyMemberInfo='" + partyMemberInfo + '\'' +
                    ", pic1='" + pic1 + '\'' +
                    ", pic2='" + pic2 + '\'' +
                    ", pic3='" + pic3 + '\'' +
                    ", pic4='" + pic4 + '\'' +
                    ", pic5='" + pic5 + '\'' +
                    ", pic6='" + pic6 + '\'' +
                    ", pic7='" + pic7 + '\'' +
                    ", pic8='" + pic8 + '\'' +
                    ", pic9='" + pic9 + '\'' +
                    ", unitInfo='" + unitInfo + '\'' +
                    '}';
        }
    }
}
