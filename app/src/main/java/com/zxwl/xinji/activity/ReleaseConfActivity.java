package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jaeger.library.StatusBarUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.commonlibrary.utils.DialogUtils;
import com.zxwl.commonlibrary.utils.KeyBoardUtil;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.bean.response.PersonnelBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.FullyGridLayoutManager;
import com.zxwl.xinji.adapter.SelectImageAdapter;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.fragment.RefreshRecyclerFragment;
import com.zxwl.xinji.utils.Base64Utils;
import com.zxwl.xinji.utils.PreferenceUtil;
import com.zxwl.xinji.widget.ContainsEmojiEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.functions.Func1;

/**
 * 发布三会一课
 */
public class ReleaseConfActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvRightOperate;
    private ContainsEmojiEditText etTitle;
    private ContainsEmojiEditText etContent;

    private TextView tvConfTypeLable;
    private TextView tvConfType;
    private TextView tvConfTime;
    private TextView tvAddressLable;
    private TextView tvConfPersonnel;
    private TextView tvPersonnelLable;
    private ContainsEmojiEditText etConfAddress;

    private LinearLayout llConfType;
    private LinearLayout llConfPersonnel;

    private LinearLayout llAddress;

    private LinearLayout llHost;
    private TextView tvHontLable;
    private ContainsEmojiEditText etHost;
    private TextView tvTimeLable;

    private RecyclerView rvSelect;
    private SelectImageAdapter selectImageAdapter;
    private List<LocalMedia> selectListImage = new ArrayList<>();//选择的图片
    private List<LocalMedia> selectListVideo = new ArrayList<>();//选择的视频

    private LocalMedia selectVideo;//选择的视频

    private List<PersonnelBean> selectPersonnes;

    public static final String TYPE_SHYK = "三会一课";

    public static final String TYPE_ZTDR = "主题党日";

    public static final String TYPE_MZPY = "民主评议";

    public static final String TYPE_ZZSHH = "组织生活会";

    public static final String TYPE_MORE = "其他";

    public static final String TYPE_GZDT = "工作动态";

    public static final String TYPE_XSPB = "线上评比";

    public static final String TYPE_DZZHJ = "党组织换届";

    public static final String TYPE_CWHHJ = "村委会换届";

    public static final String TYPE_ZYFW = "志愿服务";

    //标题
    public static final String TITLE = "TITLE";

    //标题
    private String title;

    private LoginBean.AccountBean accountBean;

    private String videoUrl;
    private String videoImageUrl;

    //主持人
    private String host;

    //应到人数
    private String users;

    //实到人数
    private String attendUsers;

    //列席人员姓名：张三!*!李四
    private String attendNames;

    private String theme;
    private String content;
    private String address;
    private String time;
    private String reviewedNum;

    private String loadImageUrl;
    private String loadVideoUrl;
    private String requestUrl;

    private int confType = -1;//会议类型

    private boolean showTime = false;//是否显示时间选择

    private LinearLayout llForm;
    private TextView tvFormLable;
    private TextView tvConfForm;

    //应到人数
    private LinearLayout llShouldArrive;
    private TextView tvShouldArriveLable;
    private ContainsEmojiEditText etShouldArrive;

    //实到人数
    private LinearLayout llActual;
    private TextView tvActualLable;
    private ContainsEmojiEditText etActual;

    //输入内容提示
    private TextView tvAgreedLable;

    private LinearLayout llComment;
    private TextView tvCommentLable;
    private ContainsEmojiEditText etComment;

    public static void startActivity(Context context, String title) {
        Intent intent = new Intent(context, ReleaseConfActivity.class);
        intent.putExtra(TITLE, title);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvRightOperate = (TextView) findViewById(R.id.tv_right_operate);
        etTitle = (ContainsEmojiEditText) findViewById(R.id.et_title);
        etContent = (ContainsEmojiEditText) findViewById(R.id.et_content);
        rvSelect = (RecyclerView) findViewById(R.id.rv_select);

        tvConfTypeLable = (TextView) findViewById(R.id.tv_conf_type_lable);
        tvConfType = (TextView) findViewById(R.id.tv_conf_type);
        tvConfTime = (TextView) findViewById(R.id.tv_conf_time);
        tvAddressLable = (TextView) findViewById(R.id.tv_address_lable);
        tvConfPersonnel = (TextView) findViewById(R.id.tv_conf_personnel);
        tvPersonnelLable = (TextView) findViewById(R.id.tv_personnel_lable);
        etConfAddress = (ContainsEmojiEditText) findViewById(R.id.et_conf_address);

        llConfType = (LinearLayout) findViewById(R.id.ll_conf_type);

        llConfPersonnel = (LinearLayout) findViewById(R.id.ll_conf_personnel);
        llAddress = (LinearLayout) findViewById(R.id.ll_address);

        llHost = (LinearLayout) findViewById(R.id.ll_host);
        tvHontLable = (TextView) findViewById(R.id.tv_hont_lable);
        etHost = (ContainsEmojiEditText) findViewById(R.id.et_host);

        tvTimeLable = (TextView) findViewById(R.id.tv_time_lable);

        llForm = (LinearLayout) findViewById(R.id.ll_form);
        tvFormLable = (TextView) findViewById(R.id.tv_form_lable);
        tvConfForm = (TextView) findViewById(R.id.tv_conf_form);
        llShouldArrive = (LinearLayout) findViewById(R.id.ll_should_arrive);
        tvShouldArriveLable = (TextView) findViewById(R.id.tv_should_arrive_lable);
        etShouldArrive = (ContainsEmojiEditText) findViewById(R.id.et_should_arrive);
        llActual = (LinearLayout) findViewById(R.id.ll_actual);
        tvActualLable = (TextView) findViewById(R.id.tv_actual_lable);
        etActual = (ContainsEmojiEditText) findViewById(R.id.et_actual);
        tvAgreedLable = (TextView) findViewById(R.id.tv_agreed_lable);

        llComment = (LinearLayout) findViewById(R.id.ll_comment);
        tvCommentLable = (TextView) findViewById(R.id.tv_comment_lable);
        etComment = (ContainsEmojiEditText) findViewById(R.id.et_comment);
    }

    /**
     * 一天的毫秒数
     */
    private long daySs = 24 * 60 * 60 * 1000;

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        tvRightOperate.setText("发布");
        tvRightOperate.setVisibility(View.VISIBLE);

        title = getIntent().getStringExtra(TITLE);

        tvTopTitle.setText(title);

        showTime = TYPE_XSPB.equals(title);

        if (TYPE_XSPB.equals(title)) {
            long currentLong = DateUtil.stringToLong(DateUtil.getCurrentTime(DateUtil.FORMAT_DATE), DateUtil.FORMAT_DATE);

            time = DateUtil.longToString(currentLong + 3 * daySs, DateUtil.FORMAT_DATE) + " 23:59";
            tvConfTime.setText(time);
        } else {
            time = DateUtil.getCurrentTime(showTime ? DateUtil.FORMAT_DATE_TIME : DateUtil.FORMAT_DATE);
            tvConfTime.setText(time);
        }

        accountBean = PreferenceUtil.getUserInfo(this);

        register();

        initRecycler();

        initView();
    }

    private void initView() {
        switch (title) {
            //工作动态
            case TYPE_GZDT:
                loadImageUrl = Urls.LOAD_IAMGE_ZTJY;
                loadVideoUrl = Urls.LOAD_VIDEO_ZTJY;

//                setContentLength(2000);

                llConfType.setVisibility(View.VISIBLE);
                tvConfType.setText("请选择");
                tvConfTypeLable.setText("类型");

                llConfPersonnel.setVisibility(View.GONE);

                llHost.setVisibility(View.VISIBLE);

                etTitle.setHint("请输入主题");
                etContent.setHint("请输入内容");

                etHost.setHint("请输入人员");
                tvHontLable.setText("人员");

                llHost.setVisibility(View.GONE);
                llAddress.setVisibility(View.GONE);
                break;

            //线上评比
            case TYPE_XSPB:
                loadImageUrl = Urls.LOAD_IAMGE_VOTE;
                requestUrl = Urls.ADD_VOTE;

                llConfType.setVisibility(View.GONE);
                llHost.setVisibility(View.GONE);

                etTitle.setHint("请输入评比名称");
                etConfAddress.setHint("请输入部门");
                etContent.setHint("请输入评比须知");
                tvAddressLable.setText("组织部门");
                tvTimeLable.setText("截止时间");
                tvPersonnelLable.setText("候选人");
                tvConfPersonnel.setText("请选择候选人员");
                break;

            //主题党日
            case TYPE_ZTDR:
                loadImageUrl = Urls.LOAD_IAMGE_ZTDR;
                loadVideoUrl = Urls.LOAD_VIDEO_ZTDR;
                requestUrl = Urls.ADD_ZTDR;

                llConfType.setVisibility(View.GONE);

                llForm.setVisibility(View.VISIBLE);
                llShouldArrive.setVisibility(View.VISIBLE);
                llActual.setVisibility(View.VISIBLE);
                tvAgreedLable.setVisibility(View.VISIBLE);

                tvTimeLable.setText("时间");
                etTitle.setHint("请输入主题");
                tvAgreedLable.setText("主要内容");
                etContent.setHint("请输入主要内容");
                break;

            //三会一课
            case TYPE_SHYK:
                loadImageUrl = Urls.LOAD_IAMGE_SHYK;
                loadVideoUrl = Urls.LOAD_VIDEO_SHYK;
                requestUrl = Urls.ADD_SHYK;

                tvConfTypeLable.setText("类型");
                tvConfType.setText("请选择类型");
                llConfType.setVisibility(View.VISIBLE);

                llShouldArrive.setVisibility(View.VISIBLE);
                llActual.setVisibility(View.VISIBLE);
                tvAgreedLable.setVisibility(View.VISIBLE);

                etTitle.setHint("请输入主题");

                tvAgreedLable.setText("主要内容");
                etContent.setHint("请输入主要内容");
                break;

            //民主评议
            case TYPE_MZPY:
                loadImageUrl = Urls.LOAD_IAMGE_MZPY;
                loadVideoUrl = Urls.LOAD_VIDEO_MZPY;
                requestUrl = Urls.ADD_MZPY;

                tvConfTypeLable.setText("类型");
                tvConfType.setText("请选择类型");

                tvTimeLable.setText("活动时间");

                llConfType.setVisibility(View.VISIBLE);

                llShouldArrive.setVisibility(View.VISIBLE);
                llActual.setVisibility(View.VISIBLE);
                tvAgreedLable.setVisibility(View.VISIBLE);

                llComment.setVisibility(View.VISIBLE);

                tvAgreedLable.setText("评议结果");
                etContent.setHint("请输入评议结果");

                tvTimeLable.setText("时间");
                etTitle.setHint("请输入主题");
                break;

            //组织生活会
            case TYPE_ZZSHH:
                loadImageUrl = Urls.LOAD_IAMGE_ZZSHH;
                loadVideoUrl = Urls.LOAD_VIDEO_ZZSHH;
                requestUrl = Urls.ADD_ZZSHH;

                tvConfTypeLable.setText("类型");
                tvConfType.setText("请选择类型");

                tvTimeLable.setText("时间");

                llConfType.setVisibility(View.VISIBLE);

                llShouldArrive.setVisibility(View.VISIBLE);
                llActual.setVisibility(View.VISIBLE);
                tvAgreedLable.setVisibility(View.VISIBLE);

                etTitle.setHint("请输入主题");

                tvAgreedLable.setText("主要内容");
                etContent.setHint("请输入主要内容");
                break;

            //其他
            case TYPE_MORE:
                loadImageUrl = Urls.LOAD_IAMGE_MORE;
                loadVideoUrl = Urls.LOAD_VIDEO_MORE;
                requestUrl = Urls.ADD_MORE;

                tvTimeLable.setText("时间");
                llConfType.setVisibility(View.GONE);

                llShouldArrive.setVisibility(View.VISIBLE);
                llActual.setVisibility(View.VISIBLE);
                tvAgreedLable.setVisibility(View.VISIBLE);

                tvHontLable.setText("负责人");
                etHost.setHint("请输入负责人");

                etTitle.setHint("请输入主题");

                tvAgreedLable.setText("详细内容");
                etContent.setHint("请输入详细内容");
                break;

            //村委会换届
            case TYPE_DZZHJ:
            case TYPE_CWHHJ:
                if (TYPE_DZZHJ.equals(title)) {
                    loadImageUrl = Urls.LOAD_IAMGE_PARTY_ELECTION;
                    requestUrl = Urls.ADD_PARTY_ELECTION;
                } else {
                    loadImageUrl = Urls.LOAD_IAMGE_HAMLET_ELECTION;
                    requestUrl = Urls.ADD_HAMLET_ELECTION;
                }

//                setContentLength(500);

                etTitle.setHint("请输入标题");
                tvTimeLable.setText("选举时间");

                tvAddressLable.setText(title.equals(TYPE_DZZHJ) ? "应到会党员人数" : "选民数");
                tvHontLable.setText(title.equals(TYPE_DZZHJ) ? "参选党员数量" : "参加选民人数");

                etConfAddress.setHint(title.equals(TYPE_DZZHJ) ? "请输入应到会党员人数" : "请输入选民数");
                etHost.setHint(title.equals(TYPE_DZZHJ) ? "请输入参选党员数量" : "请输入参加选举选民数");

                etConfAddress.setInputType(InputType.TYPE_CLASS_NUMBER);
                etHost.setInputType(InputType.TYPE_CLASS_NUMBER);

                tvPersonnelLable.setText("选举结果");
                tvConfPersonnel.setText("请选择选举结果");

                etContent.setHint("请输入详细内容");

                initRecycler();
                break;

            //志愿服务
            case TYPE_ZYFW:
                loadImageUrl = Urls.LOAD_IAMGE_ZYFW;
                loadVideoUrl = Urls.LOAD_VIDEO_ZYFW;

                requestUrl = Urls.ADD_ZYFW;

                etTitle.setHint("请输入标题");
                tvTimeLable.setText("时间");

                llConfType.setVisibility(View.GONE);
                llHost.setVisibility(View.GONE);
                llAddress.setVisibility(View.GONE);

                etContent.setHint("请输入详细内容");

                initRecycler();
                break;
        }
    }

    private void setContentLength(int maxLength) {
        InputFilter[] filters = {new InputFilter.LengthFilter(maxLength)};
        etContent.setFilters(filters);
    }

    private void initRecycler() {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        rvSelect.setLayoutManager(manager);
        selectImageAdapter = new SelectImageAdapter(R.layout.gv_filter_image, new ArrayList<>(), onAddPicClickListener);
        selectImageAdapter.setSelectMax(9);

        selectImageAdapter.setOnDelClickListener(new SelectImageAdapter.OnDelClickListener() {
            @Override
            public void onItemClick(LocalMedia localMedia) {
                String pictureType = localMedia.getPictureType();

                int mediaType = PictureMimeType.pictureToVideo(pictureType);
                if (PictureConfig.TYPE_IMAGE == mediaType) {
                    selectListImage.remove(localMedia);
                } else {
                    selectListVideo.remove(localMedia);
                    selectVideo = null;
                }
            }
        });

        selectImageAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (selectImageAdapter.getData().size() > 0) {
                    LocalMedia media = (LocalMedia) selectImageAdapter.getItem(position);
                    String pictureType = media.getPictureType();

                    int mediaType = PictureMimeType.pictureToVideo(pictureType);

                    //判断类型
                    switch (mediaType) {
                        case PictureConfig.TYPE_IMAGE:
                            // 预览图片 可自定长按保存路径
                            //PictureSelector.create(MainActivity.this).themeStyle(themeId).externalPicturePreview(position, "/custom_file", selectList);
                            PictureSelector.create(ReleaseConfActivity.this).themeStyle(R.style.picture_default_style).openExternalPreview(position, selectListImage);
                            break;

                        case PictureConfig.TYPE_VIDEO:
                            // 预览视频
                            PictureSelector.create(ReleaseConfActivity.this).externalPictureVideo(media.getPath());
                            break;

                        case 3:
                            // 预览音频
                            PictureSelector.create(ReleaseConfActivity.this).externalPictureAudio(media.getPath());
                            break;
                    }
                }
            }
        });
        rvSelect.setAdapter(selectImageAdapter);
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(this);
        tvRightOperate.setOnClickListener(this);
        tvConfType.setOnClickListener(this);
        tvConfTime.setOnClickListener(this);
        tvConfPersonnel.setOnClickListener(this);
        tvConfForm.setOnClickListener(this);

        etContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //canScrollVertically()方法为判断指定方向上是否可以滚动,参数为正数或负数,负数检查向上是否可以滚动,正数为检查向下是否可以滚动
                if (etContent.canScrollVertically(1) || etContent.canScrollVertically(-1)) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);//requestDisallowInterceptTouchEvent();要求父类布局不在拦截触摸事件
                    if (event.getAction() == MotionEvent.ACTION_UP) { //判断是否松开
                        v.getParent().requestDisallowInterceptTouchEvent(false); //requestDisallowInterceptTouchEvent();让父类布局继续拦截触摸事件
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_release_conf;
    }

    @Override
    public void onClick(View v) {
        long currentTime = 0;
        long startTimeLong = 0;
        switch (v.getId()) {
            case R.id.iv_back_operate:
                finish();
                break;

            case R.id.tv_right_operate:
                switch (title) {
                    //线上评比
                    case TYPE_XSPB:
                        theme = etTitle.getText().toString().trim();
                        if (TextUtils.isEmpty(theme)) {
                            ToastHelper.showShort("评比名称不能为空");
                            return;
                        }

                        address = etConfAddress.getText().toString().trim();
                        if (TextUtils.isEmpty(address)) {
                            ToastHelper.showShort("组织部门不能为空");
                            return;
                        }

                        content = etContent.getText().toString().trim();
                        if (TextUtils.isEmpty(content)) {
                            ToastHelper.showShort("评比须知不能为空");
                            return;
                        }

                        String endTime = tvConfTime.getText().toString().trim();
                        long endTimeLong = DateUtil.stringToLong(endTime, DateUtil.FORMAT_DATE_TIME);

                        currentTime = DateUtil.stringToLong(DateUtil.getCurrentTime(DateUtil.FORMAT_DATE_TIME), DateUtil.FORMAT_DATE_TIME);

                        if (endTimeLong - currentTime < 0) {
                            ToastHelper.showShort("截止时间不能小于当前时间");
                            break;
                        }

//                        if (null == selectPersonnes || selectPersonnes.size() <= 0) {
//                            ToastHelper.showShort("候选人员不能为空");
//                            break;
//                        }

//                        getSelectUserId();

                        DialogUtils.showProgressDialog(ReleaseConfActivity.this, "正在上传...");

                        getOber()
                                .compose(new CustomCompose())
                                .compose(bindToLifecycle())
                                .subscribe(new RxSubscriber<BaseData>() {
                                    @Override
                                    public void onSuccess(BaseData baseData) {
                                        DialogUtils.dismissProgressDialog();
                                        if (BaseData.SUCCESS.equals(baseData.result)) {
                                            ToastHelper.showShort("上传成功");
                                            EventBus.getDefault().post(new EventMessage(Messages.REFRESH_RECYCLER, ""));
                                            finish();
                                        } else {
                                            ToastHelper.showShort(baseData.message);
                                        }
                                    }

                                    @Override
                                    protected void onError(ResponeThrowable responeThrowable) {
                                        DialogUtils.dismissProgressDialog();
                                        ToastHelper.showShort("onError:" + responeThrowable.getCause().getMessage());
                                    }
                                });
                        break;

                    //工作动态
                    case TYPE_GZDT:
                        theme = etTitle.getText().toString().trim();
                        if (TextUtils.isEmpty(theme)) {
                            ToastHelper.showShort("主题不能为空");
                            return;
                        }

                        if (-1 == gzdtType) {
                            ToastHelper.showShort("类型不能为空");
                            return;
                        }

//                        address = etConfAddress.getText().toString().trim();
//                        if (TextUtils.isEmpty(address)) {
//                            ToastHelper.showShort("地点不能为空");
//                            return;
//                        }
//
//                        host = etHost.getText().toString().trim();
//                        if (TextUtils.isEmpty(host)) {
//                            ToastHelper.showShort("人员姓名不能为空");
//                            break;
//                        }

                        content = etContent.getText().toString().trim();
                        if (TextUtils.isEmpty(content)) {
                            ToastHelper.showShort("内容不能为空");
                            return;
                        }

                        time = tvConfTime.getText().toString().trim();

                        startTimeLong = DateUtil.stringToLong(time, DateUtil.FORMAT_DATE);

                        currentTime = DateUtil.stringToLong(DateUtil.getCurrentTime(DateUtil.FORMAT_DATE), DateUtil.FORMAT_DATE);

                        if (startTimeLong - currentTime > 0) {
                            ToastHelper.showShort("时间不能大于当前时间");
                            return;
                        }

                        DialogUtils.showProgressDialog(ReleaseConfActivity.this, "正在上传...");

                        getOber()
                                .compose(new CustomCompose())
                                .compose(bindToLifecycle())
                                .subscribe(new RxSubscriber<BaseData>() {
                                    @Override
                                    public void onSuccess(BaseData baseData) {
                                        DialogUtils.dismissProgressDialog();
                                        if (BaseData.SUCCESS.equals(baseData.result)) {
                                            ToastHelper.showShort("上传成功");
                                            EventBus.getDefault().post(new EventMessage(Messages.REFRESH_RECYCLER, ""));
                                            finish();
                                        } else {
                                            ToastHelper.showShort(baseData.message);
                                        }
                                    }

                                    @Override
                                    protected void onError(ResponeThrowable responeThrowable) {
                                        DialogUtils.dismissProgressDialog();
                                        ToastHelper.showShort("onError:" + responeThrowable.getCause().getMessage());
                                    }
                                });
                        break;

                    //主题党日
                    case TYPE_ZTDR:
                    case TYPE_MZPY:
                    case TYPE_ZZSHH:
                    case TYPE_MORE:
                    case TYPE_SHYK:
                        if (checkText()) {
                            return;
                        }

                        host = etHost.getText().toString().trim();

                        if (TextUtils.isEmpty(host)) {
                            ToastHelper.showShort("主持人不能为空");
                            return;
                        }

                        if (TYPE_SHYK.equals(title) && -1 == confType) {
                            ToastHelper.showShort("请选择会议类型");
                            return;
                        }

                        if (TYPE_ZTDR.equals(title) && -1 == confType) {
                            ToastHelper.showShort("请选择形式");
                            return;
                        }

                        if (TYPE_MZPY.equals(title) && -1 == eductype) {
                            ToastHelper.showShort("请选择会议类型");
                            return;
                        }

                        if (TYPE_ZZSHH.equals(title) && -1 == eductype) {
                            ToastHelper.showShort("请选择会议类型");
                            return;
                        }

                        users = etShouldArrive.getText().toString().trim();
                        if (TextUtils.isEmpty(users)) {
                            ToastHelper.showShort("请输入应到人数");
                            return;
                        }

                        attendUsers = etActual.getText().toString().trim();
                        if (TextUtils.isEmpty(attendUsers)) {
                            ToastHelper.showShort("请输入实到人数");
                            return;
                        }

                        reviewedNum = etComment.getText().toString().trim();
                        if (TYPE_MZPY.equals(title) && TextUtils.isEmpty(reviewedNum)) {
                            ToastHelper.showShort("请输入被评议人数");
                            return;
                        }
//                        boolean isSelectNull = null == selectPersonnes || selectPersonnes.size() <= 0;
//
//                        if (isSelectNull && !TYPE_MORE.equals(title)) {
//                            ToastHelper.showShort("请选择参会人员");
//                            return;
//                        }
//
//                        if (!isSelectNull) {
//                            //获取参会人员
//                            getSelectUserId();
//                        }

                        DialogUtils.showProgressDialog(ReleaseConfActivity.this, "正在上传...");

                        getOber()
                                .compose(new CustomCompose())
                                .compose(bindToLifecycle())
                                .subscribe(new RxSubscriber<BaseData>() {
                                    @Override
                                    public void onSuccess(BaseData baseData) {
                                        DialogUtils.dismissProgressDialog();
                                        if (BaseData.SUCCESS.equals(baseData.result)) {
                                            ToastHelper.showShort("上传成功");

                                            EventBus.getDefault().post(new EventMessage(Messages.REFRESH_RECYCLER, title));

                                            finish();
                                        } else {
                                            ToastHelper.showShort(baseData.message);
                                        }
                                    }

                                    @Override
                                    protected void onError(ResponeThrowable responeThrowable) {
                                        DialogUtils.dismissProgressDialog();
                                        ToastHelper.showShort("onError:" + responeThrowable.getCause().getMessage());
                                    }
                                });
                        break;

                    //上传党组织换届
                    //上传村委会换届
                    case TYPE_CWHHJ:
                    case TYPE_DZZHJ:
                        theme = etTitle.getText().toString().trim();
                        if (TextUtils.isEmpty(theme)) {
                            ToastHelper.showShort("主题不能为空");
                            return;
                        }

                        address = etConfAddress.getText().toString().trim();
                        if (TextUtils.isEmpty(address)) {
                            ToastHelper.showShort(title.equals(TYPE_DZZHJ) ? "应到会党员人数不能为空" : "选民数不能为空");
                            return;
                        }

                        host = etHost.getText().toString().trim();
                        if (TextUtils.isEmpty(host)) {
                            ToastHelper.showShort(title.equals(TYPE_DZZHJ) ? "参选党员数量不能为空" : "参加选举选民数不能为空");
                            break;
                        }

                        content = etContent.getText().toString().trim();
                        if (TextUtils.isEmpty(content)) {
                            ToastHelper.showShort("内容不能为空");
                            return;
                        }

                        time = tvConfTime.getText().toString().trim();

                        if (-1 == electionResultType) {
                            ToastHelper.showShort("请选择选举结果");
                            return;
                        }

                        DialogUtils.showProgressDialog(ReleaseConfActivity.this, "正在上传...");

                        getOber()
                                .compose(new CustomCompose())
                                .compose(bindToLifecycle())
                                .subscribe(new RxSubscriber<BaseData>() {
                                    @Override
                                    public void onSuccess(BaseData baseData) {
                                        DialogUtils.dismissProgressDialog();
                                        if (BaseData.SUCCESS.equals(baseData.result)) {
                                            ToastHelper.showShort("上传成功");

                                            EventBus.getDefault().post(new EventMessage(Messages.REFRESH_RECYCLER, title));

                                            finish();
                                        } else {
                                            ToastHelper.showShort(baseData.message);
                                        }
                                    }

                                    @Override
                                    protected void onError(ResponeThrowable responeThrowable) {
                                        DialogUtils.dismissProgressDialog();
                                        ToastHelper.showShort("onError:" + responeThrowable.getCause().getMessage());
                                    }
                                });
                        break;

                    //志愿服务
                    case TYPE_ZYFW:
                        theme = etTitle.getText().toString().trim();
                        if (TextUtils.isEmpty(theme)) {
                            ToastHelper.showShort("主题不能为空");
                            return;
                        }

                        content = etContent.getText().toString().trim();
                        if (TextUtils.isEmpty(content)) {
                            ToastHelper.showShort("内容不能为空");
                            return;
                        }

                        time = tvConfTime.getText().toString().trim();

                        startTimeLong = DateUtil.stringToLong(time, DateUtil.FORMAT_DATE);

                        currentTime = DateUtil.stringToLong(DateUtil.getCurrentTime(DateUtil.FORMAT_DATE), DateUtil.FORMAT_DATE);

                        if (startTimeLong - currentTime > 0) {
                            ToastHelper.showShort("时间不能大于当前时间");
                            return;
                        }

                        DialogUtils.showProgressDialog(ReleaseConfActivity.this, "正在上传...");

                        getOber()
                                .compose(new CustomCompose())
                                .compose(bindToLifecycle())
                                .subscribe(new RxSubscriber<BaseData>() {
                                    @Override
                                    public void onSuccess(BaseData baseData) {
                                        DialogUtils.dismissProgressDialog();
                                        if (BaseData.SUCCESS.equals(baseData.result)) {
                                            ToastHelper.showShort("上传成功");

                                            EventBus.getDefault().post(new EventMessage(Messages.REFRESH_RECYCLER, title));

                                            finish();
                                        } else {
                                            ToastHelper.showShort(baseData.message);
                                        }
                                    }

                                    @Override
                                    protected void onError(ResponeThrowable responeThrowable) {
                                        DialogUtils.dismissProgressDialog();
                                        ToastHelper.showShort("onError:" + responeThrowable.getCause().getMessage());
                                    }
                                });

                        break;
                }
                break;

            case R.id.tv_conf_type:
                //工作动态
                if (TYPE_GZDT.equals(title)) {
                    showConfTypeSelectDialog(TYPE_GZDT, EDUCTYPE_TYPES, eductype - 1);
                }//民主评议
                else if (TYPE_MZPY.equals(title)) {
                    showConfTypeSelectDialog(TYPE_MZPY, MZPY_TYPES, eductype - 1);
                }//组织生活会
                else if (TYPE_ZZSHH.equals(title)) {
                    showConfTypeSelectDialog(TYPE_ZZSHH, ZZSHH_TYPES, eductype - 1);
                }//三会一课
                else {
                    showConfTypeSelectDialog(TYPE_SHYK, CONF_TYPES, confType - 1);
                }
                break;

            //主题党日：请选择形式
            case R.id.tv_conf_form:
                showConfTypeSelectDialog(TYPE_ZTDR, FROM_TYPES, confType - 1);
                break;

            case R.id.tv_conf_time:
                KeyBoardUtil.closeKeybord(etTitle, ReleaseConfActivity.this);
                initLunarPicker();
                pvCustomLunar.show();
                break;

            case R.id.tv_conf_personnel:
                if (TYPE_XSPB.equals(title)) {
                    SelectPersonnelActivity.startActivity(ReleaseConfActivity.this, SelectPersonnelActivity.SELECT_RATING, selectPersonnes);
                } else if (TYPE_DZZHJ.equals(title)) {
                    showSelectDialog(dzzElectionResultResults, "请选择选举结果");
                } else if (TYPE_CWHHJ.equals(title)) {
                    showSelectDialog(cwhElectionResultResults, "请选择选举结果");
                } else {
                    SelectPersonnelActivity.startActivity(ReleaseConfActivity.this, SelectPersonnelActivity.SELECT_ATTEND, selectPersonnes);
                }
                break;
        }
    }

    private boolean checkText() {
        theme = etTitle.getText().toString().trim();

        if (TextUtils.isEmpty(theme)) {
            ToastHelper.showShort("主题不能为空");
            return true;
        }

        content = etContent.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastHelper.showShort(etContent.getHint());
            return true;
        }

        address = etConfAddress.getText().toString().trim();
        if (TextUtils.isEmpty(address) && !TYPE_MORE.equals(title)) {
            ToastHelper.showShort("地点不能为空");
            return true;
        }

