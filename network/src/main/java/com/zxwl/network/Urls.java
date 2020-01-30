package com.zxwl.network;

import okhttp3.MediaType;

public class Urls {
    /**
     * 辛集客户访问地址
     */
    public static String BASE_URL = "http://61.182.50.12:8085/xjdj/";
    public static String CREATE_BASE_URL = "http://61.182.50.12:8080/xjdj/";

    public static final int SUCCESS = 1;

    public static String acCSRFToken = "";

    //public static String BASE_URL = "http://192.168.20.249:8080/xjdj/";//杨章建地址
    //public static String BASE_URL = "http://192.168.16.236:10014/xjdj/";//张煜内网地址

    public static String SMC_REGISTER_SERVER = "61.182.50.11";

    public static final String SMC_REGISTER_PORT = "5061";

    /**
     * 党章党规
     */
    public static final String DZDG_URL = "http://www.12371.cn/special/dnfg/";

    /**
     * 组工文件
     */
    public static final String ZGWJ_URL = "http://www.12371.cn/special/zgwjk/";

    /**
     * 服务大厅
     */
    public static final String FWDT_URL = "https://mz16766655.m.icoc.bz/";

    /**
     * 党务问答
     */
    public static final String DWWD_URL = "http://fuwu.12371.cn/dwwd/";

    /**
     * 现代远程教育
     */
    public static final String XDYCJY_URL = "http://dygbjy.12371.cn/";

    /**
     * 共产党员电视栏目
     */
    public static final String GCDYDSLM_URL = "http://dslm.12371.cn/";

    /**
     * 便民服务-网上办事
     */
    public static final String WSBS_URL = "http://xj.hbzwfw.gov.cn/";

    /**
     * 便民服务-网上办税
     */
    public static final String WSBShui_URL = "http://hebei.chinatax.gov.cn/hbsw/wxnav/bs/index.html";

    /**
     * 便民服务-医疗保障
     */
    public static final String YLBZ_URL = "http://ylbzj.hebei.gov.cn/category/2";

    /**
     * 便民服务-农村产权交易
     */
    public static final String NCCQJY_URL = "http://xinji.hbree.cn/";

    /**
     * 便民服务-配方施肥
     */
    public static final String PFSF_URL = "http://cetu.inagri.cn:7080/xj/pg/index.jsp";

    /**
     * 便民服务-公积金查询
     */
    public static final String GJJCX_URL = "http://www.xjgjj.org.cn/website/index.html";

    /**
     * 阳光政务-政务公开
     */
    public static final String ZWGK_URL = "http://info.xinji.gov.cn:8081/";

    /**
     * 阳光政务-天气预报
     */
    public static final String TQYB_URL = "https://widget-page.heweather.net/h5/index.html?bg=1&md=0123456&lc=CN101090114&key=4a2fecfa3acd405683597bbcd01635cd";

    /**
     * 阳光政务-快递查询
     */
    public static final String KDCX_URL = "https://m.kuaidi100.com/";

    /**
     * 阳光政务-高速路况
     */
    public static final String GSLK_URL = "http://hebecc.com/hbzwfw/traffic";

    /**
     * 生活服务-违章查询
     */
    public static final String WZCX_URL = "https://he.122.gov.cn/#/inquiry";

    /**
     * 生活服务-电视直播
     */
    public static final String DSZB_URL = "https://mz16766655.m.icoc.bz/col.jsp?id=102";

    /**
     * 生活服务-火车票预订
     */
    public static final String HCPYD_URL = "https://m.ctrip.com/webapp/train/?sourceid=1775&allianceid=18887&sid=452649&utm_source=sm&utm_medium=cpc&utm_campaign=cmm44&sepopup=2&hiderecommapp=1#/index?VNK=4b46284f";

    /**
     * 生活服务-航班预订
     */
    public static final String HBYD_URL = "https://m.ctrip.com/html5/flight/swift/index?sourceid=1775&allianceid=18887&sid=452649&utm_source=sm&utm_medium=cpc&utm_campaign=cmm44&sepopup=2";

    /**
     * 出行-限行查询
     */
    public static final String XXCX_URL = "https://wx.hbgajg.com/wap/wfcx/xianxing?ufrom=&sign=&code=011p1TAe0jRneu11GhCe03NLAe0p1TAZ&state=honglvdeng";

    /**
     * 出行-公交查询
     */
    public static final String GJCX_URL = "https://mp.weixin.qq.com/s/dCfa8BSzwpzrcOwn9i84kA";

