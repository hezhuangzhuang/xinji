package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.zxwl.network.bean.response.DepartmentDetailsBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.RetryWithDelay;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.FullyGridLayoutManager;
import com.zxwl.xinji.adapter.SelectImageAdapter;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.fragment.MapBasicFragment;
import com.zxwl.xinji.utils.Base64Utils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.RequestBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 编辑单位信息
 */
public class EditUnitInfoActivity extends BaseActivity {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvRightOperate;
    private EditText etBasicProfile;

    private TextView tvBasicProfileLable;
    private RecyclerView rvSelect;
    private SelectImageAdapter selectImageAdapter;
    private List<LocalMedia> selectListImage;//选择的图片

    private DepartmentDetailsBean.DataBean departmentDetailsBean;

    public static final String INFO_BEAN = "INFO_BEAN";
    public static final String IS_BASIC = "IS_BASIC";
    public static final String TITLE = "TYPE_TITLE";

    private String baseInfo;

    private String loadImageUrl = Urls.LOAD_IAMGE_UNIT_INFO;

    private String requestUrl = Urls.UPDATE_UNIT_INFO;

    /**
     * 请求接口传递的参数
     */
    private List<String> httpUrls = new ArrayList<>();

    //是否是基本信息
    private boolean isBasic = false;
    private String title;

    public static void startActivity(Context context, DepartmentDetailsBean.DataBean departmentDetailsBean, boolean isBasic) {
        Intent intent = new Intent(context, EditUnitInfoActivity.class);
        intent.putExtra(INFO_BEAN, departmentDetailsBean);
        intent.putExtra(IS_BASIC, isBasic);
        context.startActivity(intent);
    }

    public static void startActivity(Context context,
                                     DepartmentDetailsBean.DataBean departmentDetailsBean, String title) {
        Intent intent = new Intent(context, EditUnitInfoActivity.class);
        intent.putExtra(INFO_BEAN, departmentDetailsBean);
        intent.putExtra(TITLE, title);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvRightOperate = (TextView) findViewById(R.id.tv_right_operate);
        etBasicProfile = (EditText) findViewById(R.id.tv_basic_profile);
        rvSelect = (RecyclerView) findViewById(R.id.rv_select);

        tvBasicProfileLable = (TextView) findViewById(R.id.tv_basic_profile_lable);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        isBasic = getIntent().getBooleanExtra(IS_BASIC, false);
        title = getIntent().getStringExtra(TITLE);

        tvRightOperate.setText("保存");
        tvRightOperate.setVisibility(View.VISIBLE);

        tvTopTitle.setText("编辑" + title);

        tvBasicProfileLable.setText(title);
        etBasicProfile.setHint("请输入" + title);

        departmentDetailsBean = (DepartmentDetailsBean.DataBean) getIntent().getSerializableExtra(INFO_BEAN);

        setInfo();

        setSelection(etBasicProfile);

        initAdapter();
    }

