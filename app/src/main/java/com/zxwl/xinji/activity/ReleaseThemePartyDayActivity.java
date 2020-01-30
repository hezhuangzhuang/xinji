package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.RetryWithDelay;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.FullyGridLayoutManager;
import com.zxwl.xinji.adapter.SelectImageAdapter;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.utils.Base64Utils;
import com.zxwl.xinji.utils.PreferenceUtil;
import com.zxwl.xinji.widget.ContainsEmojiEditText;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.RequestBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 发布主题党日
 */
public class ReleaseThemePartyDayActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvRightOperate;
    private ContainsEmojiEditText etTitle;
    private ContainsEmojiEditText etContent;

    private LinearLayout llEndTime;
    private TextView tvEndTime;

    private LinearLayout llStartTime;
    private TextView tvStartTime;
    private TextView tvStartLable;
    private TextView tvEndLable;

    private ContainsEmojiEditText etMakeOne;
    private ContainsEmojiEditText etMakeTwo;
    private ContainsEmojiEditText etName;

    private RecyclerView rvSelect;
    private SelectImageAdapter selectImageAdapter;

    //选择的图片
    private List<LocalMedia> selectListImage = new ArrayList<>();

    //选择的视频
    private List<LocalMedia> selectListVideo = new ArrayList<>();

    private LocalMedia selectVideo;//选择的视频

    public static final String TYPE_WXY = "微心愿";

    public static final String TYPE_DWGK = "党务村务公开";

    public static final String TYPE_CWGK = "村务公开";

    public static final String TYPE_BFHDZS = "帮扶活动展示";

    //标题
    public static final String TITLE = "TITLE";

    //标题
    private String title;

    private LoginBean.AccountBean accountBean;

    //选开始时间
    private boolean selectStartTime = false;

    private String theme;
    private String content;

    private String loadImageUrl;//上传图片路径

    public static void startActivity(Context context, String title) {
        Intent intent = new Intent(context, ReleaseThemePartyDayActivity.class);
        intent.putExtra(TITLE, title);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvRightOperate = (TextView) findViewById(R.id.tv_right_operate);

        rvSelect = (RecyclerView) findViewById(R.id.rv_select);

        etTitle = (ContainsEmojiEditText) findViewById(R.id.et_title);
        etContent = (ContainsEmojiEditText) findViewById(R.id.et_content);
        llEndTime = (LinearLayout) findViewById(R.id.ll_time);
        tvEndTime = (TextView) findViewById(R.id.tv_conf_time);
        llStartTime = (LinearLayout) findViewById(R.id.ll_start_time);
        tvStartTime = (TextView) findViewById(R.id.tv_start_time);
        tvStartLable = (TextView) findViewById(R.id.tv_start_lable);
        tvEndLable = (TextView) findViewById(R.id.tv_end_lable);
        etMakeOne = (ContainsEmojiEditText) findViewById(R.id.et_make_one);
        etMakeTwo = (ContainsEmojiEditText) findViewById(R.id.et_make_two);
        etName = (ContainsEmojiEditText) findViewById(R.id.et_name);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        accountBean = PreferenceUtil.getUserInfo(this);

        title = getIntent().getStringExtra(TITLE);

        time = DateUtil.getCurrentTime(DateUtil.FORMAT_DATE);

        tvRightOperate.setText("发布");
        tvRightOperate.setVisibility(View.VISIBLE);

        tvTopTitle.setText(title);

        initView();

        tvStartTime.setText(DateUtil.getCurrentTime(DateUtil.FORMAT_DATE));
        tvEndTime.setText(DateUtil.getCurrentTime(DateUtil.FORMAT_DATE));
    }

    private void initView() {
        switch (title) {
            case TYPE_CWGK:
            case TYPE_DWGK:
                loadImageUrl = Urls.LOAD_IAMGE_ZTJY;
                initRecycler();

                tvStartLable.setText("时间");
                tvEndLable.setText("公示期限");

                //设置可以输入任意字符
                //手动设置maxLength为2000
//                InputFilter[] filters = {new InputFilter.LengthFilter(500)};
//                etContent.setFilters(filters);

                etMakeOne.setVisibility(View.VISIBLE);
                etMakeTwo.setVisibility(View.VISIBLE);
                break;

            case TYPE_BFHDZS:
                loadImageUrl = Urls.LOAD_IAMGE_ZYFW;
                llEndTime.setVisibility(View.GONE);
                initRecycler();
                break;

            case TYPE_WXY:
                etName.setVisibility(View.VISIBLE);
                llEndTime.setVisibility(View.GONE);

                tvStartLable.setText("时间");
                etTitle.setHint("心愿");
                etContent.setHint("具体诉求");
                break;

        }
    }

    /**
     * 初始化recycler
     */
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
                            PictureSelector.create(ReleaseThemePartyDayActivity.this).themeStyle(R.style.picture_default_style).openExternalPreview(position, selectListImage);
                            break;

                        case PictureConfig.TYPE_VIDEO:
                            // 预览视频
                            PictureSelector.create(ReleaseThemePartyDayActivity.this).externalPictureVideo(media.getPath());
                            break;

                        case 3:
                            // 预览音频
                            PictureSelector.create(ReleaseThemePartyDayActivity.this).externalPictureAudio(media.getPath());
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
        tvEndTime.setOnClickListener(this);
        tvStartTime.setOnClickListener(this);

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
        return R.layout.activity_release_theme_party_day;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_operate:
                finish();
                break;

            case R.id.tv_right_operate:
                switch (title) {
                    //村务公开
                    case TYPE_CWGK:
                        if (checkText()) {
                            return;
                        }
                        addCwgkRequest(Urls.ADD_PARTYAFF_LIST, "2");
                        break;

                    //党务公开
                    case TYPE_DWGK:
                        if (checkText()) {
                            return;
                        }
                        addCwgkRequest(Urls.ADD_PARTYAFF_LIST, "1");
                        break;

                    //帮扶活动展示
                    case TYPE_BFHDZS:
                        if (checkText()) {
                            return;
                        }
                        addBfhdzsRequest(Urls.ADD_ZYFW);
                        break;
                }
                break;

            //选结束时间
            case R.id.tv_conf_time:
                selectStartTime = false;

                KeyBoardUtil.closeKeybord(etTitle, getApplication());
                initLunarPicker();
                pvCustomLunar.show();
                break;

            //选开始时间1
            case R.id.tv_start_time:
                selectStartTime = true;

                KeyBoardUtil.closeKeybord(etTitle, getApplication());
                initLunarPicker();
                pvCustomLunar.show();
                break;
        }
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

                    //选择的视频
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

    /**
     * 获取图片路径
     *
     * @param index
     * @return
     */
    private String getImageUrl(List<String> urls, int index) {
        return index >= urls.size() ? "" : urls.get(index);
    }

    /**
     * 判断文本是否有输入
     *
     * @return
     */
    private boolean checkText() {
        theme = etTitle.getText().toString().trim();
        if (TextUtils.isEmpty(theme)) {
            ToastHelper.showShort("标题不能为空");
            return true;
        }

        content = etContent.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastHelper.showShort("内容不能为空");
            return true;
        }
        return false;
    }

    private MaterialDialog dialog;

    private void showSelectDialog(String[] values, String hint) {
        dialog = new MaterialDialog.Builder(this)
                .title(hint)
                .items(values)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (hint) {
                            case "请选择脱贫属性":
                                break;

                            case "请选择帮扶人性别":
                                break;
                        }
                        return true;
                    }
                })
                .build();
        //点击对话框以外的地方，对话框不消失