    /**
     * 出行-高速口
     */
    public static final String GSK_URL =
            "http://hls01open.ys7.com/openlive/590c779594014c9eb8cc2f143d84f3fa.m3u8";
//            "http://hzhls01.ys7.com:7885/openlive/686486490_1_2.m3u8?ticket=YUFPOFJQSi8rTFlURExLSHB1c256bDJGVFZhYjhmY1NQZkxRU3lnN2FCND0kMSQyMDE5MTIwNzEwMjAzMCQxNTc1NTk4ODAwNzY0JDE1NzU2ODUyMzA3NjQkMSQxNTc1NTk4ODAwNzY0JDE1NzU2ODUyMzA3NjQkMTAwMDAkMWYyODIxNzVhZWM0NGIyNmFiOGRkMWRkN2M0MTAxMjQkLTE=&token=870ddc3849b44b88b969d4286c3cd802";

    /**
     * 视频广播-听广播
     */
    public static final String TGB_URL = "https://m.qingting.fm/categories/5";

    /**
     * 视频广播-视频广播
     */
    public static final String TYPE_SPDB = "https://jiyunxinjifabu.hebyun.com.cn/topic/2019/11/05/8fb11fb688f04d7e959a055fa3aaa77a.html";

    /**
     * 互动交流-直通书记
     */
    public static final String ZTSJ_URL = "http://cn.mikecrm.com/j2iui2b";

    /**
     * 学习平台
     */
    public static final String XXPT_URL = "http://www.12371.cn/special/xxzd/";

    /**
     * 习近平重要讲话数据库
     */
    public static final String ZYJH_URL = "http://jhsjk.people.cn/";

    /**
     * 思想理论
     */
    public static final String SXLL_URL = "http://www.12371.cn/special/sxll/";

    /**
     * 纪检监察
     */
    public static final String JJJC_URL = "http://hebei.12388.gov.cn/xinjishi/";

    public static String XXPT_IMAGE_URL = BASE_URL + "picFolder/banner/banner1.jpg";
    public static String ZYJH_IMAGE_URL = BASE_URL + "picFolder/banner/banner2.jpg";
    public static String SXLL_IMAGE_URL = BASE_URL + "picFolder/banner/banner3.jpg";


    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    //查询主题党日
    public static final String QUERY_THEME_DAY = "themeDayAction_queryListApp.action";

    //三会一课
    public static final String QUERY_THREE_SESSIONS = "threeSessionsAction_queryListApp.action";

    //民主评议
    public static final String QUERY_DEMOCRATIC = "democraticAction_queryListApp.action";

    //组织生活会l
    public static final String QUERY_LIFE_MEETING = "lifeMeetingAction_queryListApp.action";

    //其他
    public static final String QUERY_OTHERS = "othersAction_queryListApp.action";

    //查询主题党日的年份
    public static final String QUERY_THEME_DAY_YEAR = "themeDayAction_queryYear.action";

    //三会一课的年份
    public static final String QUERY_THREE_SESSIONS_YEAR = "threeSessionsAction_queryYear.action";

    //民主评议的年份
    public static final String QUERY_DEMOCRATIC_YEAR = "democraticAction_queryYear.action";

    //组织生活会的年份
    public static final String QUERY_LIFE_MEETING_YEAR = "lifeMeetingAction_queryYear.action";

    //其他的年份
    public static final String QUERY_OTHERS_YEAR = "othersAction_queryYear.action";

    //亮点工作的年份
    public static final String QUERY_LDGZ_YEAR = "highlightsWorkAction_queryYear.action";

    //荣誉表彰的年份
    public static final String QUERY_RYBZ_YEAR = "honorAction_queryYear.action";

    //图说本村的年份
    public static final String QUERY_TSBC_YEAR = "pictureVillageAction_queryYear.action";





    //上传主题教育图片
    public static final String LOAD_IAMGE_ZTJY = "subjeducAction_loadAvatar.action";

    //上传主题教育视频
    public static final String LOAD_VIDEO_ZTJY = "subjeducAction_loadAppVideos.action";

    //添加党务公开
    public static final String ADD_PARTYAFF_LIST = "partyAffAction_addPartyaffList.action";

    //上传主题党日图片
    public static final String LOAD_IAMGE_ZTDR = "themeDayAction_loadAvatar.action";

    //上传主题党日视频
    public static final String LOAD_VIDEO_ZTDR = "themeDayAction_loadAppVideos.action";

    //上传三会一课图片
    public static final String LOAD_IAMGE_SHYK = "threeSessionsAction_loadAvatar.action";

    //上传三会一课视频
    public static final String LOAD_VIDEO_SHYK = "threeSessionsAction_loadAppVideos.action";

    //上传民主评议图片
    public static final String LOAD_IAMGE_MZPY = "democraticAction_loadAvatar.action";

    //上传民主评议视频
    public static final String LOAD_VIDEO_MZPY = "democraticAction_loadAppVideos.action";

    //上传组织生活会图片
    public static final String LOAD_IAMGE_ZZSHH = "lifeMeetingAction_loadAvatar.action";

    //上传组织生活会视频
    public static final String LOAD_VIDEO_ZZSHH = "lifeMeetingAction_loadAppVideos.action";

    //上传组织生活会图片
    public static final String LOAD_IAMGE_MORE = "othersAction_loadAvatar.action";