    private void setInfo() {
        if (MapBasicFragment.TYPE_JTJJ.equals(title)) {
            requestUrl = Urls.ADD_JTJJ;
            loadImageUrl = Urls.LOAD_IAMGE_JTJJ;
        }
        if (!isUpdate()) {
            etBasicProfile.setText("");
            selectListImage = new ArrayList<>();
            return;
        }
        switch (title) {
            //村情概况
            case MapBasicFragment
                    .TYPE_CQGK:
                loadImageUrl = Urls.LOAD_IAMGE_UNIT_INFO;
                requestUrl = Urls.UPDATE_UNIT_INFO;

                tvBasicProfileLable.setText(title);
                etBasicProfile.setText(departmentDetailsBean.baseInfo);

                getImageList(Arrays.asList(
                        departmentDetailsBean.pic1,
                        departmentDetailsBean.pic2,
                        departmentDetailsBean.pic3,

                        departmentDetailsBean.pic4,
                        departmentDetailsBean.pic5,
                        departmentDetailsBean.pic6,

                        departmentDetailsBean.pic7,
                        departmentDetailsBean.pic8,
                        departmentDetailsBean.pic9
                ));
                break;

            //党建阵地
            case MapBasicFragment
                    .TYPE_DJZD:
                loadImageUrl = Urls.LOAD_IAMGE_UNIT_INFO;
                requestUrl = Urls.UPDATE_UNIT_INFO;

                etBasicProfile.setText(departmentDetailsBean.unitInfo);
                getImageList(Arrays.asList(
                        departmentDetailsBean.pic11,
                        departmentDetailsBean.pic22,
                        departmentDetailsBean.pic33,

                        departmentDetailsBean.pic44,
                        departmentDetailsBean.pic55,
                        departmentDetailsBean.pic66,

                        departmentDetailsBean.pic77,
                        departmentDetailsBean.pic88,
                        departmentDetailsBean.pic99
                ));
                break;

            //集体经济
            case MapBasicFragment
                    .TYPE_JTJJ:
                loadImageUrl = Urls.LOAD_IAMGE_JTJJ;
                requestUrl = Urls.ADD_JTJJ;

                tvBasicProfileLable.setText(title);
                etBasicProfile.setText(departmentDetailsBean.title);

                getImageList(Arrays.asList(
                        departmentDetailsBean.pic1,
                        departmentDetailsBean.pic2,
                        departmentDetailsBean.pic3,

                        departmentDetailsBean.pic4,
                        departmentDetailsBean.pic5,
                        departmentDetailsBean.pic6,

                        departmentDetailsBean.pic7,
                        departmentDetailsBean.pic8,
                        departmentDetailsBean.pic9
                ));
                break;
        }
    }

