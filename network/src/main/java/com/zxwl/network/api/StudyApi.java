package com.zxwl.network.api;

import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.response.AdvisoryBean;
import com.zxwl.network.bean.response.BannerEntity;
import com.zxwl.network.bean.response.BfhdzsBean;
import com.zxwl.network.bean.response.BfzrrglBean;
import com.zxwl.network.bean.response.ChatBean;
import com.zxwl.network.bean.response.CmswdbBean;
import com.zxwl.network.bean.response.CollectionBean;
import com.zxwl.network.bean.response.CommentBean;
import com.zxwl.network.bean.response.ConfBean;
import com.zxwl.network.bean.response.CwgkBean;
import com.zxwl.network.bean.response.DepartmentBean;
import com.zxwl.network.bean.response.DepartmentDetailsBean;
import com.zxwl.network.bean.response.DocumentBean;
import com.zxwl.network.bean.response.DzzcyBean;
import com.zxwl.network.bean.response.DzzhjBean;
import com.zxwl.network.bean.response.InformationBean;
import com.zxwl.network.bean.response.LonlatBean;
import com.zxwl.network.bean.response.MessageBean;
import com.zxwl.network.bean.response.NewAdvisoryBean;
import com.zxwl.network.bean.response.NewsBean;
import com.zxwl.network.bean.response.NoteBean;
import com.zxwl.network.bean.response.NotifBean;
import com.zxwl.network.bean.response.OrgInfoCountBean;
import com.zxwl.network.bean.response.PartyBranchPersonBean;
import com.zxwl.network.bean.response.PersonnelBean;
import com.zxwl.network.bean.response.ProblemBean;
import com.zxwl.network.bean.response.RoundBean;
import com.zxwl.network.bean.response.SearchBean;
import com.zxwl.network.bean.response.ServicePhoneBean;
import com.zxwl.network.bean.response.SgdzBean;
import com.zxwl.network.bean.response.SjdbBean;
import com.zxwl.network.bean.response.StatisticsBean;
import com.zxwl.network.bean.response.StudyBean;
import com.zxwl.network.bean.response.StudyNewsBean;
import com.zxwl.network.bean.response.ThemePartyBean;
import com.zxwl.network.bean.response.TsbcBean;
import com.zxwl.network.bean.response.UrgeDetail;
import com.zxwl.network.bean.response.VersionBean;
import com.zxwl.network.bean.response.VotePeopleBean;
import com.zxwl.network.bean.response.VotePeopleDetailsBean;
import com.zxwl.network.bean.response.WxyBean;
import com.zxwl.network.bean.response.WxyCountBean;
import com.zxwl.network.bean.response.XspbBean;
import com.zxwl.network.bean.response.XspbDetailsBean;
import com.zxwl.network.bean.response.XwqlqdBean;
import com.zxwl.network.bean.response.ZtjyBean;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * author：pc-20171125
 * data:2018/12/18 17:52
 */

public interface StudyApi {

