package com.zxwl.xinji.fragment;


import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.zxwl.commonlibrary.BaseLazyFragment;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.bean.response.DepartmentDetailsBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.activity.EditUnitInfoActivity;
import com.zxwl.xinji.adapter.FullyGridLayoutManager;
import com.zxwl.xinji.adapter.ShowImageAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 单位简介
 */
public class UnitProfileFragment extends BaseLazyFragment implements View.OnClickListener {
    private TextView tvBasicProfileLable;
    private TextView tvBasicProfile;

    private TextView tvPartyPositionLable;
    private TextView tvPartyPosition;
    private RecyclerView rvList;

    private FrameLayout flEdit;
    private TextView tvEditLable;

    //单位id
    public static final String UNIT_ID = "UNIT_ID";

    //是否显示编辑按钮
    public static final String SHOW_UNIT = "SHOW_UNIT";

    //是否是基本简介
    public static final String IS_BASIC = "IS_BASIC";

    //单位id
    private int unitId;

    //是否显示编辑按钮
    private boolean showUnit = false;

    private ShowImageAdapter selectImageAdapter;
    private List<LocalMedia> selectListImage;

    private DepartmentDetailsBean.DataBean dataBean;

    public static final String TYPE_JBJJ = "单位简介";

    public static final String TYPE_DJZD = "党建阵地";

    //是否是基本简介，
    private boolean isBasic = false;

    public UnitProfileFragment() {
    }

    public static UnitProfileFragment newInstance(int unitId, boolean showUnit, boolean isBasic) {
        UnitProfileFragment fragment = new UnitProfileFragment();
        Bundle args = new Bundle();
        args.putInt(UNIT_ID, unitId);
        args.putBoolean(SHOW_UNIT, showUnit);
        args.putBoolean(IS_BASIC, isBasic);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View inflateContentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_unit_profile, container, false);
    }

    @Override
    protected void findViews(View view) {
        tvBasicProfileLable = (TextView) view.findViewById(R.id.tv_basic_profile_lable);
        tvBasicProfile = (TextView) view.findViewById(R.id.tv_basic_profile);
        flEdit = (FrameLayout) view.findViewById(R.id.fl_edit);

        tvEditLable = (TextView) view.findViewById(R.id.tv_edit_lable);

        tvPartyPositionLable = (TextView) view.findViewById(R.id.tv_party_position_lable);
        tvPartyPosition = (TextView) view.findViewById(R.id.tv_party_position);
        rvList = (RecyclerView) view.findViewById(R.id.rv_select);
    }

    @Override
    protected void addListeners() {
        flEdit.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        unitId = getArguments().getInt(UNIT_ID);
        showUnit = getArguments().getBoolean(SHOW_UNIT, false);
        isBasic = getArguments().getBoolean(IS_BASIC, false);

        //如果是基本简介
        if (isBasic) {
            tvEditLable.setText("编辑基本简介");
            tvPartyPositionLable.setVisibility(View.GONE);
            tvPartyPosition.setVisibility(View.GONE);
            rvList.setVisibility(View.GONE);
        } else {
            tvEditLable.setText("编辑党建阵地");
            tvBasicProfileLable.setVisibility(View.GONE);
            tvBasicProfile.setVisibility(View.GONE);
            initRecycler();
        }

        queryUnitDetail();

        register();
    }

    /**
     * 查询详情
     */
    private void queryUnitDetail() {
        HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryDepartmentDetail("",unitId)
                .compose(this.bindToLifecycle())
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<DepartmentDetailsBean>() {
                    @Override
                    public void onSuccess(DepartmentDetailsBean detailsBean) {
                        if (BaseData.SUCCESS.equals(detailsBean.result)) {
                            DepartmentDetailsBean.DataBean newData = detailsBean.data;
                            if (null != newData) {
                                dataBean = newData;

                                setUnitInfo(newData);

                                if (showUnit) {
                                    flEdit.setVisibility(View.VISIBLE);
                                } else {
                                    flEdit.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            ToastHelper.showShort(detailsBean.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    public void setUnitInfo(DepartmentDetailsBean.DataBean newData) {
        if (isBasic) {
            tvBasicProfile.setText(newData.baseInfo);
        } else {
            tvPartyPosition.setText(newData.unitInfo);

            getImageList(Arrays.asList(
                    newData.pic1,
                    newData.pic2,
                    newData.pic3,
                    newData.pic4,
                    newData.pic5,
                    newData.pic6,
                    newData.pic7,
                    newData.pic8,
                    newData.pic9
            ));
            selectImageAdapter.replaceData(selectListImage);
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

    private void loadImage(RoundedImageView iamge, String url) {
        if (TextUtils.isEmpty(url)) {
            iamge.setVisibility(View.INVISIBLE
            );
        } else {
            iamge.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .asBitmap()
                    .load(url)
                    .into(iamge);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_one:
                PictureSelector.create(getActivity()).themeStyle(R.style.picture_default_style).openExternalPreview(0, selectListImage);
                break;

            case R.id.iv_two:
                PictureSelector.create(getActivity()).themeStyle(R.style.picture_default_style).openExternalPreview(1, selectListImage);
                break;

            case R.id.iv_three:
                PictureSelector.create(getActivity()).themeStyle(R.style.picture_default_style).openExternalPreview(2, selectListImage);
                break;

            case R.id.fl_edit:
                if (null != dataBean) {
                    dataBean.departmentId = unitId;
                    EditUnitInfoActivity.startActivity(getActivity(), dataBean, isBasic);
                }
                break;
        }
    }

    /**
     * 初始化reycler
     */
    private void initRecycler() {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
        rvList.setLayoutManager(manager);
        selectImageAdapter = new ShowImageAdapter(R.layout.gv_filter_image, new ArrayList<>());

        selectImageAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (selectImageAdapter.getData().size() > 0) {
                    LocalMedia media = (LocalMedia) selectImageAdapter.getItem(position);

                    int mediaType = PictureMimeType.pictureToVideo(media.getPictureType());

                    //判断类型
                    switch (mediaType) {
                        case PictureConfig.TYPE_IMAGE:
                            // 预览图片 可自定长按保存路径
                            PictureSelector.create(getActivity()).themeStyle(R.style.picture_default_style).openExternalPreview(position, selectListImage);
                            break;

                        case PictureConfig.TYPE_VIDEO:
                            // 预览视频
                            PictureSelector.create(getActivity()).externalPictureVideo(media.getPath());
                            break;

                        case 3:
                            // 预览音频
                            PictureSelector.create(getActivity()).externalPictureAudio(media.getPath());
                            break;
                    }
                }
            }
        });
        rvList.setAdapter(selectImageAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unRegister();
    }

    private void register() {
        if (!EventBus.getDefault().isRegistered(this)) {
            //注册EventBsus,注意参数是this，传入activity会报错
            EventBus.getDefault().register(this);
        }
    }

    private void unRegister() {
        if (EventBus.getDefault().isRegistered(this)) {
            //注册EventBus,注意参数是this，传入activity会报错
            EventBus.getDefault().unregister(this);
        }
    }

    //EventBus的处理事件函数  该方法可以随意取名 必须为public 必须添加注解并指定线程模型
    //之后EventBus会自动扫描到此函数，进行数据传递
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshRecycler(EventMessage eventMessage) {
        switch (eventMessage.message) {
            case Messages.UPDATE_UNIT:
                DepartmentDetailsBean.DataBean newData = (DepartmentDetailsBean.DataBean) eventMessage.t;

                dataBean = newData;

                setUnitInfo(newData);
                break;
        }
    }
}
