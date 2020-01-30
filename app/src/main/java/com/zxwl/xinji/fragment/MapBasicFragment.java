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

import com.chad.library.adapter.base.BaseQuickAdapter;
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
 * 村情概况
 * 党建阵地
 * 集体经济
 */
public class MapBasicFragment extends BaseLazyFragment {
    private FrameLayout flEdit;
    private TextView tvBasicLable;
    private TextView tvEditLable;
    private TextView tvBasic;
    private RecyclerView rvList;

    private ShowImageAdapter selectImageAdapter;
    private List<LocalMedia> selectListImage;

    public static final String UNIT_ID = "UNIT_ID";
    public static final String TITLE = "TITLE";

    //是否显示编辑按钮
    public static final String SHOW_EDIT = "SHOW_EDIT";

    public static final String TYPE_CQGK = "基本信息";
    public static final String TYPE_DJZD = "党建阵地";
    public static final String TYPE_JTJJ = "集体经济";

    private int unitId;
    private String title;

    //是否显示编辑按钮
    private boolean showEdit = false;

    private DepartmentDetailsBean.DataBean currentDetailsBean;
    private String requestUrl;

    public MapBasicFragment() {
    }

    public static MapBasicFragment newInstance(int unitId, boolean showUnit, String title) {
        MapBasicFragment fragment = new MapBasicFragment();
        Bundle args = new Bundle();
        args.putInt(UNIT_ID, unitId);
        args.putBoolean(SHOW_EDIT, showUnit);
        args.putString(TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View inflateContentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_map_basic, container, false);
    }

    @Override
    protected void findViews(View view) {
        flEdit = (FrameLayout) view.findViewById(R.id.fl_edit);
        tvBasicLable = (TextView) view.findViewById(R.id.tv_basic_lable);
        tvEditLable = (TextView) view.findViewById(R.id.tv_edit_lable);
        tvBasic = (TextView) view.findViewById(R.id.tv_basic);
        rvList = (RecyclerView) view.findViewById(R.id.rv_select);
    }

    @Override
    protected void initData() {
        unitId = getArguments().getInt(UNIT_ID);
        title = getArguments().getString(TITLE);
        showEdit = getArguments().getBoolean(SHOW_EDIT);

        flEdit.setVisibility(showEdit ? View.VISIBLE : View.GONE);

        initUrl();

        initRecycler();

        //获取基本信息
        queryJbxxDetail();

        register();
    }

    private void initUrl() {
        switch (title) {
            case TYPE_JTJJ:
                tvEditLable.setText("编辑集体经济");
                requestUrl = "collectiveEconomyAction_queryDetail.action";
                break;

            case TYPE_CQGK:
                tvEditLable.setText("编辑基本信息");
                requestUrl = "departmentInfoAction_queryDetail.action";
                break;

            default:
                tvEditLable.setText("编辑党建阵地");
                requestUrl = "departmentInfoAction_queryDetail.action";
                break;
        }
    }

    @Override
    protected void addListeners() {
        flEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditUnitInfoActivity.startActivity(getActivity(), currentDetailsBean, title);
            }
        });
    }

    /**
     * 查询基本信息详情
     */
    private void queryJbxxDetail() {
        HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryDepartmentDetail(requestUrl, unitId)
                .compose(this.bindToLifecycle())
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<DepartmentDetailsBean>() {
                    @Override
                    public void onSuccess(DepartmentDetailsBean detailsBean) {
                        if (BaseData.SUCCESS.equals(detailsBean.result)) {
                            DepartmentDetailsBean.DataBean newData = detailsBean.data;
                            if (null != newData) {
                                currentDetailsBean = newData;
                                //设置基本信息
                                setInfo(newData);
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

    /**
     * 设置基本信息
     *
     * @param newData
     */
    private void setInfo(DepartmentDetailsBean.DataBean newData) {
        switch (title) {
            case TYPE_CQGK:
                tvBasic.setText(newData.baseInfo);
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
                break;

            case TYPE_DJZD:
                tvBasic.setText(newData.unitInfo);
                getImageList(Arrays.asList(
                        newData.pic11,
                        newData.pic22,
                        newData.pic33,
                        newData.pic44,
                        newData.pic55,
                        newData.pic66,
                        newData.pic77,
                        newData.pic88,
                        newData.pic99
                ));
                break;

            case TYPE_JTJJ:
                tvBasic.setText(newData.title);
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
                break;
        }
        selectImageAdapter.replaceData(selectListImage);
    }

    /**
     * 获取图片列表
     */
    private void getImageList(List<String> imageUrls) {
        if (null == selectListImage) {
            selectListImage = new ArrayList<>();
        }
        selectListImage.clear();
        LocalMedia localMedia = null;
        for (String url : imageUrls) {
            if (!TextUtils.isEmpty(url)) {
                localMedia = new LocalMedia();
                localMedia.setPath(url);
                selectListImage.add(localMedia);
            }
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

                currentDetailsBean = newData;

                setInfo(newData);
                break;
        }
    }
}