    /**
     * 是否是更新
     *
     * @return
     */
    private boolean isUpdate() {
        return 0 != departmentDetailsBean.id;
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvRightOperate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseInfo = etBasicProfile.getText().toString().trim();
                if (TextUtils.isEmpty(baseInfo)) {
                    ToastHelper.showShort("请输入" + title);
                    return;
                }
                DialogUtils.showProgressDialog(EditUnitInfoActivity.this, "正在上传...");
                addDeptInfoRequest();
            }
        });
    }

    /**
     * 添加单位信息
     */
    private void addDeptInfoRequest() {
        JSONObject jsonObject = new JSONObject();

        try {
            for (int i = 0; i < selectListImage.size(); i++) {
                //如果不包含http前缀代表是本地图片
                if (!selectListImage.get(i).getPath().contains("http://")) {
                    String s = Base64Utils.bitmapToBase(selectListImage.get(i).getPath());
                    Log.i("Base64Utils", s);
                    jsonObject.put("pic" + (i + 1), s);
                } else {
                    httpUrls.add(selectListImage.get(i).getPath());
                }
            }
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
                            httpUrls.addAll(strings);

                            return HttpUtils
                                    .getInstance(EditUnitInfoActivity.this)
                                    .getRetofitClinet()
                                    .setBaseUrl(Urls.BASE_URL)
                                    .builder(StudyApi.class)
                                    .updateUnitInfo(
                                            requestUrl,
                                            getRequestBody(httpUrls)
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
                        DialogUtils.dismissProgressDialog();

                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            ToastHelper.showShort(baseData.message);

                            switch (title) {
                                case MapBasicFragment.TYPE_DJZD:
                                    departmentDetailsBean.unitInfo = baseInfo;
                                    break;

                                case MapBasicFragment.TYPE_CQGK:
                                    departmentDetailsBean.baseInfo = baseInfo;
                                    break;

                                case MapBasicFragment.TYPE_JTJJ:
                                    departmentDetailsBean.title = baseInfo;
                                    break;
                            }

                            //如果是党建这你
                            if (MapBasicFragment.TYPE_DJZD.equals(title)) {
                                departmentDetailsBean.pic11 = getUrl(0, httpUrls);
                                departmentDetailsBean.pic22 = getUrl(1, httpUrls);
                                departmentDetailsBean.pic33 = getUrl(2, httpUrls);
                                departmentDetailsBean.pic44 = getUrl(3, httpUrls);
                                departmentDetailsBean.pic55 = getUrl(4, httpUrls);
                                departmentDetailsBean.pic66 = getUrl(5, httpUrls);
                                departmentDetailsBean.pic77 = getUrl(6, httpUrls);
                                departmentDetailsBean.pic88 = getUrl(7, httpUrls);
                                departmentDetailsBean.pic99 = getUrl(8, httpUrls);
                            } else {
                                departmentDetailsBean.pic1 = getUrl(0, httpUrls);
                                departmentDetailsBean.pic2 = getUrl(1, httpUrls);
                                departmentDetailsBean.pic3 = getUrl(2, httpUrls);
                                departmentDetailsBean.pic4 = getUrl(3, httpUrls);
                                departmentDetailsBean.pic5 = getUrl(4, httpUrls);
                                departmentDetailsBean.pic6 = getUrl(5, httpUrls);
                                departmentDetailsBean.pic7 = getUrl(6, httpUrls);
                                departmentDetailsBean.pic8 = getUrl(7, httpUrls);
                                departmentDetailsBean.pic9 = getUrl(8, httpUrls);
                            }

                            if (Urls.UPDATE_UNIT_INFO.equals(requestUrl)) {
                                departmentDetailsBean.id = departmentDetailsBean.id;
                            } else {
                                departmentDetailsBean.id = Integer.valueOf(baseData.id);
                            }

                            EventBus.getDefault().post(new EventMessage(Messages.UPDATE_UNIT, departmentDetailsBean));
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
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_unit_info;
    }

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

                    selectListImage = tempSelectLocalMedia;
                    newList.addAll(selectListImage);
                    selectImageAdapter.replaceData(newList);
                    selectImageAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    /**
     * 获取图片列表
     */
    private void getImageList(List<String> imageUrls) {
        selectListImage = new ArrayList<>();
        LocalMedia localMedia = null;
        for (String url : imageUrls) {
            if (!TextUtils.isEmpty(url)) {
                localMedia = new LocalMedia();
                localMedia.setPath(url);
                selectListImage.add(localMedia);
            }
        }
    }

    private void initAdapter() {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        rvSelect.setLayoutManager(manager);
        selectImageAdapter = new SelectImageAdapter(R.layout.gv_filter_image, selectListImage, onAddPicClickListener);
        selectImageAdapter.setSelectMax(9);

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
                            PictureSelector.create(EditUnitInfoActivity.this).themeStyle(R.style.picture_default_style).openExternalPreview(position, selectListImage);
                            break;

                        // 预览视频
                        case PictureConfig.TYPE_VIDEO:
                            PictureSelector.create(EditUnitInfoActivity.this).externalPictureVideo(media.getPath());
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
            startSelectAct(PictureMimeType.ofImage());
        }
    };

    private void startSelectAct(int type) {
        int maxSelect = 9;
        PictureSelector.create(this)
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


    /**
     * 获取对应下标的图片
     *
     * @param i
     * @param imageUrls
     * @return
     */
    private String getUrl(int i, List<String> imageUrls) {
        if (i < imageUrls.size()) {
            return imageUrls.get(i);
        } else {
            return "";
        }
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
            if (Urls.UPDATE_UNIT_INFO.equals(requestUrl)) {
                jsonObject.put("id", departmentDetailsBean.id);
            }
            jsonObject.put("departmentId", departmentDetailsBean.departmentId);

            switch (title) {
                case MapBasicFragment.TYPE_CQGK:
                    jsonObject.put("baseInfo", baseInfo);
                    jsonObject.put("type", 1);
                    break;

                case MapBasicFragment.TYPE_DJZD:
                    jsonObject.put("unitInfo", baseInfo);
                    jsonObject.put("type", 2);
                    break;

                case MapBasicFragment.TYPE_JTJJ:
                    jsonObject.put("unitId", departmentDetailsBean.departmentId);
                    jsonObject.put("title", baseInfo);
                    break;
            }

            for (int i = 0; i < imgUrls.size(); i++) {
                jsonObject.put("pic" + (i + 1), imgUrls.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return RequestBody.create(Urls.MEDIA_TYPE, jsonObject.toString());
    }


    private void setSelection(EditText editText) {
        //设置光标位置
        CharSequence text = editText.getText();
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
    }
}