    //上传组织生活会视频
    public static final String LOAD_VIDEO_MORE = "othersAction_loadAppVideos.action";

    //添加主题党日
    public static final String ADD_ZTDR = "themeDayAction_addThemeDay.action";

    //添加三会一课
    public static final String ADD_SHYK = "threeSessionsAction_addThreeSessions.action";

    //添加民主评议
    public static final String ADD_MZPY = "democraticAction_addDemocratic.action";

    //添加组织生活会
    public static final String ADD_ZZSHH = "lifeMeetingAction_addLifeMeeting.action";

    //添加其他
    public static final String ADD_MORE = "othersAction_addOthers.action";

    //帮扶活动展示图片
    public static final String LOAD_IAMGE_ZYFW = "povertyActivityAction_loadAvatar.action";

    //帮扶活动展示视频
    public static final String LOAD_VIDEO_ZYFW = "povertyActivityAction_loadAppVideos.action";

    //添加帮扶活动
    public static final String ADD_ZYFW = "povertyActivityAction_addPovertyActivity.action";

    //添加评比图片
    public static final String LOAD_IAMGE_VOTE = "voteAction_loadAvatar.action";

    //添加评比
    public static final String ADD_VOTE = "voteAction_addVote.action";

    //单位简介上传图片
    public static final String LOAD_IAMGE_UNIT_INFO = "departmentInfoAction_loadAvatar.action";

    //单位简介编辑
    public static final String UPDATE_UNIT_INFO = "departmentInfoAction_updDeptInfo.action";

    //查询排名-名次排序
    public static final String QUERY_CANDIDATE_LIST_ACTION = "votePeopleAction_queryCandidateList.action";

    //查询排名-不排序
    public static final String QUERY_CANDIDATE_LIST_ACTION2 = "votePeopleAction_queryCandidateList2.action";

    //查询党组织换届
    public static final String QUERY_PARTY_ELECTION_ACTION = "partyElectionAction_queryPartyElection.action";

    //查询村委会换届
    public static final String QUERY_HAMLET_ELECTION_ACTION = "hamletElectionAction_queryHamletElection.action";

    //查询党组织换届上传图片
    public static final String LOAD_IAMGE_PARTY_ELECTION = "partyElectionAction_loadAvatar.action";

    //查询村委会换届上传图片
    public static final String LOAD_IAMGE_HAMLET_ELECTION = "hamletElectionAction_loadAvatar.action";

    //添加党组织换届
    public static final String ADD_PARTY_ELECTION = "partyElectionAction_addPartyElectionList.action";

    //添加村委会换届
    public static final String ADD_HAMLET_ELECTION = "hamletElectionAction_addHamletElectionList.action";

    //建议反馈
    public static final String ADD_FEEDBACK_ACTION = "feedbackAction_addFeedback.action";

    //意见建议
    public static final String ADD_ENTITY_ACTION = "suggestAction_addEntity.action";

    //获取支部书记
    public static final String QUERY_ZBSJ_LIST = "deptOrgInfoAction_querySecretary.action";

    //获取支部委员
    public static final String QUERY_ZBWY_LIST = "deptOrgInfoAction_queryMember.action";

    //获取支部党员
    public static final String QUERY_ZBDY_LIST = "partyMembersAction_queryPartyMember.action";

    //添加设岗定责
    public static final String ADD_SGDZ = "dutyAction_addEntity.action";

    //添加党员联系户
    public static final String ADD_DYLXH = "partycontactAction_addPartycontactList.action";


    //集体经济上传图片
    public static final String LOAD_IAMGE_JTJJ = "collectiveEconomyAction_loadAvatar.action";

    //添加集体经济
    public static final String ADD_JTJJ = "collectiveEconomyAction_addEntity.action";

    //亮点工作-上传图片
    public static final String LOAD_IAMGE_LDGZ = "highlightsWorkAction_loadAvatar.action";

    //添加亮点工作
    public static final String ADD_LDGZ = "highlightsWorkAction_addEntity.action";

    //荣誉表彰-上传图片
    public static final String LOAD_IAMGE_RYBZ= "honorAction_loadAvatar.action";

    //添加荣誉表彰
    public static final String ADD_RYBZ = "honorAction_addEntity.action";

    //图说本村-上传图片
    public static final String LOAD_IAMGE_TSBC= "pictureVillageAction_loadAvatar.action";

    //添加图说本村
    public static final String ADD_TSBC = "pictureVillageAction_addEntity.action";

    //查询集体经济
    public static final String QUERY_JTJJ_LIST = "collectiveEconomyAction_queryListApp.action";

    //查询亮点工作
    public static final String QUERY_LDGZ_LIST = "highlightsWorkAction_queryListApp.action";

    //查询荣誉表彰
    public static final String QUERY_RYBZ_LIST = "honorAction_queryListApp.action";

    //查询图说本村
    public static final String QUERY_TSBC_LIST = "pictureVillageAction_queryListApp.action";

}