    /**
     * 获取新闻列表数据
     * newsAction_queryListApp.action?pageSize=5&pageNum
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("news2Action_queryListApp.action")
    Observable<BaseData<NewsBean>> queryNews(
            @Query("columnId") int columnId,
            @Query("pageNum") int pageNum
    );

    /**
     * 搜索
     * newsAction_queryListApp.action?pageSize=5&pageNum
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("studyNewsAction_searchListApp.action")
    Observable<BaseData<SearchBean>> searchInfo(
            @Query("title") String title,
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("columnId") String columnId
    );

    /**
     * 搜索组工文件
     * newsAction_queryListApp.action?pageSize=5&pageNum
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("orgDocumentAction_searchListApp.action")
    Observable<BaseData<DocumentBean>> searchDocument(
            @Query("title") String title,
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("type") int type
    );


    /**
     * 建议反馈
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST
    Observable<BaseData> addFeedback(
            @Url String url,
            @Query("phone") String phone,
            @Query("content") String content,
            @Query("other") String other,
            @Query("name") String name
    );

    /*
     * 上传访客记录
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST
    Observable<BaseData> checkIn(@Url String url,
                                 @Field("name") String name,
                                 @Field("tel") String tel,
                                 @Field("studentName") String studentName,
                                 @Field("plate") String plate,
                                 @Field("remark") String remark,
                                 @Field("src") String base64);

    /*
     * 上传访客记录
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST
    Observable<BaseData> checkIn(@Url String url,
                                 @Body RequestBody body);


    /**
     * 微心愿查询
     * type:0：未认领，1：已认领
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("tinywishAction_queryTinywishList.action")
    Observable<BaseData<WxyBean>> queryWxys(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("unitId") int unitId,
            @Query("type") int type
    );

    /**
     * banner查询
     * bannerAction_queryAllList
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("bannerAction_queryAllList.action")
    Observable<BaseData<BannerEntity>> queryBanners();

    /**
     * 查询党员联系户
     * partycontactAction_queryPartycontact
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("partycontactAction_queryPartycontact.action")
    Observable<BaseData<SgdzBean>> queryPartyContacts(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("unitid") int unitid
    );


    /**
     * 查询新的督办事件数量
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("urgeDetailAction_queryUrgeCountApp.action")
    Observable<BaseData> queryDbsjUnReadCount(
            @Query("accountId") int accountId
    );

    /**
     * 查询消息数
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("noticeDetailAction_queryUnreadNum.action")
    Observable<BaseData> queryNotifUnReadCount();

    /**
     * 查询我要咨询新的答复数量
     * advisoryDetailAction_appendAdvisoryApp
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("urgeDetailAction_queryUrgeCountApp.action")
    Observable<BaseData> queryWyzxUnReadCount(
            @Query("accountId") int accountId
    );


    /**
     * 我要咨询-查询列表
     * advisoryAction_queryListApp
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("advisoryAction_queryListApp.action")
    Observable<BaseData<AdvisoryBean>> queryWyzxs(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("accountId") int unitid
    );

    /**
     * 查询小微权力清单
     * advisoryAction_queryListApp
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("smallpowerdetAction_querySmallpowerdetList.action")
    Observable<BaseData<XwqlqdBean>> queryXwqlqds(
            @QueryMap Map<String, Integer> param
    );


    /**
     * 查询村民事务代办
     * advisoryAction_queryListApp
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("affairsAgencyAction_queryListApp.action")
    Observable<BaseData<CmswdbBean>> queryCmswdbs(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize
    );

    /**
     * 我要咨询-查询单个咨询详情
     * advisoryAction_qu
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("advisoryDetailAction_queryListApp.action")
    Observable<BaseData<ChatBean>> queryWyzxDetail(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("serialNo") int serialNo
    );

    /**
     * 我要咨询-新增咨询
     * advisoryAction_qu
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("advisoryDetailAction_addAdvisoryApp.action")
    Observable<NewAdvisoryBean> newAdvisory(
            @Query("accountId") int accountId,
            @Query("content") String content
    );

    /**
     * 我要咨询-追加咨询
     * advisoryDetailAction_appendAdvisoryApp
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("advisoryDetailAction_appendAdvisoryApp.action")
    Observable<NewAdvisoryBean> appendAdvisory(
            @Query("accountId") int accountId,
            @Query("content") String content,
            @Query("serialNo") int serialNo
    );

    /**
     * 查询常见问题
     * doubtAction_queryListApp
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("doubtAction_queryListApp.action")
    Observable<BaseData<ProblemBean>> queryCjwt(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("title") String title
    );

    /**
     * 获取通知列表
     * noticeAction_queryListApp
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("noticeDetailAction_queryListApp.action")
    Observable<BaseData<MessageBean>> queryNotices(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("accountId") int accountId
    );

    /**
     * 删除通知
     * noticeAction_deleteForApp.action?ids=1233,1234
     *
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("noticeDetailAction_deleteForApp.action")
    Observable<BaseData> delNotice(
            @Query("ids") String ids
    );

    /**
     * 查询
     * noticeAction_deleteForApp.action?ids=1233,1234
     *
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("noticeDetailAction_queryById.action")
    Observable<NotifBean> queryNoticeById(
            @Query("id") String id
    );


    /**
     * 获取新闻资讯
     * studySiteAction_queryListApp
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("studySiteAction_queryListApp.action")
    Observable<BaseData<InformationBean>> queryStudyInfos(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize
    );

    /**
     * 获取现代远程教育
     * studyEduAction_queryListApp
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("studyEduAction_queryListApp.action")
    Observable<BaseData<InformationBean>> queryStudyEdus(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize
    );

    /**
     * 获取理论学习接口
     * studyEduAction_queryListApp
     * columnId     推荐：1，政治理论：2
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("studyNewsAction_queryListApp.action")
    Observable<BaseData<NewsBean>> queryStudys(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("columnId") int columnId
    );

    /**
     * 获取服务电话
     * governservecallAction_queryGovernservecall
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("governservecallAction_queryGovernservecall.action")
    Observable<BaseData<ServicePhoneBean>> queryServicePhones(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize
    );

    /**
     * 获取督办事件
     * governservecallAction_queryGovernservecall
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("urgeDetailAction_queryListApp.action")
    Observable<BaseData<SjdbBean>> queryUrgeDetails(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("accountId") int accountId
    );

    /**
     * 回复督办事件
     * governservecallAction_queryGovernservecall
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("urgeDetailAction_replyUrgeApp.action")
    Observable<BaseData> replyUrge(
            @Query("id") int id,
            @Query("accountId") int accountId,
            @Query("content") String content
    );


    /**
     * 通过组织单位id查询下级组织
     * departmentAction_queryDeptListByParentId
     * <p>
     * 备注：首次调用接口的parentId为账号登录后返回的unitId字段
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("departmentAction_queryDeptListByParentId2.action")
    Observable<BaseData<DepartmentBean>> queryDepartment(
            @Query("parentId") int parentId
    );

    /**
     * 通过组织单位id查询组织详情
     * departmentInfoAction_queryDetail
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST
    Observable<DepartmentDetailsBean> queryDepartmentDetail(
            @Url String url,
            @Query("unitId") int unitId
    );

    /**
     * 获取三会一课
     * threeSessionsAction_queryListApp
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("threeSessionsAction_queryListApp.action")
    Observable<BaseData<ThemePartyBean>> queryConfs(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("accountId") int accountId,
            @Query("unitId") int unitId
    );

    /**
     * 获取民主评议
     * democraticAction_queryListApp
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("democraticAction_queryListApp.action")
    Observable<BaseData<ThemePartyBean>> queryDemocratics(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("accountId") int accountId,
            @Query("unitId") int unitId
    );

    /**
     * 查询主题党日的年份
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST
    Observable<BaseData> queryThemeDayYear(
            @Url String url
    );


    /**
     * 获取主题党日
     * themeDayAction_queryListApp
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST
    Observable<BaseData<ThemePartyBean>> queryThemeDays(
            @Url String url,
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("accountId") int accountId,
            @Query("unitId") int unitId,
            @Query("year") int year
    );

    /**
     * 获取主题党日
     * themeDayAction_queryListApp
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST
    Observable<BaseData<ThemePartyBean>> queryThemeDays(
            @Url String url,
            @QueryMap Map<String, Integer> param
    );

    /**
     * 获取组织生活会
     * lifeMeetingAction_queryListApp
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("lifeMeetingAction_queryListApp.action")
    Observable<BaseData<ThemePartyBean>> queryLifes(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("accountId") int accountId,
            @Query("unitId") int unitId
    );

    /**
     * 获取其他组织生活
     * othersAction_queryListApp
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("othersAction_queryListApp.action")
    Observable<BaseData<ThemePartyBean>> queryOthers(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("accountId") int accountId,
            @Query("unitId") int unitId
    );

    /**
     * 线上评比数据
     * advisoryAction_queryListApp
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("votePeopleAction_queryListApp.action")
    Observable<BaseData<XspbBean>> queryXspbs(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize
    );

    /**
     * 查询投票候选人
     * advisoryAction_queryListApp
     * votePeopleAction_queryCandidateList.action
     * votePeopleAction_queryCandidateList2.action
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST
    Observable<BaseData<VotePeopleBean>> queryVotePeoples(
            @Url String url,
            @Query("voteId") int voteId
    );

    /**
     * 投票
     * advisoryAction_queryListApp
     *
     * @param voteId    投票id
     * @param accountId
     * @param partyId   候选人id
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("votePeopleAction_vote.action")
    Observable<BaseData> votePeople(
            @Query("voteId") int voteId,
            @Query("partyId") int partyId
    );


    /**
     * 主题教育
     * subjeducAction_querySubjec
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("subjeducAction_querySubjec.action")
    Observable<BaseData<ZtjyBean>> queryZtjys(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("unitid") int unitid,
            @Query("eductype") int eductype
    );

    /*
     * 上传图片
     */
//    @Multipart
//    @FormUrlEncoded
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST
    Observable<BaseData> loadImage(
            @Url String url,
            @Body RequestBody requestBody
    );

