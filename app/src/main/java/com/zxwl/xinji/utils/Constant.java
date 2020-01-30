package com.zxwl.xinji.utils;

/**
 * author：pc-20171125
 * data:2019/4/12 14:47
 */
public class Constant {
    public static final String AUTO_LOGIN = "AUTO_LOGIN";//是否自动登录

    public static final String USER_NAME = "USER_NAME";//用户名

    public static final String PASS_WORD = "PASS_WORD";//密码

    public static final String LAST_CODE = "LAST_CODE";//最后一次收到的验证码

    public static final String LAST_CODE_TIME = "LAST_CODE_TIME";//最后一次收到的验证码的时间

    public static final String NOTIFA_HINT = "NOTIFA_HINT";//消息是否提醒

    public static final String SITE_NAME = "SITE_NAME";//SMC登录账户

    public static final String SITE_PWD = "SITE_PWD";//SMC登录密码

    //PDF存放位置
    public static final String PDF_SAVE_DIR = FileUtils.getDir("pdf");

    //电话号码的长度
    public static final int PHONE_LENGHT = 11;

    /**
     *  填应用AppId，APP在开放平台注册的id
     */
    public static final String weixinAppId="wxfdcf299c7f48102b";
//    public static final String weixinAppId="9c25a7abfb6375c329548739ab6200fe";

    /**
     * 小程序原始id
     */
    public static final String weixiUserName="gh_132b4d188f3e";
//    public static final String weixiUserName="gh_132b4d188f3e";

}
