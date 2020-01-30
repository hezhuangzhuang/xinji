package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jaeger.library.StatusBarUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.zxwl.commonlibrary.utils.DialogUtils;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.RequestBody;
import rx.Observable;
import rx.functions.Func1;

/**
 * 添加图说本村
 */
public class AddTsbcActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvRightOperate;
    private ContainsEmojiEditText etTitle;

    private RecyclerView rvSelect;
    private SelectImageAdapter selectImageAdapter;
    private List<LocalMedia> selectListImage = new ArrayList<>();//选择的图片

    public static final String TYPE_JTJJ = "集体经济";
    public static final String TYPE_LDGZ = "亮点工作";
    public static final String TYPE_RYBZ = "荣誉表彰";
    public static final String TYPE_TSBC = "图说本村";

    public static final String TITLE = "TITLE";
    private String title;

    private String loadImageUrl;
    private String addRequestUrl;

    private String content;

    private LoginBean.AccountBean accountBean;

    public static void startActivity(Context context, String title) {
        Intent intent = new Intent(context, AddTsbcActivity.class);
        intent.putExtra(TITLE, title);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvRightOperate = (TextView) findViewById(R.id.tv_right_operate);
        etTitle = (ContainsEmojiEditText) findViewById(R.id.et_title);
        rvSelect = (RecyclerView) findViewById(R.id.rv_select);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        title = getIntent().getStringExtra(TITLE);
        tvTopTitle.setText(title);

        tvRightOperate.setText("发布");
        tvRightOperate.setVisibility(View.VISIBLE);

        accountBean = PreferenceUtil.getUserInfo(this);

        initUrl();

        initRecycler();
    }

    private void initUrl() {
        switch (title) {
            case TYPE_JTJJ:
                loadImageUrl = Urls.LOAD_IAMGE_JTJJ;
                addRequestUrl = Urls.ADD_JTJJ;
                break;

            case TYPE_LDGZ:
                loadImageUrl = Urls.LOAD_IAMGE_LDGZ;
                addRequestUrl = Urls.ADD_LDGZ;
                break;

            case TYPE_RYBZ:
                loadImageUrl = Urls.LOAD_IAMGE_RYBZ;
                addRequestUrl = Urls.ADD_RYBZ;
                break;

            case TYPE_TSBC:
                loadImageUrl = Urls.LOAD_IAMGE_TSBC;
                addRequestUrl = Urls.ADD_TSBC;
                break;
        }
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(this);
        tvRightOperate.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_tsbc;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_operate:
                finish();
                break;

            case R.id.tv_right_operate:
                content = etTitle.getText().toString().trim();

                if (TextUtils.isEmpty(content)) {
                    ToastHelper.showShort("标题不能为空");
                    return;
                }

                if (selectListImage.size() < 1) {
                    ToastHelper.showShort("图片不能为空");
                    return;
                }

                DialogUtils.showProgressDialog(AddTsbcActivity.this, "正在上传...");

                loadImage()
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
                        })
                        .compose(new CustomCompose())
                        .compose(bindToLifecycle())
                        .subscribe(new RxSubscriber<BaseData>() {
                            @Override
                            public void onSuccess(BaseData baseData) {
                                DialogUtils.dismissProgressDialog();
                                if (BaseData.SUCCESS.equals(baseData.result)) {
                                    ToastHelper.showShort("上传成功");
                                    if (TYPE_TSBC.equals(title)) {
                                        EventBus.getDefault().post(new EventMessage(Messages.REFRESH_MAP_MORE, ""));
                                    } else {
                                        EventBus.getDefault().post(new EventMessage(Messages.REFRESH_MAP_MORE, title));
                                    }
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
    }

    private void initRecycler() {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        rvSelect.setLayoutManager(manager);
        selectImageAdapter = new SelectImageAdapter(R.layout.gv_filter_image, new ArrayList<>(), onAddPicClickListener);
        selectImageAdapter.setSelectMax(3);

        selectImageAdapter.setOnDelClickListener(new SelectImageAdapter.OnDelClickListener() {
            @Override
            public void onItemClick(LocalMedia localMedia) {
                String pictureType = localMedia.getPictureType();

                int mediaType = PictureMimeType.pictureToVideo(pictureType);
                if (PictureConfig.TYPE_IMAGE == mediaType) {
                    selectListImage.remove(localMedia);
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
                            PictureSelector.create(AddTsbcActivity.this).themeStyle(R.style.picture_default_style).openExternalPreview(position, selectListImage);
                            break;

                        case PictureConfig.TYPE_VIDEO:
                            // 预览视频
                            PictureSelector.create(AddTsbcActivity.this).externalPictureVideo(media.getPath());
                            break;

                        case 3:
                            // 预览音频
                            PictureSelector.create(AddTsbcActivity.this).externalPictureAudio(media.getPath());
                            break;
                    }
                }
            }
        });
        rvSelect.setAdapter(selectImageAdapter);
    }

    private SelectImageAdapter.onAddPicClickListener onAddPicClickListener = new SelectImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            startSelectAct();
        }
    };

    private void startSelectAct() {
        int maxSelect = 3;
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
//               .theme(themeId)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
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
                .selectionMedia(selectListImage)//是否传入已选图片
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
                    selectListImage.clear();
                    selectListImage.addAll(tempSelectLocalMedia);
                    selectImageAdapter.replaceData(tempSelectLocalMedia);
                    break;
            }
        }
    }

    /**
     * 上传图片
     *
     * @return
     */
    private Observable<BaseData> loadImage() {
        RequestBody body = getImageRequestBody();

        return HttpUtils.getInstance(this)
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
//            jsonObject.put("accountId", accountBean.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return RequestBody.create(Urls.MEDIA_TYPE, jsonObject.toString());
    }

    /**
     * 上传说本村
     *
     * @param imgUrls
     * @return
     */
    private Observable<BaseData> getAddRequest(List<String> imgUrls) {
        RequestBody educsubjRequestBody = getAddRequestBody(imgUrls);

        return HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .addTsbc(
                        addRequestUrl,
                        educsubjRequestBody
                );
    }

    private RequestBody getAddRequestBody(List<String> imgUrls) {
        JSONObject jsonObject = new JSONObject();
        try {
//            jsonObject.put("title", content.replace("\n", "<br>"));
            jsonObject.put("title", content);
            jsonObject.put("unitId", String.valueOf(accountBean.unitId));

            for (int i = 0; i < imgUrls.size(); i++) {
                jsonObject.put("pic" + (i + 1), imgUrls.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return RequestBody.create(Urls.MEDIA_TYPE, jsonObject.toString());
    }

}