//        long startTimeLong = DateUtil.stringToLong(time, DateUtil.FORMAT_DATE);
//        if (startTimeLong - System.currentTimeMillis() < 0) {
//            ToastHelper.showShort("时间必须大于当前时间");
//            return true;
//        }
        return false;
    }

    private Observable<BaseData> getOber() {
        //图片视频都有
        if (selectListVideo.size() > 0 && selectListImage.size() > 0) {
            return loadVideo()
                    .flatMap(new Func1<BaseData, Observable<BaseData>>() {
                        @Override
                        public Observable<BaseData> call(BaseData baseData) {
                            if (BaseData.SUCCESS.equals(baseData.result)) {
                                String[] videoUrls = baseData.data.split(",");
                                videoUrl = videoUrls[0];
                                videoImageUrl = videoUrls[1];
                                return loadImage();
                            } else {
                                return null;
                            }
                        }
                    }).flatMap(new Func1<BaseData, Observable<BaseData>>() {
                        @Override
                        public Observable<BaseData> call(BaseData baseData) {
                            if (BaseData.SUCCESS.equals(baseData.result)) {
                                return getAddRequest(Arrays.asList(baseData.data.split(",")));
                            } else {
                                return null;
                            }
                        }
                    });
        }//有视频
        else if (selectListVideo.size() > 0) {
            return loadVideo().
                    flatMap(new Func1<BaseData, Observable<BaseData>>() {
                        @Override
                        public Observable<BaseData> call(BaseData baseData) {
                            if (BaseData.SUCCESS.equals(baseData.result)) {
                                String[] videoUrls = baseData.data.split(",");
                                videoUrl = videoUrls[0];
                                videoImageUrl = videoUrls[1];

                                return getAddRequest(new ArrayList<>());
                            } else {
                                return null;
                            }
                        }
                    });
        }//有图片
        else if (selectListImage.size() > 0) {
            return loadImage()
                    .flatMap(new Func1<BaseData, Observable<BaseData>>() {
                        @Override
                        public Observable<BaseData> call(BaseData baseData) {
                            if (BaseData.SUCCESS.equals(baseData.result)) {
                                List<String> imgUrls = Arrays.asList(baseData.data.split(","));

                                return getAddRequest(imgUrls);
                            } else {
                                return null;
                            }
                        }
                    });
        }//只有文本消息
        else {
            return getAddRequest(new ArrayList<>());
        }
    }

    private Observable<BaseData> getAddRequest(List<String> imgUrls) {
        switch (title) {
            //工作动态
            case TYPE_GZDT:
                return addEducsubj(imgUrls);

            //三会一课
            case TYPE_SHYK:
                //主题党日
            case TYPE_ZTDR:
                //民主评议
            case TYPE_MZPY:
                //组织生活
            case TYPE_ZZSHH:
                //其他
            case TYPE_MORE:
                return addPartyDay(imgUrls);

            case TYPE_XSPB:
                return addVote(imgUrls);

            case TYPE_DZZHJ:
            case TYPE_CWHHJ:
                return addDzzgh(imgUrls);

            case TYPE_ZYFW:
                return addZyfw(imgUrls);
        }
        return null;
    }

    private Observable<BaseData> addZyfw(List<String> imgUrls) {
        RequestBody zyfwRequestBody = getZyfwRequestBody(imgUrls);

        return HttpUtils.getInstance(ReleaseConfActivity.this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .addDzzhj(
                        requestUrl,
                        zyfwRequestBody
                );
    }

    private MaterialDialog dialog;

    /**
     * 显示照片选择对话框
     */
    private void showSelectDialog() {
        dialog = new MaterialDialog.Builder(this)
                .title("提示")
                .items("选择照片", "选择视频")
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        startSelectAct(which + 1);
                        return true;
                    }
                })
                .build();
        //点击对话框以外的地方，对话框不消失
        dialog.setCanceledOnTouchOutside(false);
        //点击对话框以外的地方和返回键，对话框都不消失
        //dialog.setCancelable(false);
        dialog.show();
    }

    public static final String TITLE_FORM = "请选择形式";
    public static final String TITLE_TYPE = "请选择会议类型";
    public static final String TITLE_EDUCTYPE = "请选择类别";


    /**
     * 三会一课-会议类型
     */
    private final static String[] CONF_TYPES = {
            "党员大会",
            "党支部会",
            "党小组会",
            "党课"
    };

    /**
     * 主题党日-形式
     */
    private final static String[] FROM_TYPES = {
            "党员大会",
            "党支部会",
            "党小组会",
            "党课",
            "志愿服务",
            "参观学习",
            "其他",
    };

    private int eductype = -1;

    /**
     * 工作动态
     */
    private final static String[] EDUCTYPE_TYPES = {
//            RefreshRecyclerFragment.TYPE_DJZX,
            RefreshRecyclerFragment.TYPE_XCDT,
//            RefreshRecyclerFragment.TYPE_DXJY,
            RefreshRecyclerFragment.TYPE_TSZS
    };

    /**
     * 民主评议
     */
    private final static String[] MZPY_TYPES = {
            "党员大会",
            "党小组会"
    };

    /**
     * 组织生活会
     */
    private final static String[] ZZSHH_TYPES = {
            "党员大会", "党小组会", "支部委员会"
    };


    private MaterialDialog confTypeDialog;

    private String getDialogShowTitle(String title) {
        switch (title) {
            //三会一课
            case TYPE_SHYK:
                return "请选择会议类型";

            //主题党日
            case TYPE_ZTDR:
                return "请选择形式";

            //工作动态
            case TYPE_GZDT:
                return "请选择类别";

            //民主评议
            case TYPE_MZPY:
                return "请选择形式";

            //组织生活会
            case TYPE_ZZSHH:
                return "请选择形式";

            default:
                return "";
        }
    }

    //工作动态的类型
    private int gzdtType = -1;

    /**
     * 会议类型选择
     */
    private void showConfTypeSelectDialog(String title, String[] confTypes, int selectedIndex) {
        confTypeDialog = new MaterialDialog.Builder(this)
                .title(getDialogShowTitle(title))
                .items(confTypes)
                .itemsCallbackSingleChoice(selectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (title) {
                            //三会一课
                            case TYPE_SHYK:
                                tvConfType.setText(confTypes[which]);
                                confType = (which + 1);

                                //党课
                                if (which == 3) {
                                    tvHontLable.setText("授课讲师");
                                    etHost.setHint("请输入授课讲师");
                                    etContent.setHint("请输入授课内容");
                                } else {
                                    tvHontLable.setText("主持人");
                                    etHost.setHint("请输入主持人");
                                    etContent.setHint("请输入议定事项");
                                }
                                return true;

                            //主题党日
                            case TYPE_ZTDR:
                                tvConfForm.setText(confTypes[which]);
                                confType = (which + 1);
                                return true;

                            //工作动态
                            case TYPE_GZDT:
                                tvConfType.setText(confTypes[which]);
                                if (which == 0) {
                                    gzdtType = 2;
                                } else {
                                    gzdtType = 4;
                                }
                                return true;

                            //民主评议
                            case TYPE_MZPY:
                                tvConfType.setText(confTypes[which]);
                                eductype = (which + 1);
                                return true;

                            //组织生活会
                            case TYPE_ZZSHH:
                                tvConfType.setText(confTypes[which]);
                                eductype = (which + 1);
                                return true;
                        }
                        return true;
                    }
                })
                .build();
        //点击对话框以外的地方，对话框不消失
