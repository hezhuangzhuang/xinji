package com.zxwl.network.bean.response;

/**
 * author：pc-20171125
 * data:2019/6/3 14:50
 */
public class LoginBean {


    /**
     * message : 登录成功！
     * result : success
     * id : 79
     * sessionID : 89F22541379289A485007046BED3FC00
     * account : {"account":"yzj","checkAdmin":"1","checkAdminVal":"管理员账号","departmentEmpId":"","description":"22","deviceID":"1","firstChar":"Y","flag":"","id":"79","level":"2","levelName":"乡镇","name":"杨章健2","password":"xV4Rw54W9zP27ZfFwlRwQQ003d003d","password1":"xV4Rw54W9zP27ZfFwlRwQQ003d003d","sessionID":"89F22541379289A485007046BED3FC00","sex":"","state":"0","styleInfo":"","styleInfoPc":"","telephone":"22222222","unitId":"2","url":"http://192.168.20.249:8080/xjdj\\webApp\\detail\\img\\defaultAvatar.png","votes":"0","votesPercent":""}
     * requestUrl : http://192.168.20.249:8080/xjdj/
     */
    public String message;
    public String result;
    public String id;
    public String sessionID;
    public AccountBean account;
    public String requestUrl;
    public String siteAccount;//smc登录账户
    public String sitePwd;//smc登录密码
    public String longitude;
    public String latitude;
    public String SMC_URL;

    public static class AccountBean {
        /**
         * account : yzj
         * checkAdmin : 1
         * checkAdminVal : 管理员账号
         * departmentEmpId :
         * description : 22
         * deviceID : 1
         * firstChar : Y
         * flag :
         * id : 79
         * level : 2
         * levelName : 乡镇
         * name : 杨章健2
         * password : xV4Rw54W9zP27ZfFwlRwQQ003d003d
         * password1 : xV4Rw54W9zP27ZfFwlRwQQ003d003d
         * sessionID : 89F22541379289A485007046BED3FC00
         * sex :
         * state : 0
         * styleInfo :
         * styleInfoPc :
         * telephone : 22222222
         * unitId : 2
         * url : http://192.168.20.249:8080/xjdj\webApp\detail\img\defaultAvatar.png
         * votes : 0
         * votesPercent :
         */
        public String account;

        //账号类型，0：普通账号，1：管理员账号
        public int checkAdmin;
        //账号类型中文说明
        public String checkAdminVal;
        public String departmentEmpId;
        public String description;
        public String deviceID;

        //姓名首字母
        public String firstChar;
        public String flag;
        public String id;

        //行政级别,1：市级,2：乡镇,3：村级
        public int level;

        //行政级别中文说明
        public String levelName;
        public String name;
        //明码密码，用于实体属性
        public String password;
        public String password1;
        public String sessionID;

        //性别 [1 男 2女]
        public String sex;
        public String state;
        public String styleInfo;
        public String styleInfoPc;
        public String telephone;

        //组织单位id [DEPARTMENT表id]
        public int unitId;

        //组织单位名称
        public String unitName;

        //个人头像图片地址
        public String url;
        public String votes;
        public String votesPercent;

        public String partyBranchId; // 党支部id
        public String partyGroupId; // 党小组id
        public String partyGroupName; //

        public String siteAccount;//smc登录账户
        public String sitePwd;//smc登录密码

        public String longitude;
        public String latitude;

        public String townId;
        public String villageId;
    }
}