//        dialog.setCanceledOnTouchOutside(false);
        //点击对话框以外的地方和返回键，对话框都不消失
        //dialog.setCancelable(false);
        dialog.show();
    }

    private SelectImageAdapter.onAddPicClickListener onAddPicClickListener = new SelectImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            switch (title) {
                case TYPE_CWGK:
                case TYPE_DWGK:
                case TYPE_BFHDZS:
                    startSelectAct(1);
                    break;
            }
        }
    };

    private void startSelectAct(int type) {
        int imageCount = selectListVideo.size() >= 1 ? 8 : 9;

        PictureSelector.create(ReleaseThemePartyDayActivity.this)
                .openGallery(type)// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
//              .theme(themeId)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                .maxSelectNum(type == PictureMimeType.ofImage() ? imageCount : 1)// 最大图片选择数量
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

    private TimePickerView pvCustomLunar;
    private String time;

    /**
     * 农历时间已扩展至 ： 1900 - 2100年
     */
    private void initLunarPicker() {
        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        selectedDate.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH), selectedDate.get(Calendar.HOUR_OF_DAY), selectedDate.get(Calendar.MINUTE));

        Calendar startDate = Calendar.getInstance();
        startDate.set(1900, 1, 23);

        Calendar endDate = Calendar.getInstance();
        endDate.set(2100, 1, 23);

        //时间选择器 ，自定义布局
        pvCustomLunar = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                time = getTime(date);
                if (selectStartTime) {
                    tvStartTime.setText(time);
                } else {
                    tvEndTime.setText(time);
                }
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
                .setType(new boolean[]{true, true, true, false, false, false})
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(Color.RED)
                .build();
    }

    //可根据需要自行截取数据显示
    private String getTime(Date date) {
        Log.d("getTime()", "choice date millis: " + date.getTime());
        SimpleDateFormat format = new SimpleDateFormat(DateUtil.FORMAT_DATE);
        return format.format(date);
    }

    /**
     * 添加帮扶活动展示
     */
    private void addBfhdzsRequest(String url) {
        //判断结束时间是否符合
        String endTime = tvEndTime.getText().toString().trim();
        long endTimeLong = DateUtil.stringToLong(endTime, DateUtil.FORMAT_DATE);

        String startTime = tvStartTime.getText().toString().trim();
        long startTimeLong = DateUtil.stringToLong(startTime, DateUtil.FORMAT_DATE);

        long currentTime = DateUtil.stringToLong(DateUtil.getCurrentTime(DateUtil.FORMAT_DATE), DateUtil.FORMAT_DATE);

        if (startTimeLong - currentTime < 0) {
            ToastHelper.showShort("开始时间必须大于当前时间");
            return;
        }

        DialogUtils.showProgressDialog(this, "正在上传...");

        JSONObject jsonObject = new JSONObject();
        try {
            for (int i = 0; i < selectListImage.size(); i++) {
                jsonObject.put("pic" + (i + 1), Base64Utils.bitmapToBase(selectListImage.get(i).getPath()));
            }

            jsonObject.put("accountId", accountBean.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(Urls.MEDIA_TYPE, jsonObject.toString());

        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .loadImage(loadImageUrl, body)
                .flatMap(new Func1<BaseData, Observable<BaseData>>() {
                    @Override
                    public Observable<BaseData> call(BaseData stringBaseData) {
                        if (BaseData.SUCCESS.equals(stringBaseData.result)) {
                            String[] urls = stringBaseData.data.split(",");
                            List<String> strings = Arrays.asList(urls);
                            return HttpUtils
                                    .getInstance(ReleaseThemePartyDayActivity.this)
                                    .getRetofitClinet()
                                    .setBaseUrl(Urls.BASE_URL)
                                    .builder(StudyApi.class)
                                    .addThemeDay(
                                            url,
                                            getRequestBody(strings)
                                    );
                        } else {
                            return null;
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new RetryWithDelay(3, 300))
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            ToastHelper.showShort(baseData.message);
                            EventBus.getDefault().post(new EventMessage(Messages.REFRESH_RECYCLER, ""));
                            finish();
                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                        DialogUtils.dismissProgressDialog();
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        DialogUtils.dismissProgressDialog();
                        ToastHelper.showShort("onError:" + responeThrowable.getCause().getMessage());
                    }
                });
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
            jsonObject.put("accountId", accountBean.id);
            jsonObject.put("title", theme.replaceAll("\n", "<br>"));
            jsonObject.put("activityDate", time);
            jsonObject.put("content", content.replaceAll("\n", "<br>"));

            for (int i = 0; i < imgUrls.size(); i++) {
                jsonObject.put("pic" + (i + 1), imgUrls.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return RequestBody.create(Urls.MEDIA_TYPE, jsonObject.toString());
    }

    /**
     * 添加村务公开
     * type:1党务，2,村务
     */
    private void addCwgkRequest(String url, String type) {
        //判断结束时间是否符合
        String endTime = tvEndTime.getText().toString().trim();
        long endTimeLong = DateUtil.stringToLong(endTime, DateUtil.FORMAT_DATE);

        String startTime = tvStartTime.getText().toString().trim();
        long startTimeLong = DateUtil.stringToLong(startTime, DateUtil.FORMAT_DATE);

        long currentTime = DateUtil.stringToLong(DateUtil.getCurrentTime(DateUtil.FORMAT_DATE), DateUtil.FORMAT_DATE);

        if (startTimeLong - currentTime > 0) {
            ToastHelper.showShort("开始时间不能大于当前时间");
            return;
        }

        if (endTimeLong - currentTime < 0) {
            ToastHelper.showShort("公示期限必须大于开始时间");
            return;
        }

        DialogUtils.showProgressDialog(this, "正在上传...");

        JSONObject jsonObject = new JSONObject();
        try {
            for (int i = 0; i < selectListImage.size(); i++) {
                jsonObject.put("pic" + (i + 1), Base64Utils.bitmapToBase(selectListImage.get(i).getPath()));
            }
            jsonObject.put("accountId", accountBean.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(Urls.MEDIA_TYPE, jsonObject.toString());

        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .loadImage(Urls.LOAD_IAMGE_ZTJY, body)
                .flatMap(new Func1<BaseData, Observable<BaseData>>() {
                    @Override
                    public Observable<BaseData> call(BaseData stringBaseData) {
                        if (BaseData.SUCCESS.equals(stringBaseData.result)) {
                            List<String> imageUrls = Arrays.asList(stringBaseData.data.split(","));
                            RequestBody cwgkRequestBody = getCwgkRequestBody(imageUrls, startTime, endTime, type);
                            return HttpUtils
                                    .getInstance(ReleaseThemePartyDayActivity.this)
                                    .getRetofitClinet()
                                    .setBaseUrl(Urls.BASE_URL)
                                    .builder(StudyApi.class)
                                    .addHamletaffList(
                                            url,
                                            cwgkRequestBody
                                    );
                        } else {
                            return null;
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new RetryWithDelay(3, 300))
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            ToastHelper.showShort(baseData.message);
                            EventBus.getDefault().post(new EventMessage(Messages.REFRESH_RECYCLER, ""));
                            finish();
                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                        DialogUtils.dismissProgressDialog();
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        DialogUtils.dismissProgressDialog();
                        ToastHelper.showShort("onError:" + responeThrowable.getCause().getMessage());
                    }
                });
    }


    /**
     * 获取请求体
     *
     * @param imgUrls
     * @return
     */
    @NonNull
    private RequestBody getCwgkRequestBody(List<String> imgUrls, String endTime, String startTime, String type) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("theme", theme.replaceAll("\n", "<br>"));
            jsonObject.put("context", content.replaceAll("\n", "<br>"));
            jsonObject.put("outtime", startTime);
            jsonObject.put("unitId", String.valueOf(accountBean.unitId));
            jsonObject.put("publicityTime", endTime);
            jsonObject.put("remark1", etMakeOne.getText().toString().trim().replaceAll("\n", "<br>"));
            jsonObject.put("remark2", etMakeTwo.getText().toString().trim().replaceAll("\n", "<br>"));
            jsonObject.put("type", type);

            for (int i = 0; i < imgUrls.size(); i++) {
                jsonObject.put("pic" + (i + 1), imgUrls.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return RequestBody.create(Urls.MEDIA_TYPE, jsonObject.toString());
    }
}