//        confTypeDialog.setCanceledOnTouchOutside(false);
        //点击对话框以外的地方和返回键，对话框都不消失
        //dialog.setCancelable(false);
        confTypeDialog.show();
    }


    private SelectImageAdapter.onAddPicClickListener onAddPicClickListener = new SelectImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            if (TYPE_XSPB.equals(title) || TYPE_CWHHJ.equals(title) || TYPE_DZZHJ.equals(title)) {
                startSelectAct(1);
            } else {
                showSelectDialog();
            }
        }
    };

    private void startSelectAct(int type) {
        int imageCount = selectListVideo.size() >= 1 ? 8 : 9;

        int maxSelect = 1;

        if (TYPE_XSPB.equals(title)) {
            maxSelect = 1;
        } else {
            maxSelect = type == PictureMimeType.ofImage() ? imageCount : 1;
        }

        PictureSelector.create(ReleaseConfActivity.this)
                .openGallery(type)// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
//                        .theme(themeId)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                .maxSelectNum(maxSelect)// 最大图片选择数量
                .minSelectNum(0)// 最小选择数量
                .imageSpanCount(4)// 每行显示个数
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                .previewImage(true)// 是否可预览图片
                .previewVideo(true)// 是否可预览视频
                .enablePreviewAudio(true) // 是否可播放音频
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                .enableCrop(false)// 是否裁剪
                .compress(true)// 是否压缩
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                //.compressSavePath(getPath())//压缩图片保存地址
                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .withAspectRatio(16, 9)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示
                .isGif(false)// 是否显示gif图片
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                .circleDimmedLayer(false)// 是否圆形裁剪
                .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .openClickSound(false)// 是否开启点击声音
                .selectionMedia(type == PictureMimeType.ofImage() ? selectListImage : selectListVideo)//是否传入已选图片
                //.isDragFrame(false)// 是否可拖动裁剪框(固定)
//                        .videoMaxSecond(15)
//                        .videoMinSecond(10)
                //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                //.cropCompressQuality(90)// 裁剪压缩质量 默认100
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                //.rotateEnabled(true) // 裁剪是否可旋转图片
                //.scaleEnabled(true)// 裁剪是否可放大缩小图片
                //.videoQuality()// 视频录制质量 0 or 1
                //.videoSecond()//显示多少秒以内的视频or音频也可适用
                //.recordVideoSecond()//录制视频秒数 默认60s
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    public static final String TAG = "onActivityResult";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    List<LocalMedia> tempSelectLocalMedia = PictureSelector.obtainMultipleResult(data);

                    //新的list
                    List<LocalMedia> newList = new ArrayList<>();

                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                    for (LocalMedia media : tempSelectLocalMedia) {
                        Log.i(TAG, "压缩---->" + media.getCompressPath());
                        Log.i(TAG, "原图---->" + media.getPath());
                        Log.i(TAG, "裁剪---->" + media.getCutPath());
                    }

                    //判断选择的是否是选择的视频
                    boolean isSelectVideo = tempSelectLocalMedia.size() > 0 && PictureMimeType.pictureToVideo(tempSelectLocalMedia.get(0).getPictureType()) == PictureConfig.TYPE_VIDEO;

                    //如果选择的是视频
                    if (isSelectVideo) {
                        selectVideo = tempSelectLocalMedia.get(0);
                        selectListVideo.clear();
                        selectListVideo.add(selectVideo);
                        newList.add(0, selectVideo);
                        newList.addAll(selectListImage);
                    } else {
                        selectListImage = tempSelectLocalMedia;
                        if (selectListVideo.size() > 0) {
                            newList.add(0, selectListVideo.get(0));
                        }
                        newList.addAll(selectListImage);
                    }
                    selectImageAdapter.replaceData(newList);
//                    selectImageAdapter.setList(newList);
                    selectImageAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    private TimePickerView pvCustomLunar;

    /**
     * 农历时间已扩展至 ： 1900 - 2100年
     */
    private void initLunarPicker() {
        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        selectedDate.set(
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH),
                selectedDate.get(Calendar.HOUR_OF_DAY),
                selectedDate.get(Calendar.MINUTE)
        );

        Calendar startDate = Calendar.getInstance();
        startDate.set(1900, 1, 23);

        Calendar endDate = Calendar.getInstance();
        endDate.set(2100, 1, 23);

        //时间选择器 ，自定义布局
        pvCustomLunar = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                time = getTime(date, showTime ? DateUtil.FORMAT_DATE_TIME : DateUtil.FORMAT_DATE);
                tvConfTime.setText(time);
            }
        })
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLayoutRes(R.layout.pickerview_custom_lunar, new CustomListener() {
                    @Override
                    public void customLayout(final View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        TextView tvCancle = (TextView) v.findViewById(R.id.tv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomLunar.returnData();
                                pvCustomLunar.dismiss();
                            }
                        });
                        tvCancle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomLunar.dismiss();
                            }
                        });
                    }
                })
                .setType(new boolean[]{true, true, true, showTime, showTime, false})
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(Color.RED)
                .build();
    }

    //可根据需要自行截取数据显示
    private String getTime(Date date, String pattern) {
        Log.d("getTime()", "choice date millis: " + date.getTime());
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    private void register() {
        if (isRegisterEventBus) {
            if (!EventBus.getDefault().isRegistered(this)) {
                //注册EventBsus,注意参数是this，传入activity会报错
                EventBus.getDefault().register(this);
            }
        }
    }

    private void unRegister() {
        if (isRegisterEventBus) {
            if (EventBus.getDefault().isRegistered(this)) {
                //注册EventBus,注意参数是this，传入activity会报错
                EventBus.getDefault().unregister(this);
            }
        }
    }

    //EventBus的处理事件函数  该方法可以随意取名 必须为public 必须添加注解并指定线程模型
    //之后EventBus会自动扫描到此函数，进行数据传递
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getSelectPersonnelData(EventMessage eventMessage) {
        switch (eventMessage.message) {
            //选择列席人员
            case Messages.SELECT_PERSONNEL:
                selectPersonnes = (List<PersonnelBean>) eventMessage.t;
                //列席人员数量
                int selectAdd = 0;
                //参会人员数量
                int selectConf = 0;
                //选出
                for (int i = 0; i < selectPersonnes.size(); i++) {
                    if (selectPersonnes.get(i).isAttend) {
                        selectAdd++;
                    }

                    if (selectPersonnes.get(i).isParticipants) {
                        selectConf++;
                    }
                }

                tvConfPersonnel.setText("列席人员" + selectAdd + ",参会人员" + selectConf);
                break;

            //选择评比
            case Messages.SELECT_RATING:
                selectPersonnes = (List<PersonnelBean>) eventMessage.t;
                //评比人员数量
                int selectRating = 0;
                //选出
                for (int i = 0; i < selectPersonnes.size(); i++) {
                    if (selectPersonnes.get(i).isAttend) {
                        selectRating++;
                    }
                }

                tvConfPersonnel.setText("候选人员" + selectRating);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unRegister();
    }

    /**
     * 上传视频
     *
     * @return
     */
    private Observable<BaseData> loadVideo() {
        //构建要上传的文件
        LocalMedia localMedia = selectListVideo.get(0);

        File file = new File(selectListVideo.size() > 0 ? selectListVideo.get(0).getPath() : "");
        RequestBody requestFile = RequestBody.create(MediaType.parse("application/octet-stream"), file);

        MultipartBody.Part requestFileBody = MultipartBody.Part.createFormData("upload", file.getName(), requestFile);

        // 添加描述
        String descriptionString = "hello, 这是文件描述";
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);

        return HttpUtils.getInstance(ReleaseConfActivity.this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .loadVideoFile(loadVideoUrl, description, requestFileBody);
    }

    /**
     * 上传图片
     *
     * @return
     */
    private Observable<BaseData> loadImage() {
        RequestBody body = getImageRequestBody();

        return HttpUtils.getInstance(ReleaseConfActivity.this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .loadImage(loadImageUrl, body);
    }

    @NonNull
    private RequestBody getImageRequestBody() {
        JSONObject jsonObject = new JSONObject();
        try {
            for (int i = 0; i < selectListImage.size(); i++) {
                jsonObject.put("pic" + (i + 1), Base64Utils.bitmapToBase(selectListImage.get(i).getPath()));
            }
            jsonObject.put("accountId", accountBean.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return RequestBody.create(Urls.MEDIA_TYPE, jsonObject.toString());
    }

    /**
     * 上传主题教育
     *
     * @param imgUrls
     * @return
     */
    private Observable<BaseData> addEducsubj(List<String> imgUrls) {
        RequestBody educsubjRequestBody = getEducsubjRequestBody(imgUrls);

        return HttpUtils.getInstance(ReleaseConfActivity.this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .addEducsubj(
                        educsubjRequestBody
                );
    }


    /**
     * 获取主题教育请求体
     *
     * @return
     */
    private RequestBody getEducsubjRequestBody(List<String> imgUrls) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("theme", theme.replace("\n", "<br>"));
            jsonObject.put("content", content.replace("\n", "<br>"));
            jsonObject.put("eduDate", time);
            jsonObject.put("unitId", String.valueOf(accountBean.unitId));
            jsonObject.put("eductype", gzdtType);
//            jsonObject.put("staff", host.replace("\n", "<br>"));
//            jsonObject.put("site", address.replace("\n", "<br>"));
            jsonObject.put("videoUrl", videoUrl);
            jsonObject.put("videoThumbnailUrl", videoImageUrl);

            for (int i = 0; i < imgUrls.size(); i++) {
                jsonObject.put("pic" + (i + 1), imgUrls.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return RequestBody.create(Urls.MEDIA_TYPE, jsonObject.toString());
    }

    /**
     * 上传主题党日
     *
     * @param imgUrls
     * @return
     */
    private Observable<BaseData> addPartyDay(List<String> imgUrls) {
        RequestBody description = getRequestBody(imgUrls);

        return HttpUtils.getInstance(ReleaseConfActivity.this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .addThemeDay(requestUrl, description);
    }

    private Observable<BaseData> addVote(List<String> imgUrls) {

        RequestBody description = getVoteRequestBody(imgUrls);

        return HttpUtils.getInstance(ReleaseConfActivity.this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .addThemeDay(requestUrl, description);
    }

    /**
     * 添加换届信息
     *
     * @param imgUrls
     * @return
     */
    private Observable<BaseData> addDzzgh(List<String> imgUrls) {
        RequestBody dzzhjRequestBody = getDzzhjRequestBody(imgUrls);

        return HttpUtils.getInstance(ReleaseConfActivity.this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .addDzzhj(
                        requestUrl,
                        dzzhjRequestBody
                );
    }

    /**
     * 获取组织换届请求体
     *
     * @return
     */
    private RequestBody getDzzhjRequestBody(List<String> imgUrls) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("theme", theme.replace("\n", "<br>"));
            jsonObject.put("publicityTime", time);
            jsonObject.put("context", content.replace("\n", "<br>"));
            jsonObject.put("totalNum", Integer.valueOf(address));
            jsonObject.put("electNum", Integer.valueOf(host));
            jsonObject.put("unitId", accountBean.unitId);
            jsonObject.put("type", electionResultType);

            for (int i = 0; i < imgUrls.size(); i++) {
                jsonObject.put("pic" + (i + 1), imgUrls.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return RequestBody.create(Urls.MEDIA_TYPE, jsonObject.toString());
    }

    /**
     * 获取志愿服务请求体
     *
     * @return
     */
    private RequestBody getZyfwRequestBody(List<String> imgUrls) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("accountId", accountBean.id);
            jsonObject.put("title", theme.replace("\n", "<br>"));
            jsonObject.put("activityDate", time);
            jsonObject.put("content", content.replace("\n", "<br>"));
            jsonObject.put("videoUrl", videoUrl);
            jsonObject.put("videoThumbnailUrl", videoImageUrl);

            for (int i = 0; i < imgUrls.size(); i++) {
                jsonObject.put("pic" + (i + 1), imgUrls.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return RequestBody.create(Urls.MEDIA_TYPE, jsonObject.toString());
    }

    /**
     * accountId	账号ID	int	N	11
     * name	评比名称	String	N	128
     * department	组织部门	String	N	20
     * notice	评比须知	String	N	128
     * deadline	截止时间	String	N	64
     * candidate	候选人id集合
     * 示例：16,15,17	String	N	-
     * pic1	配图1	String	Y	256
     *
     * @param imgUrls
     * @return
     */
    private RequestBody getVoteRequestBody(List<String> imgUrls) {
        JSONObject jsonObject = new JSONObject();
        try {
            switch (title) {
                //三会一课
                case TYPE_SHYK:
                    jsonObject.put("type", confType);
                    break;
            }
            jsonObject.put("accountId", accountBean.id);
            jsonObject.put("name", theme.replace("\n", "<br>"));
            jsonObject.put("department", address.replace("\n", "<br>"));
            jsonObject.put("notice", content.replace("\n", "<br>"));
            jsonObject.put("deadline", time + ":00");
            jsonObject.put("candidate", attendUsers);
            jsonObject.put("candidateNames", attendNames);

            for (int i = 0; i < imgUrls.size(); i++) {
                jsonObject.put("pic" + (i + 1), imgUrls.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return RequestBody.create(Urls.MEDIA_TYPE, jsonObject.toString());
    }

    /**
     * 获取请求体
     *
     * @param imgUrls
     * @return
     */
    @NonNull
    private RequestBody getRequestBody(List<String> imgUrls) {
        JSONObject jsonObject = new JSONObject();
        try {
            switch (title) {
                //三会一课
                case TYPE_SHYK:
                    //主题党日
                case TYPE_ZTDR:
                    jsonObject.put("type", confType);
                    break;

                //民主评议
                case TYPE_MZPY:
                    jsonObject.put("type", eductype);
                    jsonObject.put("reviewedNum", reviewedNum);
                    break;

                //组织生活会
                case TYPE_ZZSHH:
                    jsonObject.put("type", eductype);
                    break;
            }

            jsonObject.put("accountId", accountBean.id);
            jsonObject.put("title", theme.replace("\n", "<br>"));
            jsonObject.put("activityDate", time);
            jsonObject.put("address", address.replace("\n", "<br>"));
            jsonObject.put("host", host.replace("\n", "<br>"));
            jsonObject.put("content", content.replace("\n", "<br>"));
            jsonObject.put("users", users);//应到人数
            jsonObject.put("attendUsers", attendUsers);//实到人数
            jsonObject.put("videoUrl", videoUrl);
            jsonObject.put("videoThumbnailUrl", videoImageUrl);

            for (int i = 0; i < imgUrls.size(); i++) {
                jsonObject.put("pic" + (i + 1), imgUrls.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return RequestBody.create(Urls.MEDIA_TYPE, jsonObject.toString());
    }

    /**
     * 获取图片路径
     *
     * @param index
     * @return
     */
    private String getImageUrl(List<String> urls, int index) {
        return index >= urls.size() ? "" : urls.get(index);
    }

    private int electionResultType = -1;//选举结果

    private String[] dzzElectionResultResults = {
            "按职数全额选出书记、委员",
            "已选出书记，未全额选出委员",
            "未选出书记，已全额选出委员",
            "未选出书记，未全额选出委员",
            "选举不成功"
    };

    private String[] cwhElectionResultResults = {
            "按职数全额选出主任、委员",
            "已选出主任，未全额选出委员",
            "未选出主任，已全额选出委员",
            "未选出主任，未全额选出委员",
            "选举不成功"
    };

    private MaterialDialog resultDialog;

    /**
     * 选举结果
     *
     * @param values
     * @param hint
     */
    private void showSelectDialog(String[] values, String hint) {
        resultDialog = new MaterialDialog.Builder(this)
                .title(hint)
                .items(values)
                .itemsCallbackSingleChoice(electionResultType - 1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        electionResultType = which + 1;
                        tvConfPersonnel.setText(values[which]);
                        return true;
                    }
                })
                .build();
        //点击对话框以外的地方，对话框不消失
//        resultDialog.setCanceledOnTouchOutside(false);
        //点击对话框以外的地方和返回键，对话框都不消失
//        dialog.setCancelable(false);
        resultDialog.show();
    }
}