    /*
     * 上传视频
     */
    @Multipart
//    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST
    Observable<BaseData> loadVideoFile(
            @Url String url,
            @Part("description") RequestBody requestBody,
            @Part MultipartBody.Part file
    );


    /**
     * 新增帮扶活动
     *
     * @param url
     * @param theme
     * @param context
     * @param outtime
     * @param unitId
     * @param pic1
     * @param pic2
     * @param pic3
     * @param pic4
     * @param pic5
     * @param pic6
     * @param pic7
     * @param pic8
     * @param pic9
     * @param publicityTime
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST
    Observable<BaseData> addBfhdzs(
            @Url String url,
            @Query("title") String title,
            @Query("context") String context,
            @Query("activityDate") String outtime,
            @Query("accountId") String accountId,
            @Query("pic1") String pic1,
            @Query("pic2") String pic2,
            @Query("pic3") String pic3,
            @Query("pic4") String pic4,
            @Query("pic5") String pic5,
            @Query("pic6") String pic6,
            @Query("pic7") String pic7,
            @Query("pic8") String pic8,
            @Query("pic9") String pic9
    );

    /**
     * 添加联系户
     * <p>
     * name	党员姓名
     * phone	党员联系电话
     * cname	联系户姓名
     * cphone	联系户电话
     * caddress	联系户住址
     * workunit	联系户工作单位
     * unitId	组织id
     *
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("partycontactAction_addPartycontactList.action")
    Observable<BaseData> addDylxh(
            @Query("name") String name,
            @Query("phone") String phone,
            @Query("cname") String cname,
            @Query("cphone") String cphone,
            @Query("caddress") String caddress,
            @Query("workunit") String workunit,
            @Query("unitId") String unitId,
            @Query("pcatime") String pcatime,
            @Query("operatorId") String operatorId
    );

    /**
     * 查询人员
     *
     * @param partyBranchId 党支部
     * @param partyGroupId  党小组
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("partyMembersAction_queryOrgAllList.action")
    Observable<BaseData<PersonnelBean>> queryAllOrg(
            @Query("partyBranchId") String partyBranchId,
            @Query("partyGroupId") String partyGroupId
    );

    /**
     * 通过ID查询理论学习
     *
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("studyNewsAction_queryByIdApp.action")
    Observable<StudyBean> queryStudyDetails(@Query("id") String id);

    /**
     * 两学一做
     * infomationAction_queryInfomationList
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("infomationAction_queryInfomationList.action")
    Observable<BaseData<NewsBean>> queryLxyzs(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize
    );

    /**
     * 查询热门学习
     * infomationAction_queryInfomationList
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("studyNewsAction_queryHotStudyList.action")
    Observable<BaseData<NewsBean>> queryHotStudys();

    /**
     * 查询最新工作状态
     * infomationAction_queryInfomationList
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("subjeducAction_queryHotEducation.action")
    Observable<BaseData<ZtjyBean>> queryHotEducations();

    /*
     * 添加主题党日
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST
    Observable<BaseData> addThemeDay(
            @Url String url,
            @Body RequestBody requestBody
    );


    /**
     * 添加联系户
     * <p>
     * name	党员姓名
     * phone	党员联系电话
     * cname	联系户姓名
     * cphone	联系户电话
     * caddress	联系户住址
     * workunit	联系户工作单位
     * unitId	组织id
     *
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("povertyHelperAction_addPovertyHelper.action")
    Observable<BaseData> addBfzrr(
            @Query("accountId") String accountId,
            @Query("name") String name,
            @Query("type") int type,
            @Query("dutyUnit") String dutyUnit,
            @Query("dutyPerson") String dutyPerson,
            @Query("dutySex") int dutySex,
            @Query("dutyTel") String dutyTel
    );


    /**
     * 查询经纬度
     * advisoryAction_queryListApp
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("lonlatAction_queryLonlatByDeptId.action")
    Observable<BaseData<LonlatBean>> queryLonlats(
            @Query("parentId") int unitId
    );

    /**
     * 新增轨迹，经纬度
     * advisoryAction_queryListApp
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("lonlatLogAction_save.action")
    Observable<BaseData> addLonlat(
            @Query("longitude") String longitude,
            @Query("latitude") String latitude
    );

    /**
     * 查询时间督办的详情
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("urgeDetailAction_queryByIdApp.action")
    Observable<UrgeDetail> queryUrgeDetailById(
            @Query("id") int id
    );


    /**
     * 三会一课报表查询接口
     *
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST
    Observable<BaseData<StatisticsBean>> queryThreelessonStatis(
            @Url String url,
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("unitId") int unitid,
            @Query("beginTime") String beginTime,
            @Query("endTime") String endTime,
            @Query("total") int total
    );

    /**
     * 组织生活报表查询接口
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("lifeStatisAction_queryLifestatison.action")
    Observable<BaseData<StatisticsBean>> queryLifestatison(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("unitId") int unitid,
            @Query("lifeType") int lifeType,
            @Query("beginTime") String beginTime,
            @Query("endTime") String endTime,
            @Query("total") int total
    );

    /**
     * 新增单位简介
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST
    Observable<BaseData> updateUnitInfo(
            @Url String url,
            @Body RequestBody requestBody
    );

    /**
     * 查询督办事件列表
     * urgeDetailAction_queryListApp
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST
    Observable<BaseData<DzzhjBean>> queryDzzhjs(
            @Url String url,
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("unitId") int unitId
    );


    /**
     * 添加村民事务代表
     *
     * @param receiveTime
     * @param applicant
     * @param content
     * @param assignee
     * @param replyOpinion
     * @param completeTime
     * @param result
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("affairsAgencyAction_addEntity.action")
    Observable<BaseData> addCmswdb(
            @Query("receiveTime") String receiveTime,
            @Query("applicant") String applicant,
            @Query("content") String content,
            @Query("assignee") String assignee,
            @Query("replyOpinion") String replyOpinion,
            @Query("completeTime") String completeTime,
            @Query("result") String result
    );


    /*
     * 添加主题党日
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("affairsAgencyAction_addEntity.action")
    Observable<BaseData> addCmswdb(
            @Body RequestBody requestBody
    );

    /*
     * 查询年份
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("orgDocumentAction_queryYear.action")
    Observable<BaseData> queryYear(
            @Query("type") String type
    );

    /**
     * 查询督办事件列表
     * urgeDetailAction_queryListApp
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("orgDocumentAction_queryListApp.action")
    Observable<BaseData<DocumentBean>> queryDocuments(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("type") int type,
            @Query("year") int year
    );

    /**
     * 添加微心愿
     *
     * @param name       姓名	String
     * @param content    心愿内容	String
     * @param wishinfo   具体心愿诉求	String
     * @param telephone  联系方式	    String
     * @param tinytime   时间	String yyyy-MM-dd格式
     * @param townshipId 乡镇ID	Int
     * @param villageId  街村ID	int
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("tinywishAction_addTinywishList.action")
    Observable<BaseData> addWxy(
            @Query("name") String name,
            @Query("content") String content,
            @Query("wishinfo") String wishinfo,
            @Query("telephone") String telephone,
            @Query("tinytime") String tinytime,
            @Query("townshipId") int townshipId,
            @Query("villageId") int villageId
    );

    /**
     * 认领微心愿
     *
     * @param id
     * @param claimName
     * @param claimTel
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("tinywishAction_claimTinywish.action")
    Observable<BaseData> claimWxy(
            @Query("id") int id,
            @Query("claimName") String claimName,
            @Query("claimTel") String claimTel
    );

    /**
     * 通过线上评比ID查询候选对象列表
     * urgeDetailAction_queryListApp
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("voteCandidateAction_queryListApp.action")
    Observable<BaseData<VotePeopleDetailsBean>> queryVoteCandidate(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("voteId") int voteId
    );

    /**
     * 查询版本
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("versionAction_queryVersion.action")
    Observable<VersionBean> queryNewVersion();

    /*
     * 新加党组织换届
     * 新加村委会换届
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST
    Observable<BaseData> addDzzhj(
            @Url String url,
            @Body RequestBody requestBody
    );

    /*
     * 新加村务公开
     * 新加党务公开
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST
    Observable<BaseData> addHamletaffList(
            @Url String url,
            @Body RequestBody requestBody
    );

    /*
     * 新加主题教育
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("subjeducAction_addEducsubj.action")
    Observable<BaseData> addEducsubj(
            @Query("theme") String theme,
            @Query("content") String content,
            @Query("eduDate") String eduDate,
            @Query("unitId") String unitId,
            @Query("eductype") int eductype,
            @Query("staff") String staff,
            @Query("site") String site,
            @Query("pic1") String pic1,
            @Query("pic2") String pic2,
            @Query("pic3") String pic3,
            @Query("pic4") String pic4,
            @Query("pic5") String pic5,
            @Query("pic6") String pic6,
            @Query("pic7") String pic7,
            @Query("pic8") String pic8,
            @Query("pic9") String pic9,
            @Query("videoUrl") String videoUrl,
            @Query("videoThumbnailUrl") String videoThumbnailUrl,
            @Query("operatorId") String operatorId
    );

    /*
     * 新加主题教育
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("subjeducAction_addEducsubj.action")
    Observable<BaseData> addEducsubj(
            @Body RequestBody requestBody
    );


    /**
     * 发展党员规程
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("partyRuleAction_queryPartyRuleList.action")
    Observable<BaseData<XwqlqdBean>> queryFzdygc(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize
    );

    /**
     * 组织关系转接
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("orgTransferAction_queryOrgTransferList.action")
    Observable<BaseData<XwqlqdBean>> queryZzgxzj(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize
    );


    /**
     * 获取党支部人员列表
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST
    Observable<BaseData<PartyBranchPersonBean>> queryPartyPerson(
            @Url String url,
            @QueryMap Map<String, String> param
    );

    /**
     * 查询微心愿数据
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("tinywishAction_queryCount.action")
    Observable<WxyCountBean> queryWxyCount();

    /**
     * 查询帮扶责任人
     * advisoryAction_queryListApp
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("povertyHelperAction_queryListApp.action")
    Observable<BaseData<BfzrrglBean>> queryBfzrrs(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("unitId") int unitId
    );

    /**
     * 村务公开信息查询接口
     * newsAction_queryListApp.action?pageSize=5&pageNum
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("partyAffAction_queryPartyaff.action")
    Observable<BaseData<CwgkBean>> queryCwgks(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("type") int type,
            @Query("unitId") int unitId
    );

    /**
     * 帮扶活动展示
     * povertyActivityAction_queryListApp
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("povertyActivityAction_queryListApp.action")
    Observable<BaseData<BfhdzsBean>> queryBfhdzss(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("unitId") int unitId
    );


    /**
     * 设岗定责
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("dutyAction_queryListApp.action")
    Observable<BaseData<SgdzBean>> querySgdz(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("unitId") int unitId
    );

    /**
     * 新增设岗定责
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST
    Observable<BaseData> addSgdz(
            @Url String url,
            @Body RequestBody requestBody
    );

    /**
     * 通过id查询学习详情
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("studyNewsAction_queryByIdApp.action")
    Observable<StudyNewsBean> queryNewById(
            @Query("id") int newsId
    );

    /**
     * 查询最新通知
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("noticeDetailAction_queryRoundList.action")
    Observable<BaseData<RoundBean>> queryRoundList();

    /**
     * 通过id查询投票详情
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("votePeopleAction_queryVoteDetail.action")
    Observable<XspbDetailsBean> queryVoteDetailById(
            @Query("voteId") int voteId
    );

    /**
     * 获取会议列表
     * newsAction_queryListApp.action?pageSize=5&pageNum
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("confAction_queryListApp.action")
    Observable<BaseData<ConfBean>> queryVideoConfs(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("confState") int confState,
            @Query("accountId") String accountId
    );

    /**
     * 创建会议
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("confAction_createConfApp.action")
    Observable<BaseData> createConf(
            @Query("unitIdsAndSiteIds") String unitIdsAndSiteIds,
            @Query("accountId") String accountId
    );

    /**
     * 查询收藏列表
     *
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("collectionAction_queryListApp.action")
    Observable<BaseData<CollectionBean>> querycCollectiont(
            @Query("accountId") String accountId,
            @Query("pageNum") int pageNum
    );

    /**
     * 添加收藏
     *
     * @param type      收藏类型：0学习  1视听学习
     * @param itemId
     * @param accountId
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("collectionAction_addCollection.action")
    Observable<BaseData> addCollectiont(
            @Query("itemId") int itemId
    );

    /**
     * 删除收藏
     *
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("collectionAction_delCollection.action")
    Observable<BaseData> delCollectiont(
            @Query("collectionId") String collectionId
    );

    /**
     * 获取评论
     * newsAction_queryListApp.action?pageSize=5&pageNum
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("newsCommentAction_queryListApp.action")
    Observable<BaseData<CommentBean>> getCommentList(
            @Query("studyId") int studyId,
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize
    );

    /**
     * 添加评论
     * newsAction_queryListApp.action?pageSize=5&pageNum
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("newsCommentAction_addComment.action")
    Observable<BaseData> addComment(
            @Query("newsId") int newsId,
            @Query("content") String content
    );

    /**
     * 删除评论
     * newsCommentAction_delComment
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("newsCommentAction_delComment.action")
    Observable<BaseData> delComment(
            @Query("newsCommentId") int newsCommentId
    );

    /**
     * 获取评论
     * newsAction_queryListApp.action?pageSize=5&pageNum
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("notebookAction_queryListApp.action")
    Observable<BaseData<NoteBean>> getNoteList(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize
    );

    /**
     * 记事本更新
     * newsAction_queryListApp.action?pageSize=5&pageNum
     * id	id	int	选填	11
     * title	标题	string	必填	200
     * context	内容	string	必填	2000
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("notebookAction_updateEntity.action")
    Observable<BaseData> updateNote(
            @Body RequestBody body
    );

    /**
     * 记事本删除
     * <p>
     * newsAction_queryListApp.action?pageSize=5&pageNum
     * <p>
     * id	id	int	选填	11
     * title	标题	string	必填	200
     * context	内容	string	必填	2000
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("notebookAction_delEntity.action")
    Observable<BaseData> delNote(
            @Query("ids") String ids
    );

    /**
     * 查询背景图片
     *
     * @param ids
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("backgroundAction_queryDetail.action")
    Observable<BaseData<String>> queryBackground(
            @Query("type") int type
    );

    /**
     * 添加图说本村
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST
    Observable<BaseData> addTsbc(
            @Url String url,
            @Body RequestBody body
    );

    /**
     * 查询图说本村
     *
     * @param ids pageNum
     *            pageSize
     *            unitId
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST
    Observable<BaseData<TsbcBean>> queryTsbcList(
            @Url String url,
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("unitId") int unitId
    );
    /**
     * 查询图说本村
     *
     * @param ids pageNum
     *            pageSize
     *            unitId
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST
    Observable<BaseData<TsbcBean>> queryTsbcList(
            @Url String url,
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("unitId") int unitId,
            @Query("year") int year
    );

    /**
     * 查询党组织人数
     *
     * @param ids
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("deptOrgInfoAction_queryPartyCount.action")
    Observable<OrgInfoCountBean> queryPartyCount(
            @Query("unitId") int unitId
    );

    /**
     * 查询村党组织
     * 村党组织type传1
     *村委会type传2
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("deptOrgInfoAction_queryListApp.action")
    Observable<BaseData<DzzcyBean>> queryOrgPeopleList(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("unitId") int unitId,
            @Query("type") int type
    );

    /**
     * 查询村监会
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("deptSupervisionAction_queryListApp.action")
    Observable<BaseData<DzzcyBean>> querySupervisionList(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("unitId") int unitId
    );

    /**
     * 查询村合作组织
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("deptCooperationAction_queryListApp.action")
    Observable<BaseData<DzzcyBean>> queryCooperationList(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("unitId") int unitId
    );

    /**
     * 会议中添加会场
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("confAction_addSiteApp.action")
    Observable<BaseData> addSite(
            @Query("confId") String confId,
            @Query("unitIdsAndSiteIds") String unitIdsAndSiteIds
    );

    /**
     * 会议中挂断会场
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("confAction_removeSiteApp.action")
    Observable<BaseData> removeSite(
            @Query("confId") String confId,
            @Query("siteIds") String siteIds
    );

    /**
     * 查询集体经济
     * collectiveEconomyAction_queryDetail
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("collectiveEconomyAction_queryDetail.action")
    Observable<DepartmentDetailsBean> queryJtjjDetail(
            @Query("unitId") int unitId
    );

}
